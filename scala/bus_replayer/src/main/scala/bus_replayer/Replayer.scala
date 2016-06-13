package bus_replayer

import elastic_api.{SavedMessage, ElasticApi, ElasticQuery}
import bus_api.{ReplayRequest, ReplayResponse, AMQPublisher, AMQSubscriber}
import json_handling.json_handler._
/**
  * Created by marp on 2016-06-11.
  */

class Replayer(topic: String) {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all // json4s needs this for something

  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher

  amqSubscriber.subscribe("replay_request", (mess:String) =>
    requestReceived(mess))

  def requestReceived(mess:String): Unit = {
    try {
      val request = toCaseClass[ReplayRequest](mess)
      handleRequest(new OnGoingReplay(request))  //TODO consider the risk of memory leak here; how JVM is the JVM?
    } catch {
      case e:IllegalArgumentException =>
        println("Replayer received an incorrect request: "+mess)
    }
  }

  def handleRequest(replay:OnGoingReplay): Unit = {
    query(replay.request.Topic, replay.request.FromTime, replay.sendToBus)
  }

  def query(topic:String, fromTime:BigInt, sendToBus: (Object) => Unit): Unit = {
    val size = 10000
    val query =s"""
    {
      "size":$size,
      "query": {
        "range":{
          "timestamp": {
            "gte": $fromTime
          }
        }
      }
    }"""
    ElasticApi.threadedSearch(topic, query, sendToBus)
  }
  class OnGoingReplay(replayRequest: ReplayRequest) {
    val request = replayRequest

    def sendToBus(messages:Object): Unit = {
      messages match {
        case res:ElasticQuery =>
          println("sending to topic "+replayRequest.Topic)

          val hits = res.hits.hits.sortBy(_._id.toLong)  // because the id is also the sequence number of the event!
          amqPublisher.publish(request.ResponseTopic, toJsonString(
            new ReplayResponse(
              EventCount = hits.length,
              Success = true
            )))
          val caseHits = hits.map( h => toCaseClass[SavedMessage](h._source))
          caseHits.foreach(h => amqPublisher.publish(request.ResponseTopic, toJsonString(h)))
        case _ => println("something was goofed")
      }
    }
  }
}

/*
{
  "Topic":"erica_event",
  "ResponseTopic":"banans",
  "FromTime":10
}
*/