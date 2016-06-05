package service

object Main extends App {
  var amqService = new AMQService
  def myFunc() { println("hej") }
  amqService.subscribe("hej", myFunc)
  // amqService.publish("neuesneuestopic", "mooooo")
}
