package tech.odes.rush.bootstrap

import org.apache.spark.SparkConf
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.SparkSession

package object spark {

  private def defaultConf = {
    val additionalConfigs = new java.util.HashMap[String, String]
    additionalConfigs.put("spark.serializer", classOf[KryoSerializer].getName)
    additionalConfigs
  }

  def defaultSparkConf(appName: String): SparkConf = buildSparkConf(appName, defaultConf)

  def buildSparkConf(appName: String, additionalConfigs: java.util.Map[String, String]): SparkConf = {
    val sparkConf = new SparkConf().setAppName(appName)
    additionalConfigs.forEach(sparkConf.set)
    sparkConf
  }

  def defaultSparkSession(appName: String): SparkSession = buildSparkSession(appName, defaultConf)

  def buildSparkSession(appName: String, additionalConfigs: java.util.Map[String, String]): SparkSession = {
    val builder = SparkSession.builder.appName(appName)
    builder.getOrCreate
  }
}
