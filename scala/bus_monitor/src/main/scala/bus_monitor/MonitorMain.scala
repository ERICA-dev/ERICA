package bus_monitor
import bus_api.AMQSubscriber
/**
  * Created by marp on 2016-06-13.
  */
object MonitorMain extends App {
  new Monitor("sandboxTopic")
}

class Monitor(topic:String) {
  val amqSubscriber = new AMQSubscriber
  println("Monitor listening to topic "+topic)
  amqSubscriber.subscribe(topic, (mess:String) =>println("Received on topic"+topic +"\n"+mess))
}
