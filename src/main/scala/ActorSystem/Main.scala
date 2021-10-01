package ActorSystem

import akka.actor.typed.ActorSystem

object Main {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem[HelloWorldMain.SayHello] =
      ActorSystem(HelloWorldMain(), "hello")
      system ! HelloWorldMain.SayHello("World")
      system ! HelloWorldMain.SayHello("Akka")
    }
}
