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

  amqSubscriber.subscribe("hej", (mess:String) =>
    received(mess))

  def received(mess:String): Unit = {
    val json:JValue = parse(mess)
    val messageType = json \ "isa"
    val messageData = json \ "data"

    println("\n\nInterpreter received:" + mess)
    println("message type: "+messageType+ "\n")

    publishEvents(messageType match {
      case JString("newLoad") => newReceived(messageData) // in production these patients should be ignored due to data incompleteness
      case JString("new")     => newReceived(messageData)
      case JString("diff")    => diffReceived(messageData)
      case JString("removed") => removedReceived(messageData)
      case _ => throw new IllegalArgumentException
    })
  }

  def newReceived(mess:JValue): List[EricaEvent] = {
    println(mess.extract[NewPatient])
    val patient = mess.extract[NewPatient].patient
    val timestamp = patient.CareContactRegistrationTime
    val id = patient.PatientId

    // create "registered" event
    val registered = List(EricaEvent(
      Type = "new_event",
      Title = "registered_subject",
      Value = patient.CareContactId.toString,
      Category = "new_registration",
      Start = getEpoch(patient.CareContactRegistrationTime),
      End =  getEpoch(patient.CareContactRegistrationTime),
      SubjectId = patient.PatientId
    ))

    // create one event for each elvisEvent
    val events = elvisEventTranslator(patient.Events, "new_event")

    // create one event for each elvis field
    val fields = Map(
      "DepartmentComment" ->     patient.DepartmentComment,
      "Location" ->              patient.Location,
      "ReasonForVisit" ->        patient.ReasonForVisit,
      "Team" ->                  patient.Team,
      "VisitRegistrationTime" -> patient.VisitRegistrationTime
    )

    val updates = (for (key <- fields.keys) yield
      translateUpdate("new_registration", timestamp, id, key, fields.get(key).get.toString)).toList

    registered ++ events ++ updates
  }

  def diffReceived(mess:JValue):  List[EricaEvent] = {
    val diff = mess.extract[PatientDiff]

    // create one event for each update
    val updates = elvisUpdateTranslator(diff.updates)

    // create one event_removed for each removedEvent
    val removedEvents = elvisEventTranslator(diff.removedEvents, "removed_event")

    // create one event for each elvisEvent
    val events = elvisEventTranslator(diff.newEvents, "new_event")

    updates ++ events ++ removedEvents
  }

  def elvisUpdateTranslator(updates:Map[String, JValue]): List[EricaEvent] = {
    val standardKeys = List("CareContactId", "PatientId", "timestamp")
    val changedKeys = updates.keys.filterNot(standardKeys.contains)

    val timestamp:DateTime = DateTime.parse(updates.get("timestamp").get.values.toString)
    val id:BigInt = updates.get("PatientId").get.asInstanceOf[JInt].values

    (for (key <- changedKeys) yield
      translateUpdate("elvis_update", timestamp, id, key, updates.get(key).get.values.toString)).toList
  }

  def translateUpdate(category:String, timestamp:DateTime, id:BigInt, key:String, value:String): EricaEvent = {
    EricaEvent(
      Type = "new_event",
      Title = key,
      Value = value,
      Category = category,
      Start = getEpoch(timestamp),
      End =  getEpoch(timestamp),
      SubjectId = id
    )
  }

  def removedReceived(mess:JValue):  List[EricaEvent] = {
    val removed = mess.extract[RemovedPatient]
    // only create the "removed" event
    List(EricaEvent(
      Type = "new_event",
      Title = "removed_subject",
      Value = removed.patient.CareContactId.toString,
      Category = "removed_subject",
      Start = getEpoch(removed.timestamp),
      End =  getEpoch(removed.timestamp),
      SubjectId = removed.patient.PatientId
    ))
  }

  def elvisEventTranslator(events:List[ElvisEvent], event_type:String): List[EricaEvent] = {
    events match {
      case x::Nil   => List(elvisEventToErica(events.head, event_type))
      case x::xs    => elvisEventToErica(x, event_type) :: elvisEventTranslator(xs, event_type)
      case List()   => List()
    }
  }

  def elvisEventToErica(event:ElvisEvent, event_type:String): EricaEvent = {
    event.Category match {
      case "P" => EricaEvent( // special case: priority events are strangely formatted by default
        Type = event_type,
        Title = "priority",
        Value = event.Value,
        Category = event.Category,
        Start = getEpoch(event.Start),
        End =  getEpoch(event.End),
        SubjectId = event.CareEventId
      )
      case s:String => EricaEvent( // general case: event is essentially unchanged
        Type = event_type,
        Title = event.Title,
        Value = event.Value,
        Category = event.Category,
        Start = getEpoch(event.Start),
        End =  getEpoch(event.End),
        SubjectId = event.CareEventId
      )
    }
  }

  /**
    * Converts a DateTime to the more agreeable epoch format
    */
  def getEpoch(timestamp:DateTime): BigInt = {
    timestamp.getMillis
  }

  def publishEvents(events: List[EricaEvent]): Unit = {
    println("\npublishing events...")
    events.foreach( e => println(e))
  }
}
