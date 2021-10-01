package ActorSystem

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{Behaviors, LoggerOps}

object HelloWorldBot {

  def apply(max: Int): Behavior[HelloWorld.Greeted] = {
    bot(0, max)
  }

  private def bot(greetingCounter: Int, max: Int): Behavior[HelloWorld.Greeted] =
    Behaviors.receive{ (context, message) =>
      val counter = greetingCounter + 1;
      println(s"Greeting $counter for ${message.whom}")
      if(counter == max){
        Behaviors.stopped
      }else{
        message.from ! HelloWorld.Greet(message.whom, context.self)
        bot(counter, max)
      }
    }
}
