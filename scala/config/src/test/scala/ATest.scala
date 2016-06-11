import config.Config
import org.scalatest.FlatSpec

class ASpec extends FlatSpec {
  "Configs bus_ip" should "be a string" in {
    val ip = Config.get("bus_ip")
    assert(ip.isInstanceOf[String])
  }
}
