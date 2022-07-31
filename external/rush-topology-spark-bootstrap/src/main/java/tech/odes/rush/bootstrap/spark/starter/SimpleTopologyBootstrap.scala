package tech.odes.rush.bootstrap.spark.starter

import org.apache.spark.internal.Logging

class SimpleTopologyBootstrap extends Logging {
  def print = {
    println(tech.odes.rush.__RUSH_BANNER)
  }
}

object SimpleTopologyBootstrap {

  def main(args: Array[String]): Unit = {
    // tech.odes.rush.bootstrap.spark.defaultSparkSession("")
    new SimpleTopologyBootstrap().print
  }
}
