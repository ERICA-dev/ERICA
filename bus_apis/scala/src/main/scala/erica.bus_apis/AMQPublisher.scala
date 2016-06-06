package erica.bus_apis

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.codemettle.reactivemq.ReActiveMQExtension
import com.codemettle.reactivemq.ReActiveMQMessages._
import com.codemettle.reactivemq.model.{Topic, AMQMessage}
import com.github.nscala_time.time.Imports._

class AMQPublisher {

  implicit val system = ActorSystem()

  val actor = system.actorOf(Props.create(classOf[PublishActor]))
  actor ! Connect

  def publish(topic: String, msg: String) {
    actor ! ("publish", topic, msg)
  }
}

private class PublishActor() extends Actor {

  var theBus: Option[ActorRef] = None

  def receive = {
    case Connect => {
      ReActiveMQExtension(context.system).manager ! GetAuthenticatedConnection(s"nio://localhost:61616", "admin", "admin")
    }
    case ConnectionEstablished(request, c) => {
      println("connected:" + request)
      theBus = Some(c)
      println("bus:" + theBus)
    }
    case ConnectionFailed(request, reason) => {
      println("failed:" + reason)
    }
    // TODO göra så att denna väntar på connect? som det är nu så händer ingetalls om man är för snabb att publisha tror jag
    case ("publish", topic: String, msg: String) => {
      println(msg)
      theBus.foreach{bus => bus ! SendMessage(Topic(topic), AMQMessage(msg))}
    }
  }
}
