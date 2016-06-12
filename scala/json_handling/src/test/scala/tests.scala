import json_handling.json_handler._
import org.scalatest.FlatSpec

/**
  * Created by marp on 2016-06-12.
  */


case class Test (
  a: String,
  b: BigInt,
  c: TestTwo
)
case class TestTwo (
  e:String,
  f:BigInt
)

case class DifferentFormatTest (
  a: String
)

class jsonTester extends FlatSpec {
  val test = new Test(
    a = "asd",
    b = 2,
    c = new TestTwo(
      e = "sfsdf",
      f = 2
    )
  )
  val otherTest = new Test(
    a = "OTHERTEST",
    b = 2,
    c = new TestTwo(
      e = "sfsdf",
      f = 2
    )
  )
  val otherFormat = new DifferentFormatTest(
    a = "diffrent"
  )

  "converting to another type and back again" should "return the same value" in {
    assert( toJsonString(toCaseClass[Test](toJsonString(test))).equals(toJsonString(test)))
    assert( toJsonString(toJValue(toJsonString(test))).equals(toJsonString(test)))
    assert( toJsonString(toCaseClass[Test](toJsonString(test))).equals(toJsonString(test)))

    assert( toCaseClass[Test](test).equals(test))
    assert( toCaseClass[Test](toJValue(test)).equals(test))
    assert( toCaseClass[Test](toJsonString(test)).equals(test))

    assert( toJValue(toJValue(test)).equals(toJValue(test)))
    assert( toJValue(toJsonString(test)).equals(toJValue(test)))
    assert( toJValue(toCaseClass[Test](test)).equals(toJValue(test)))
  }

  "translatableTo" should "return true when translation is possible" in {
    assert(translatableTo(toJsonString(test), test) )
    assert(translatableTo(toJValue(test), test) )
    assert(translatableTo(toCaseClass[Test](test), test) )
  }

  "translatableTo" should "return false when translation is not possible" in {
    assert(!translatableTo(toJsonString(test), otherTest) )
    assert(!translatableTo(toJValue(test), otherTest) )
    assert(!translatableTo(toCaseClass[Test](test), otherTest))

    assert(!translatableTo(toJsonString(test), otherFormat) )
    assert(!translatableTo(toJValue(test), otherFormat) )
    assert(!translatableTo(toCaseClass[Test](test), otherFormat))
  }

  "convertion from case class to a diffrent case class" should "throw IllegalArgumentException" in {
    assert(
      try{
        toCaseClass[Test](otherFormat)
        false
      } catch {
        case e:IllegalArgumentException => true
      }
    )
  }
}
