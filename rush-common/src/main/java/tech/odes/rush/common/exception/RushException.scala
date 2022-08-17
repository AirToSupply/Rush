package tech.odes.rush.common.exception

import scala.collection.mutable.ArrayBuffer

class RushException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(message: String) = this(message, null)
  def this(cause: Throwable) = this(null, cause)
}

object Exceptions {
  def formatException(e: Exception) = {
    (e.toString.split("\n") ++ e.getStackTrace.map(f => "\tat " + f.toString)).mkString("\n")
  }

  def formatThrowable(e: Throwable) = {
    (e.toString.split("\n") ++ e.getStackTrace.map(f => "\tat " + f.toString)).mkString("\n")
  }

  def formatCause(e: Exception) = {
    var cause = e.asInstanceOf[Throwable]
    while (cause.getCause != null) {
      cause = cause.getCause
    }
    formatThrowable(cause)
  }

  def formatFullException(e: Exception) = {
    val buffer = new ArrayBuffer[String]
    var cause = e.asInstanceOf[Throwable]
    buffer += formatThrowable(cause)
    while (cause.getCause != null) {
      cause = cause.getCause
      buffer += "caused by: " + formatThrowable(cause)
    }

    buffer.mkString("\n")
  }
}