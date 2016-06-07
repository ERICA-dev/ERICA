package erica.bus_api

object Main extends App {
  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher
  def myFunc() { println("hej") }
  amqSubscriber.subscribe("hej", (mess:String) =>
    amqPublisher.publish("hej", mess))

  Thread.sleep(2000)
  amqPublisher.publish("hej", "håll i hatten")
}
