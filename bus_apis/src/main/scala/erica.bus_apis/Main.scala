package erica.bus_apis

object Main extends App {
  val amqSubscriber = new AMQSubscriber
  val amqPublisher = new AMQPublisher
  def myFunc() { println("hej") }
  amqSubscriber.subscribe("hej", () => 
    amqPublisher.publish("hej", "titta vad roligt"))
  Thread.sleep(2000)
  amqPublisher.publish("hej", "håll i hatten")
}
