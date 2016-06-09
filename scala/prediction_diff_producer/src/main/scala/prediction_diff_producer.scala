package erica.services

import erica.bus_api.AMQSubscriber
import erica.bus_api.AMQPublisher
import org.json4s._
import org.json4s.native.JsonMethods._

object PredictionDiffProducer extends App {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all // json4s needs this for something

  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher

  val QUEUE_INCREMENT = "{\"Queue\": \"Increment\"}"

  amqSubscriber.subscribe("EricaEvents", (mess: String) => {
    val json: JValue = parse(mess)
    val messageTitle = json \ "Title"
    println(messageTitle)

    messageTitle match {
      case JString("Arrival") => amqPublisher.publish("PredictionFeatures", QUEUE_INCREMENT)
      case _ => throw new IllegalArgumentException
    }
  })
}
