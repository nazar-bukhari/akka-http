package HighLevelServerAPI

import HighLevelServerAPI.GameAreaMap.{AddPlayer, GetPlayerByClass}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._
import scala.concurrent.duration._
import scala.language.postfixOps

case class Player(nickName: String, characterClass: String, level: Int)

object GameAreaMap { //Companion Object
  case object GetAllPlayers

  case class GetPlayer(nickName: String)

  case class GetPlayerByClass(characterClass: String)

  case class AddPlayer(player: Player)

  case class RemovePlayer(player: Player)

  case object OperationSuccess
}

class GameAreaMap extends Actor with ActorLogging {

  import GameAreaMap._

  var players = Map[String, Player]()

  override def receive: Receive = { // Just like overriding runnable method in java for multithreading
    case GetAllPlayers =>
      log.info("Get All Players")
      sender() ! players.values.toList
    case GetPlayer(nickName) =>
      log.info(s"Getting Player with nickName: $nickName")
      sender() ! players.get(nickName)
    case GetPlayerByClass(characterClass) =>
      log.info(s"Getting Player with characterClass: $characterClass")
      sender() ! players.values.toList.filter(_.characterClass == characterClass)
    case AddPlayer(player) =>
      log.info("Adding Player")
      players = players + (player.nickName -> player)
      sender() ! OperationSuccess
    case RemovePlayer(player) =>
      log.info("Removing Player")
      players = players - player.nickName
  }
}

object MarshallingJSON extends App {

  implicit val system = ActorSystem("my-system")

  import system.dispatchers

  val gameMap = system.actorOf(Props[GameAreaMap], "GameAreaMap")
  val playerList = List(
    Player("Nazar", "C1", 10),
    Player("Bukhari", "C2", 7),
    Player("Alex", "C3", 5)
  )

  playerList.foreach(player =>
    gameMap ! AddPlayer(player)
  )

//  GET /api/player
//  GET /api/player/(nickname)
//  GET /api/player?nickname=x
//  GET /api/player/class/(characterclass)
//  POST /api/player
//  DELETE /api/player

  implicit val timeout = Timeout(2 seconds)
  implicit val PlayerFormat = jsonFormat3(Player)

  val route =
    pathPrefix("api" / "player") {
      get {
        (path(Segment) | parameter(Symbol("nickname")) ){ nickname =>
          reject
        } ~
        path("class" / Segment){ characterclass =>
          reject
        } ~
        pathEndOrSingleSlash{
           //GET All Players
            reject
         }
      } ~
        post {
          reject
        } ~
        delete {
          reject
        }
    }
}
