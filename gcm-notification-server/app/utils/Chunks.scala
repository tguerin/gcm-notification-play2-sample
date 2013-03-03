package utils

class Chunks[T](iterableToChunk: Array[T]) {
  def chunk(chunkSize: Int) = Array.range(0, iterableToChunk.size, chunkSize).map(i => iterableToChunk.slice(i, i + chunkSize))
}

object Chunks {
   implicit def chunkedArray[T](array: Array[T]) = new Chunks[T](array)
}
