package service

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.codemettle.reactivemq.ReActiveMQExtension
import com.codemettle.reactivemq.ReActiveMQMessages._
import com.codemettle.reactivemq.model.{Topic, AMQMessage}
import com.github.nscala_time.time.Imports._

case object Connect

class AMQSubscriber {

  implicit val system = ActorSystem()
  
  def subscribe(topic: String, onMsg: () => Unit) {
    val actor = system.actorOf(Props.create(classOf[SubscribeActor], topic, onMsg))
    actor ! Connect
  }

  // något värt att ha alls av dessa kommenterade?
  // activeMQ-things
  // implicit val executor = system.dispatcher
  // implicit val materializer = ActorMaterializer()
  // val logger = Logging(system, "StatisticsService")
}

private class SubscribeActor(consumedTopic: String, onMSG: () => Unit) extends Actor {
  // implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all // for json serialization

  var theBus: Option[ActorRef] = None

  def receive = {
    case Connect => {
      ReActiveMQExtension(context.system).manager ! GetAuthenticatedConnection(s"nio://localhost:61616", "admin", "admin")
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
      onMSG()
    }
  }
}
