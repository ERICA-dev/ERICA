package erica.services.common

import com.github.nscala_time.time.Imports._
import org.json4s.JsonAST.JValue
import org.json4s._

/**
  * Created by marp on 2016-06-07.
  */

case class EricaEvent(
  Type:       String,
  Title:      String,
  Value:      String,
  Category:   String,
  Start:      BigInt,
  End:        BigInt,
  SubjectId:  BigInt
)

case class ElvisPatient(
  CareContactId: Int,
  CareContactRegistrationTime: DateTime,
  DepartmentComment: String,
  Events: List[ElvisEvent],
  Location: String,
  PatientId: Int,
  ReasonForVisit: String,
  Team: String,
  VisitId: Int,
  VisitRegistrationTime: DateTime
)

case class ElvisEvent(
  CareEventId: Int,
  Category: String,
  End: DateTime,
  Start: DateTime,
  Title: String,
  Type: String,
  Value: String,
  VisitId: Int
 )

case class PatientDiff(updates: Map[String, JValue], newEvents: List[ElvisEvent], removedEvents: List[ElvisEvent])
case class RemovedPatient(timestamp: DateTime, patient: ElvisPatient)
case class NewPatient(timestamp: DateTime, patient: ElvisPatient)


// General format of elasticsearch responses
// Actual results are located in Hits.hits
case class ElasticQuery (
  took: BigInt,
  timed_out: Boolean,
  _shards: Shards,
  hits: Hits
)

case class Shards (
  total: BigInt,
  successful: BigInt,
  failed: BigInt
)

case class Hits (
  total: BigInt,
  max_score: Double,
  hits: List[JValue]
)

case class ElasticError (
  error: Error,
  status: BigInt
)

case class Error (
  root_cause: List[JValue],
  `type`: String,
  reason:String,
  resource:List[String],
  index: String
)

case class SavedMessage (
                        timestamp: BigInt,
                        data:JValue
                        )

