package erica.services.diff_history

import erica.services.common.{ElasticError, ElasticQuery}
import org.json4s._
import org.json4s.native.JsonMethods._
import wabisabi.Client
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

// Client.get gets sad without this
import erica.config.Config

object ElasticApi {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  val ip = Config.get("elastic_ip")
  val port = Config.get("elastic_port")

  println("ElasticApi attempting to connect to elasticsearch on address: " + ip + ":" + port )
  val client = new Client(s"http://$ip:$port") // creates a wabisabi client for communication with elasticsearch

  def indexObject(index:String, `type`:String, id:String, data:String) {
    client.index(
      index = index,
      `type` = `type`,
      id = Some(id),
      data = data,
      refresh = true  //TODO check what this does
    )
  }

  /**
    * Counts how many objects are stored on the given index. This operation should be reasonably fast
    */
  def countIndex(index:String, callback: (Object) => Unit): Unit = {
    val query =
      """
      {
        "size":0,
        "query": {
          "match_all":{}
        }
      }
      """
    threadedSearch(index, query, callback)
  }

  //TODO fix this
  def getPatientFromElastic(index: String, careContactId: String): JValue ={
    val oldPatientQuery = client.get(index, "PATIENT_TYPE", careContactId).map(_.getResponseBody) //fetch patient from database
    while (!oldPatientQuery.isCompleted) {} // patiently wait for response from the database. //TODO at some point add timeout. It could get stuck here forever (but probably not). Update: it has not happened for 60 days
    val oldPatient:JValue = parse(oldPatientQuery.value.get.get) // unpack the string and cast to json-map

    println("Retrieved patient: " +oldPatient \ "_source")
    oldPatient \ "_source" // The good stuff is located in _source.
  }

  //TODO fix this
  def search(index:String, query:String): JValue ={
    val search = client.search(index, query).map(_.getResponseBody)
    while(!search.isCompleted){}
    parse(search.value.get.get) // unpack the string and cast to json-map
  }

  def threadedSearch(index:String, query:String, callback:(Object) => Unit): Unit ={
    val search = client.search(index, query).map(_.getResponseBody)
    search onComplete {
      case Success(s:String) => {
        val json = parse(s).values.asInstanceOf[Map[String, Any]]
        if (json.contains("error")){
          callback(parse(s).extract[ElasticError])
        }else {
          callback(parse(s).extract[ElasticQuery])
        }
      }
      case Failure(e) => println("elasticsearch query failed. query: "+query +"\nexception: "+ e.getMessage)
    }
  }



}