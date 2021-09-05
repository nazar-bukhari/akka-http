import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn

import scala.concurrent.Future

object HttpServerRouting {

  implicit val system = ActorSystem(Behaviors.empty, "my-system") //Needed for route
  implicit val executionContext = system.executionContext

  final case class Item(name: String, id: Long)
  final case class Order(items: List[Item])

  implicit val itemFormat = jsonFormat2(Item)
  implicit val orderFormat = jsonFormat1(Order)

  var result: List[Item] = Nil

  def saveOrder(order: Order): Future[Done] = {
    result = order match {
      case Order(items) => items ::: result
      case _ => result
    }
    Future(Done)
  }

  def getOrder(itemId: Long): Future[Option[Item]] = Future {
    result.find(item => item.id == itemId)
  }

  def main(args: Array[String]): Unit = {
    val route: Route =
      concat(
        get {
          pathPrefix("item" / LongNumber){ id =>
            val optionalItem: Future[Option[Item]] = getOrder(id)
            onSuccess(optionalItem){
              case Some(item) => complete(item)
              case _ => complete(StatusCodes.NotFound)
            }
//            complete(optionalItem)
          }
        },
        get{
          path("healthcheck"){
            complete("Ok")
          }
        },
        post {
          path("create-order") {
            entity(as[Order]) { order =>
              val saved: Future[Done] = saveOrder(order)
              onSuccess(saved) { _ =>
                complete("order created")
              }
            }
          }
        }
      )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
