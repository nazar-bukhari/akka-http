package HighLevelServerAPI

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._ //needed for path and complete
import akka.http.scaladsl.server.Route

object HightLevelAPI extends App {

  implicit val system = ActorSystem(Behaviors.empty, "my-system") //Needed for route
  implicit val executionContext = system.executionContext

  val simpleRoute: Route = path("highlevelapi"){
    complete(StatusCodes.OK)
  }

  val getRoute = path("highlevelapi"){
    get{
      complete(StatusCodes.OK)
    }
  }

  val chainedRoute = path("chainedroute"){
    get{
      complete(StatusCodes.OK)
    } ~
    post{
      complete(StatusCodes.Forbidden)
    }
  }

  val pathExtractionRoute =
    path("api" / "item" / IntNumber) {
      ItemNumber: Int =>
        println(s"Got a number $ItemNumber")
        complete(StatusCodes.OK)
    }

  Http().newServerAt("localhost", 8082).bind(pathExtractionRoute)
}
