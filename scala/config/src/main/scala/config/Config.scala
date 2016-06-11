package config

import org.json4s._
import org.json4s.native.JsonMethods._

object Config {
  def get(key: String): String = {
    val config = parse(scala.io.Source.fromFile("../config.json").getLines.mkString).
      values.asInstanceOf[Map[String, String]]

    println("Config loaded:" + key+ "->"+   config.get(key).get)
    config.get(key).get
  }
}






