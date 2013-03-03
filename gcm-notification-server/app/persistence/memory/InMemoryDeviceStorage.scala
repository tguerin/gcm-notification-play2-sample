package persistence.memory

import models.Device
import scala.collection
import collection.mutable

/**
 * Store devices in memory
 */
object InMemoryDeviceStorage {

  var devices = new mutable.HashSet[Device]() with collection.mutable.SynchronizedSet[Device]

  def store(device: Device) { devices += device }

  def all(): Array[Device] = { devices.toArray }

  def findByRegistrationId(regId: String): Option[Device] = { devices.view find (t => t.registrationId.equals(regId)) }

  def delete(device: Device) { devices remove device }

  def delete(regId: String) {
    val (before, atAndAfter) = devices span (device => !device.registrationId.equals(regId))
    before ++ atAndAfter.drop(1)
  }

  def updateRegistrationId(oldRegId: String, newRegId: String) {
    val device = findByRegistrationId(oldRegId)
    if (device.isDefined) {
      device.get.registrationId = newRegId
    }
  }

}
