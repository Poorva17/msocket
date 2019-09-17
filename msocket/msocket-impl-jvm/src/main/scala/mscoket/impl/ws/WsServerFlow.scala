package mscoket.impl.ws

import akka.NotUsed
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Source}
import io.bullet.borer.Decoder
import mscoket.impl.ws.Encoding.JsonText
import msocket.api.MessageHandler

class WsServerFlow[T: Decoder](websocketClient: MessageHandler[T, Source[Message, NotUsed]]) {

  val flow: Flow[Message, Message, NotUsed] = {
    Flow[Message]
      .collect {
        case TextMessage.Strict(text) => JsonText.decodeText(text)
      }
      .flatMapConcat(websocketClient.handle)
  }
}
