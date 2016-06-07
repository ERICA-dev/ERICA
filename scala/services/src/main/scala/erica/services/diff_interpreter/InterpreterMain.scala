package erica.services.diff_interpreter

import erica.bus_apis.AMQSubscriber
import erica.bus_apis.AMQPublisher
/**
  * Created by marp on 2016-06-06.
  */
object InterpreterMain extends App {
  new Interpreter
}

class Interpreter {
  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher

  def myFunc() { println("hej") }

  amqSubscriber.subscribe("hej", (mess:String) =>
    received(mess))

  def received(mess:String): Unit = {
    println("Interpreter received:" + mess)
  }
}
