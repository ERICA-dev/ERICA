package erica.config

import org.json4s._
import org.json4s.native.JsonMethods._

object Config extends App {
  var config:Map[String, String] = null
  var init_once = false

  def get(key: String): String = {
    if (!init_once) {
      config = parse(scala.io.Source.fromFile("../config.json").getLines.mkString).
        values.asInstanceOf[Map[String, String]]

      println("config loaded:")
      config.foreach(c => println("  "+c._1 + " : "+ c._2))
      init_once = true
    }
    config.get(key).get
  }
}






