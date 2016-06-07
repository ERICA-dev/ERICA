package erica.config

import org.json4s._
import org.json4s.native.JsonMethods._

object Config extends App {
  val config:Map[String, String] = parse(scala.io.Source.fromFile("../config.json").getLines.mkString).
    values.asInstanceOf[Map[String, String]]

  println("config loaded:")
  config.foreach(c => println("  "+c._1 + " : "+ c._2))

  def get(key: String): Any = {
    config.get(key).get
  }
}






