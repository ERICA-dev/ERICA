package bus_replayer

import elastic_api._
import java.util.concurrent.atomic.AtomicLong
import bus_api.{ReplayRequest, AMQPublisher, AMQSubscriber}
import org.json4s.native.Serialization.write
import org.json4s._
import org.json4s.native.JsonMethods._

/**
  * Created by marp on 2016-06-11.
  */

class Replayer(topic: String) {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all // json4s needs this for something

  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher

  amqSubscriber.subscribe("replay_request", (mess:String) =>
    requestReceived(mess))

  def requestReceived(mess:String): Unit ={
    val request = parse(mess).extract[ReplayRequest]
  }
}
