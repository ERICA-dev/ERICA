import bus_api.{AMQSubscriber, AMQPublisher}
import org.scalatest.FlatSpec
import erica.bus_api.AMQSubscriber

class StringSender extends FlatSpec {
  "A sent string" should "come back the same" in {
    val str = "heeeeejdaer"
    var returnedStr = ""
    val amqSubscriber = new AMQSubscriber
    val amqPublisher = new AMQPublisher
    amqSubscriber.subscribe("testTopik", (mess:String) => {
        returnedStr = mess
    })
    // TODO ugly delays cause of conc issue, see amqP and amqS files
    Thread.sleep(2000)
    amqPublisher.publish("testTopik", str)
    Thread.sleep(2000)
    assert(returnedStr === str)
  }
}
