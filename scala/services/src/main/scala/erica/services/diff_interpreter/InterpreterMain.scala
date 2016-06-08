package erica.services.diff_interpreter

import erica.bus_api.AMQSubscriber
import erica.bus_api.AMQPublisher
import erica.services.common._
import org.joda.time.DateTime
import org.json4s._
import org.json4s.native.JsonMethods._

/**
  * Created by marp on 2016-06-06.
  */
object InterpreterMain extends App {
  new Interpreter
}

class Interpreter {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all // json4s needs this for something

  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher

  def myFunc() { println("hej") }

  amqSubscriber.subscribe("hej", (mess:String) =>
    received(mess))

  def received(mess:String): Unit = {
    val json:JValue = parse(mess)
    val messageType = json \ "isa"
    val messageData = json \ "data"

    println("\n\nInterpreter received:" + mess)
    println("message type: "+messageType+ "\n")

    messageType match {
      case JString("newLoad") => newReceived(messageData) // in production these patients should be ignored due to data incompleteness
      case JString("new")     => newReceived(messageData)
      case JString("diff")    => diffReceived(messageData)
      case JString("removed") => removedReceived(messageData)
      case _ => throw new IllegalArgumentException
    }
  }

  def newReceived(mess:JValue): Unit = {
    println(mess.extract[NewPatient])
    val patient = mess.extract[NewPatient].patient

    // create "registered" event
    val registered = EricaEvent(Title = "registered",
                                Timestamp = getEpoch(patient.CareContactRegistrationTime),
                                SubjectId = patient.PatientId,
                                Data = Map("asd" -> JInt(2))
                              )

    // create one event for each elvis field

    // create one event for each elvisEvent
  }

  def diffReceived(mess:JValue): Unit = {
    println(mess.extract[PatientDiff])
    // create one event for each update

    // create one event_reverted for each removedEvent

    // create one event for each elvisEvent
  }

  def removedReceived(mess:JValue): Unit = {
    println(mess.extract[RemovedPatient])
    // only create the "removed" event
  }

  def elvisEventTranslator(events:List[ElvisEvent]): List[EricaEvent] ={
    null
  }

  /**
    * Converts a DateTime to the more agreeable epoch format
    */
  def getEpoch(timestamp:DateTime): Long = {
    5
  }

  def publishEvents(events: List[EricaEvent]): Unit = {
    // foreach event publish it
  }
}
