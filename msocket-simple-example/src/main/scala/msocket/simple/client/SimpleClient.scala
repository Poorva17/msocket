package msocket.simple.client

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import csw.simple.api.RequestProtocol._
import csw.simple.api.{RequestProtocol, SimpleApi}
import msocket.core.client.ClientSocket

import scala.concurrent.Future

class SimpleClient(socket: ClientSocket[RequestProtocol])(implicit mat: Materializer) extends SimpleApi {
  override def hello(name: String): Future[String] = socket.requestResponse[String](Hello(name))
  override def square(number: Int): Future[Int]    = socket.requestResponse[Int](Square(number))

  override def getNames(size: Int): Source[String, NotUsed] = socket.requestStream[String](GetNames(size))

  override def getNumbers(divisibleBy: Int): Source[Int, Future[Option[String]]] = {
    socket.requestStreamWithError[Int, String](GetNumbers(divisibleBy))
  }
}
