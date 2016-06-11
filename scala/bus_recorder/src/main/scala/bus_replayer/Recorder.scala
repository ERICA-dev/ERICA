package bus_replayer

import elastic_api._
import java.util.concurrent.atomic.AtomicLong
import bus_api.AMQSubscriber
import org.json4s.native.Serialization.write
import org.json4s._
import org.json4s.native.JsonMethods._

/**
  * Created by marp on 2016-06-11.
  */

class Recorder(topic: String) {
  println("fetching current sequence number...")
  val sequenceNumber = new AtomicLong(0)

  val amqSubscriber = new AMQSubscriber

  ElasticApi.countIndex(index = topic, callback = initSequenceNumber)

  def nextSequenceNumber: Long = sequenceNumber.getAndIncrement()

  def initSequenceNumber(search:Object): Unit = {
    println(search)
    search match {
      case found:ElasticQuery => sequenceNumber.set(found.hits.total.longValue())
      case fail:ElasticError => sequenceNumber.set(0)
    }
    println("current sequence number: "+sequenceNumber.get())
    begin()
  }

  def begin() : Unit = {
    amqSubscriber.subscribe(topic, (mess:String) => received(mess))
    println("Recorder is running.")
  }

  def received(mess:String): Unit = {
    implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all
    val data = parse(mess)
    val json = SavedMessage(
      timestamp = System.currentTimeMillis,
      data = data
    )

    println("\nindexing to "+topic +":\n"+write(json))

    ElasticApi.indexObject(
      index = topic,
      `type`="history",
      id = nextSequenceNumber.toString,
      data = write(json)
    )
  }
}
