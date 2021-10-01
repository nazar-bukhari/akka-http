import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.Uri.Path
import akka.stream.scaladsl.Source
import akka.http.scaladsl.server.Directives._
import akka.util.ByteString

import scala.io.StdIn
import scala.util.Random

object HttpServerStreamingRandomNumbers {
  def main(args: Array[String]): Unit = {
      implicit val system = ActorSystem(Behaviors.empty, "RandomNumberStream")
      implicit val executionContext = system.executionContext

    val numbers = Source.fromIterator(() =>
        Iterator.continually(Random.nextInt()))

    val route = path("random") {
      get {
        complete(
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            numbers.map(n => ByteString(s"$n\n"))
          )
        )
      }
    }
//    streaming requests means that the server decides how fast the remote client can push the data of the request body.
//    curl --limit-rate 50b 127.0.0.1:8080/random

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    StdIn.readLine()
    bindingFuture.
      flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
