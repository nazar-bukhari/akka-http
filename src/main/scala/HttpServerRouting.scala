import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.javadsl.server.Route
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._

import java.util.concurrent.Future
import scala.concurrent.Future
import scala.io.StdIn

object HttpServerRouting {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system") //Needed for route
    implicit val executionContext = system.executionContext

    final case class Item (name: String, id:Long)
    final case class Orders(items: List[Item])

    var result: List[Item] = Nil

    def saveOrder(order: Orders): Future[Done] = {
      result = order match {
        case Orders(items) => items ::: result
        case _ => result
      }
      Future(Done)
    }

    def getOrder(itemId: Long): Future[Option[Item]] = Future {
      result.find(item => item.id == itemId)
    }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}
