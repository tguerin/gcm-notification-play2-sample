package utils

import play.api.libs.concurrent.Promise
import com.google.android.gcm.server.{Result, MulticastResult}
import scala.collection.JavaConversions._

class Results(promiseOfMulticastResults: Promise[List[MulticastResult]]) {
  def toResults: Promise[List[Result]] = {
    promiseOfMulticastResults map (multicastResults => multicastResults flatMap (multicastResult => multicastResult.getResults.toList))
  }
}

object Results {
  implicit def fromMulticastResultsPromise(promiseOfMulticastResults: Promise[List[MulticastResult]]) = new Results(promiseOfMulticastResults)
}
