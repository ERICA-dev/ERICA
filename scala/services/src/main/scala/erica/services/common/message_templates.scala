package erica.services.common

import com.github.nscala_time.time.Imports._
import org.json4s.JsonAST.JValue
import org.json4s._

/**
  * Created by marp on 2016-06-07.
  */

case class EricaEvent(Title: String,
                      Timestamp: Long,
                      SubjectId: Long,
                      Data: Map[String, JValue]
                     )

case class ElvisPatient(CareContactId: Int,
                        CareContactRegistrationTime: DateTime,
                        DepartmentComment: String,
                        Events: List[ElvisEvent],
                        Location: String,
                        PatientId: Int,
                        ReasonForVisit: String,
                        Team: String,
                        VisitId: Int,
                        VisitRegistrationTime: DateTime)

case class ElvisEvent(CareEventId: Int,
                      Category: String,
                      End: DateTime,
                      Start: DateTime,
                      Title: String,
                      Type: String,
                      Value: String,
                      VisitId: Int)

case class PatientDiff(updates: Map[String, JValue], newEvents: List[ElvisEvent], removedEvents: List[ElvisEvent])
case class RemovedPatient(timestamp: DateTime, patient: ElvisPatient)
case class NewPatient(timestamp: DateTime, patient: ElvisPatient)