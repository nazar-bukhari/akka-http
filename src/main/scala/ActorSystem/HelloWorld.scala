package ActorSystem

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object HelloWorld {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive{ (context, message) =>

    println(s"Hello ${message.whom} !!")
    message.replyTo ! Greeted(message.whom, context.self)
    Behaviors.same
  }
}
