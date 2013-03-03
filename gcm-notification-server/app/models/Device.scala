package models

import persistence.db.H2DbDeviceStorage

/**
 * Class to represent a device, we only store registration id but we could link the device to an user account
 * in a real application.
 */
case class Device(var registrationId: String)

object Device {

  def createAndStore(regId: String) { H2DbDeviceStorage store Device(regId) }

  def delete(regId: String) { H2DbDeviceStorage delete Device(regId) }

  def allRegistrationIds: Array[String] = { H2DbDeviceStorage.all map (x => x.registrationId) }
}
