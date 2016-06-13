package prediction_diff_producer

import bus_api.{AMQPublisher, AMQSubscriber}
import json_handling.json_handler._

case class EricaEvent (
  Type: String,
  Title: String,
  Value: String,
  Category: String,
  Start: String,
  End: String,
  SubjectId: String
)

case class PredictionFeature (
  Feature: String,
  Change: String
)


object PredictionDiffProducer extends App {

  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher

  private def publish(feature: PredictionFeature): Unit = {
    amqPublisher.publish("PredictionFeatures", toJsonString(feature))
  }
  private def plusFeature(name: String): PredictionFeature = {
    PredictionFeature(Feature = name, Change = "+")
  }
  private def minusFeature(name: String): PredictionFeature = {
    PredictionFeature(Feature = name, Change = "-")
  }
  private def plus(name: String): Unit = {
    publish(plusFeature(name))
  }
  private def minus(name: String): Unit = {
    publish(minusFeature(name))
  }


  amqSubscriber.subscribe("EricaEvents", (mess: String) => {

    val ericaEvent = toCaseClass[EricaEvent](mess)

    ericaEvent.Type match {
      case "Arrival" => {
        // publish(plusFeature("Queue")); publish(plusFeature("Untriaged"))
        plus("Queue"); plus("Untriaged")
      }
      case "Triage" => {
        // publish(minusFeature("Queue")); publish(minusFeature("Untriaged"))
        minus("Queue"); minus("Untriaged")
      }
      case _ => throw new IllegalArgumentException
    }
  })
}
