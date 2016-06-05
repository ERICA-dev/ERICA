package service

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.codemettle.reactivemq.ReActiveMQExtension
import com.codemettle.reactivemq.ReActiveMQMessages._
import com.codemettle.reactivemq.model.{Topic, AMQMessage}
import com.github.nscala_time.time.Imports._

class AMQService {
  
  def subscribe(topic: String, onMsg: () => Unit) {
    val msgResponder = new MsgResponder(onMsg)
    val transformActor = system.actorOf(Props.create(classOf[AMQActor], topic, msgResponder))
    transformActor ! "connect"
  }

  def publish(topic: String, data: String) {
    val publishActor = system.actorOf(Props.create(classOf[PublishActor]))
    publishActor ! ("publish", topic, data)
  }

  // activeMQ-things
  implicit val system = ActorSystem()
  // implicit val executor = system.dispatcher
  // implicit val materializer = ActorMaterializer()
  // val logger = Logging(system, "StatisticsService")
}

class MsgResponder(onMsgParam: () => Unit) {
  def onMsg = onMsgParam
}

class AMQActor(consumedTopic: String, msgResponder: MsgResponder) extends Actor {
  // implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all // for json serialization

  var theBus: Option[ActorRef] = None

  def receive = {
    case "connect" => {
      ReActiveMQExtension(context.system).manager ! GetAuthenticatedConnection(s"nio://localhost:61616", "admin", "admin")
    }
    case ConnectionEstablished(request, c) => {
      println("connected:" + request)
      c ! ConsumeFromTopic(consumedTopic)
      println("cons:" + c)
      theBus = Some(c)
      println("bus:" + theBus)
      // publish("hej")
    }
    case ConnectionFailed(request, reason) => {
      println("failed:" + reason)
    }
    case mess @ AMQMessage(body, prop, headers) => {
      println(body)
      msgResponder.onMsg()
    }
  }
}

class PublishActor() extends Actor {
  var theBus: Option[ActorRef] = None

  def receive = {
    case "connect" => {
      ReActiveMQExtension(context.system).manager ! GetAuthenticatedConnection(s"nio://localhost:61616", "admin", "admin")
    }
    case ConnectionEstablished(request, c) => {
      // println("connected:" + request)
      // c ! ConsumeFromTopic(consumedTopic)
      // println("cons:" + c)
      // theBus = Some(c)
      // println("bus:" + theBus)
    }
    case ConnectionFailed(request, reason) => {
      println("failed:" + reason)
    }
    case ("publish", topic: String, msg: String) => {
      println(msg)
      theBus.foreach{bus => bus ! SendMessage(Topic(topic), AMQMessage(msg))}
    }
  }

  // def publish(topic: String, msg: String) = {
  //   theBus.foreach{bus => bus ! SendMessage(Topic(topic), AMQMessage(msg))}
  // }
}
