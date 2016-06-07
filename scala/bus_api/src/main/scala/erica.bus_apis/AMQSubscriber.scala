package erica.bus_api

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.codemettle.reactivemq.ReActiveMQExtension
import com.codemettle.reactivemq.ReActiveMQMessages._
import com.codemettle.reactivemq.model.{Topic, AMQMessage}
import erica.config.Config

case object Connect

class AMQSubscriber {

  implicit val system = ActorSystem()

  def subscribe(topic: String, onMsg: (String) => Unit) {
    val actor = system.actorOf(Props.create(classOf[SubscribeActor], topic, onMsg))
    actor ! Connect
  }
}

private class SubscribeActor(consumedTopic: String, onMSG: (String) => Unit) extends Actor {
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
      c ! ConsumeFromTopic(consumedTopic)
      println("cons:" + c)
      theBus = Some(c)
      println("bus:" + theBus)
    }
    case ConnectionFailed(request, reason) => {
      println("failed:" + reason)
    }
    case mess @ AMQMessage(body, prop, headers) => {
      println(body)
      onMSG(body.toString)
    }
  }
}
