package elastic_api

import com.github.nscala_time.time.Imports._
import org.json4s.JsonAST.JValue
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
  hits: List[Hit]
)

case class Hit (
  _index: String,
  _type: String,
  _id: String,
  _score: Double,
  _source: JValue
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





