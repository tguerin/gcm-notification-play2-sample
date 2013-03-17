package utils

import com.google.android.gcm.server.{Result, MulticastResult}
import scala.collection.JavaConversions._
import play.api.libs.concurrent.Execution.Implicits._
import concurrent.Future

class Results(promiseOfMulticastResults: Future[List[MulticastResult]]) {
  def toResults: Future[List[Result]] = {
    promiseOfMulticastResults map (multicastResults => multicastResults flatMap (multicastResult => multicastResult.getResults.toList))
  }
}

object Results {
  implicit def fromMulticastResultsPromise(promiseOfMulticastResults: Future[List[MulticastResult]]) = new Results(promiseOfMulticastResults)
}
