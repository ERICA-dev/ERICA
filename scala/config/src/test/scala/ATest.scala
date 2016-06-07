import org.scalatest.FlatSpec
import erica.config.Config

class ASpec extends FlatSpec {
  "A one" should "be a one, of course" in {
    val ip = Config.get("bus")
    println(ip)
    assert(ip.isInstanceOf[String])
  }
}
