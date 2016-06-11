package erica.bus_api

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.codemettle.reactivemq.ReActiveMQExtension
import com.codemettle.reactivemq.ReActiveMQMessages._
import com.codemettle.reactivemq.model.{Topic, AMQMessage}
import com.github.nscala_time.time.Imports._
import erica.config.Config

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

  val ip = Config.get("bus_ip")
  val port = Config.get("bus_port")
  val login = Config.get("bus_login")
  val pass = Config.get("bus_pass")

  def receive = {
    case Connect => {
      ReActiveMQExtension(context.system).manager ! GetAuthenticatedConnection(s"nio://$ip:$port", login, pass)
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
      //println("AMQPublisher sent message: "+msg)
      theBus.foreach{bus => bus ! SendMessage(Topic(topic), AMQMessage(msg))}
    }
  }
}
