package models

import com.google.android.gcm.server.Message
import play.api.libs.json.{JsSuccess, JsValue, Format}

case class Notification(message: String, collapseKey: Option[String], ttl: Option[Int]) {

  def asMessage: Message = {
    val messageBuilder = new Message.Builder()
    messageBuilder.addData("message", message)
    if (collapseKey.isDefined) {
      messageBuilder.collapseKey(collapseKey.get)
    }
    if (ttl.isDefined) {
      messageBuilder.timeToLive(ttl.get)
    }
    messageBuilder.build()
  }
}

object Notification {

  implicit object NotificationFormat extends Format[Notification] {
    def writes(o: Notification) = null

    def reads(json: JsValue) = {
      JsSuccess(Notification(
        (json \ "message").as[String],
        (json \ "collapseKey").asOpt[String],
        (json \ "ttl").asOpt[Int]))
    }
  }

}
