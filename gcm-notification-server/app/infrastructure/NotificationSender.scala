package infrastructure

import models.Notification
import play.api.libs.concurrent.Akka
import com.google.android.gcm.server.{Sender, Constants, Result, MulticastResult}
import utils.Chunks._
import scala.collection.JavaConversions._
import play.api.Play.current
import persistence.db.H2DbDeviceStorage
import concurrent.Future

object NotificationSender {

  val MaxMulticastSize = 1000

  val Sender: Sender = new Sender("API_KEY")

  def push(notification: Notification, regIdsList: Array[String]): List[Future[MulticastResult]] = {
    val message = notification.asMessage
    (regIdsList chunk MaxMulticastSize map (regIds => {
      Akka future (Sender send(message, regIds.toList, 5))
    })).toList
  }

  def handleResult(regId: String, result: Result) {
    Option(result.getMessageId) match {
      case Some(s) => handleMultipleRegistration(regId, Option(result.getCanonicalRegistrationId))
      case None => handleError(regId, result.getErrorCodeName)
    }
  }

  def handleMultipleRegistration(deviceRegistrationId: String, canonicalRegistrationId: Option[String]) {
    if (canonicalRegistrationId.isDefined) {
      H2DbDeviceStorage updateRegistrationId(deviceRegistrationId, canonicalRegistrationId.get)
    }
  }

  def handleError(deviceRegistrationId: String, errorCode: String) {
    errorCode match {
      case Constants.ERROR_NOT_REGISTERED => H2DbDeviceStorage delete (deviceRegistrationId)
      case Constants.ERROR_INVALID_REGISTRATION => H2DbDeviceStorage delete (deviceRegistrationId)
      case _ => // Handle errors as you want
    }
  }
}
