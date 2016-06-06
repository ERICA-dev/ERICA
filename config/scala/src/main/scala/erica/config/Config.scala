package erica.config

import org.json4s._
import org.json4s.native.JsonMethods._

object Config extends App {
  val config = parse(scala.io.Source.fromFile("../../config.json").getLines.mkString)

  def getConfig() {
    config
  }
}




