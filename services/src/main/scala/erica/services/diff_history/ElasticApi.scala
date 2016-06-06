package erica.services.diff_history

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write
import wabisabi.Client
import scala.concurrent.ExecutionContext.Implicits.global // Client.get gets sad without this
import erica.config.Config

object ElasticApi {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all // json4s needs this for something

  val ip = Config.get("elastic_ip")
  val port = Config.get("elastic_port")

  println("ElasticApi attempting to connect to elasticsearch on address: " + ip + ":" + port )
  val client = new Client("http://$ip:$port") // creates a wabisabi client for communication with elasticsearch

  def saveDiff(diff: JValue) {
    val targetIndex = getIndex()

    client.index(
      index = targetIndex,
      `type` = "type",
      id = Some("test_id"),
      data = write(diff),
      refresh = true
    )
  }

  /**
    this should return the sequence numer of the event
   */
  def getIndex(): String = {
    "test_index"
  }

  def getPatientFromElastic(index: String, careContactId: String): JValue ={
    val oldPatientQuery = client.get(index, "PATIENT_TYPE", careContactId).map(_.getResponseBody) //fetch patient from database
    while (!oldPatientQuery.isCompleted) {} // patiently wait for response from the database. //TODO at some point add timeout. It could get stuck here forever (but probably not). Update: it has not happened for 60 days
    val oldPatient:JValue = parse(oldPatientQuery.value.get.get) // unpack the string and cast to json-map

    println("Retrieved patient: " +oldPatient \ "_source")
    return oldPatient \ "_source" // The good stuff is located in _source.
  }

  def search(index:String, query:String): JValue ={
    val search = client.search(index, query).map(_.getResponseBody)
    while(!search.isCompleted){}
    parse(search.value.get.get) // unpack the string and cast to json-map
  }
}
