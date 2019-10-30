package msocket.impl.sse

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
import akka.stream.scaladsl.{Sink, Source}
import io.bullet.borer.{Decoder, Encoder, Json}
import msocket.impl.StreamSplitter._
import msocket.api.Transport
import msocket.api.models.{HttpException, Result, StreamError, StreamStatus}

import scala.concurrent.{ExecutionContext, Future}

class SseTransport[Req: Encoder](uri: String)(implicit actorSystem: ActorSystem) extends Transport[Req] {

  implicit val ec: ExecutionContext = actorSystem.dispatcher

  override def requestResponse[Res: Decoder](request: Req): Future[Res] = {
    requestResponseWithDelay(request)
  }

  override def requestResponseWithDelay[Res: Decoder](request: Req): Future[Res] = {
    requestStream(request).runWith(Sink.head)
  }

  override def requestStream[Res: Decoder](request: Req): Source[Res, NotUsed] = {
    val futureSource = getResponse(request).flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
    Source
      .futureSource(futureSource)
      .map(event => Json.decode(event.data.getBytes()).to[Res].value)
      .mapMaterializedValue(_ => NotUsed)
  }

  override def requestStreamWithStatus[Res: Decoder](request: Req): Source[Res, Future[StreamStatus]] = {
    requestStream[Result[Res, StreamError]](request).split
  }

  private def getResponse(request: Req): Future[HttpResponse] = {
    val payloadHeader = QueryHeader(Json.encode(request).toUtf8String)
    val httpRequest   = HttpRequest(HttpMethods.GET, uri = uri, headers = List(payloadHeader))
    Http().singleRequest(httpRequest).flatMap { response =>
      response.status match {
        case StatusCodes.OK => Future.successful(response)
        case statusCode     => Unmarshal(response).to[String].map(msg => throw HttpException(statusCode.intValue(), statusCode.reason(), msg))
      }
    }
  }

}