package HighLevelServerAPI

import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.ActorMaterializer

case class Player(nickName: String, characterClass: String, level: Int)

object GameAreaMap { //Companion Object
  case object GetAllPlayers
  case class GetPlayer(nickName: String)
  case class GetPlayerByClass(characterClass: String)
  case class AddPlayer(player: Player)
  case class RemovePlayer(player: Player)
  case object OperationSuccess
}

class GameAreaMap extends Actor with ActorLogging{
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

  implicit val system = ActorSystem(Behaviors.empty, "my-system") //Needed for route
  implicit val executionContext = system.executionContext
  import system.dispatchers

  val gameMap = system.systemActorOf(Props[GameAreaMap],"GameAreaMap")
}
