package example

import play.api.libs.json._

import scala.collection.JavaConverters._
import scala.util.control.NonFatal
import java.lang.reflect.Modifier

/////////////////////////
// This is how serialization can be done for java/groovy
abstract class JavaArgs {

  private[example] def asJsObject: JsObject = {

    val getters = this.getClass.getFields.filterNot { field =>
      val modifiers = field.getModifiers
      Modifier.isPrivate(modifiers) ||
        Modifier.isTransient(modifiers) ||
        Modifier.isProtected(modifiers) ||
        Modifier.isStatic(modifiers)
    }
    JsObject(
      getters
        .map { getter =>
          getter.getName -> toJsValue(getter.get(this))
        }
        .toMap
        .collect { case (k, Some(jsValue)) => (k, jsValue) }.toSeq
    )
  }


  private[example] def toJsValue(value: Any): Option[JsValue] = {
    value match {
      case args: JavaArgs => Some(args.asJsObject)
      case collection: java.util.Collection[_] => Some(JsArray(collection.asScala.flatMap(toJsValue).toSeq))
      case collection: Seq[_] => Some(JsArray(collection.flatMap(toJsValue)))
      case map: java.util.Map[_, _] =>
        Some(
          JsObject(
            map
              .asScala
              .map { case (key: String, v) =>
                key -> toJsValue(v)
              }
              .toMap
              .collect { case (k, Some(jsValue)) => (k, jsValue) }.toSeq
          )
        )
      case Some(v) => toJsValue(v)
      case None => None
      case v: String => Some(JsString(v))
      case v: Int => Some(JsNumber(v))
      case v: Long => Some(JsNumber(v))
      case v: Double => Some(JsNumber(v))
      case v: Boolean => Some(JsBoolean(v))
      case null => Some(JsNull)
      case _ =>
        throw new Exception(s"I can't serialize this value: $value (${value.getClass.getName})")
    }
  }

  protected[example] def serialize(): String = {
    try {
      Json.stringify(this.asJsObject)
    } catch {
      case NonFatal(ex) => throw new Exception("Failed to serialize: " + ex.getMessage, ex)
    }
  }
}
/////////////////////////


// Pseudo channgel interface
trait Channel {
  def publish(args: JavaArgs): Array[Byte]

  def publish[T: Writes](args: T): Array[Byte]
}

// implementation for all languages done in scala
class ChannelImpl extends Channel {
  // java api
  def publish(args: JavaArgs): Array[Byte] = {
    // call out to the fancy reflection stuff for java/groovy
    val json = args.serialize()
    println("json built starting from groovy/java: " + json)
    json.getBytes("utf-8")
  }

  // just use Reads/Writes for scala
  def publish[T: Writes](args: T): Array[Byte] = {
    val json = Json.stringify(Json.toJson(args))
    println("json built starting from scala: " + json)
    json.getBytes("utf-8")
  }

  // both produce Array[Bytes] which can be passed along to the message bus
}

case class ScalaArgsImpl(xxx: Int, yyy: String, aaa: Option[ScalaArgsImpl])

case object ScalaArgsImpl {
  implicit val format = Json.format[ScalaArgsImpl]
}


object TestApp extends App {
  val channel: Channel = new ChannelImpl
  println(channel.publish(ScalaArgsImpl(5, "test", None)))
}



