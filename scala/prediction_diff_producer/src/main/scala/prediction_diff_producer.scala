package erica.services

import erica.bus_api.AMQSubscriber
import erica.bus_api.AMQPublisher
import org.json4s._
import org.json4s.native.JsonMethods._

object PredictionDiffProducer extends App {

  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher

  amqSubscriber.subscribe("EricaEvents", (mess: String) => {
      println(mess)
  })
}
