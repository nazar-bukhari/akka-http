import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object HttpClientAPI {
  def main(args: Array[String]): Unit = {
    //Behaviors.empty --> behavior that treats every incoming message as unhandled.
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "SingleRequest")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://agoda.com"))

    responseFuture.onComplete{
      case Success(res) => println(res)
      case Failure(_) => sys.error("Something west wrong")
    }
  }
}
