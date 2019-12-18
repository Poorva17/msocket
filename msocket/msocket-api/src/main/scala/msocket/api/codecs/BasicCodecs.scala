package msocket.api.codecs

import java.util.concurrent.TimeUnit

import akka.Done
import akka.util.Timeout
import io.bullet.borer.derivation.CompactMapBasedCodecs
import io.bullet.borer.{Codec, Decoder, Encoder}
import msocket.api.models.Result

import scala.concurrent.duration.FiniteDuration

object BasicCodecs extends BasicCodecs
trait BasicCodecs {
  implicit def eitherCodec[E: Encoder: Decoder, S: Encoder: Decoder]: Codec[Either[E, S]] =
    Codec.of[Result[S, E]].bimap(Result.fromEither, _.toEither)

  implicit lazy val doneCodec: Codec[Done] = Codec.bimap[String, Done](_ => "done", _ => Done)

  implicit lazy val durationCodec: Codec[FiniteDuration] = Codec.bimap[(Long, String), FiniteDuration](
    finiteDuration => (finiteDuration.length, finiteDuration.unit.toString),
    { case (length, unitStr) => FiniteDuration(length, TimeUnit.valueOf(unitStr)) }
  )

  implicit lazy val timeoutCodec: Codec[Timeout] = CompactMapBasedCodecs.deriveCodec
}
