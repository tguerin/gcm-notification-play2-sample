package persistence.db

import models.Device
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

object H2DbDeviceStorage {

  val storeDevice = SQL("MERGE INTO Device (registrationId) KEY(registrationId) VALUES ({registrationId})")
  val selectAllRegistrationId = SQL("SELECT registrationId FROM Device")
  val findDeviceByRegistrationId = SQL("SELECT registrationId FROM Device WHERE registrationId={registrationId}")
  val deleteDeviceByRegistrationId = SQL("DELETE FROM Device WHERE registrationId={registrationId}")
  val updateDeviceRegistrationId = SQL("UPDATE Device SET registrationId={newRegistrationId} WHERE registrationId={oldRegistrationId}")

  def store(device: Device) {
    DB.withConnection {
      implicit connection =>
        storeDevice on ("registrationId" -> device.registrationId) execute()
    }
  }

  def all(): Array[Device] = DB.withConnection {
    implicit connection =>
      selectAllRegistrationId().map({row => Device(row[String]("registrationId"))}).toArray
  }

  def findByRegistrationId(regId: String): Option[Device] = DB.withConnection {
    implicit connection =>
      val registrationIdOption = (findDeviceByRegistrationId on ("registrationId" -> regId) as (scalar[String].singleOpt))

      registrationIdOption.collect({case registrationId => Device(registrationId)})
  }

  def delete(device: Device) {
    delete(device.registrationId)
  }

  def delete(registrationId: String) {
    DB.withConnection {
      implicit connection =>
        deleteDeviceByRegistrationId on ("registrationId" -> registrationId) execute()
    }
  }

  def updateRegistrationId(oldRegId: String, newRegId: String) {
    DB.withConnection {
      implicit connection =>
        updateDeviceRegistrationId on("oldRegistrationId" -> oldRegId, "newRegistrationId" -> newRegId) execute()
    }
  }
}
