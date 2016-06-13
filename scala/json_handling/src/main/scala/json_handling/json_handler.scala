package json_handling

import org.json4s.JValue
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._

/**
  * Created by marp on 2016-06-12.
  */

object json_handler {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  def toJValue(x:AnyRef): JValue = {
    x match {
      case s:String => parse(s)
      case j:JValue => j
      case c:Object => parse(write(c))
      case _ => throw new IllegalArgumentException
    }
  }

  def toCaseClass[T](x:AnyRef)()(implicit mf: Manifest[T]):T =  {
    x match {
      case s:String => parse(s).extract[T]
      case j:JValue => j.extract[T]
      case c:T => c.asInstanceOf[T]
      case _ => throw new IllegalArgumentException
    }
  }

  def toJsonString(x:AnyRef): String = {
    x match {
      case s:String => s
      case j:JValue => write(j)
      case c:Object => write(c)
      case _ => throw new IllegalArgumentException
    }
  }

  def translatableTo(x:AnyRef, y:AnyRef): Boolean = {
    try {
      y match {
        case s:String => toJsonString(x).equals(y)
        case j:JValue => j.extract[y.type].equals(y)
        case c:Object => toCaseClass[y.type](x).equals(y)
        case _ => false
      }
    } catch {
      case e:Exception => false
    }
  }
}
