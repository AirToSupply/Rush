package tech.odes.rush.vertex.spark.hudi.jdbc

import org.apache.spark.internal.Logging
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import tech.odes.rush.api.spark.env.SparkEnvironment
import tech.odes.rush.api.spark.vertex.SparkVertex
import tech.odes.rush.common.exception.RushException
import tech.odes.rush.config.CheckConfigUtil

import scala.collection.JavaConverters._

/**
 * Spark Vertex (JDBC)
 */
class JDBCVertex extends SparkVertex with Logging {

  override def name: String = "jdbc"

  override def in(env: SparkEnvironment, cell: DataFrame): Unit = {
    throw new RushException(s"Not support to import Spark Vertex [${name}] ")
  }

  override def out(env: SparkEnvironment, cell: DataFrame): DataFrame = {
    CheckConfigUtil.assertAllExists(env.config.asJava, Array(
      JDBCOptions.JDBC_URL,
      JDBCOptions.JDBC_DRIVER_CLASS,
      JDBCVertex.JDBC_USER,
      JDBCVertex.JDBC_PASSWORD): _*)

    CheckConfigUtil.checkAtLeastOneExists(env.config.asJava, Array(
      JDBCOptions.JDBC_TABLE_NAME,
      JDBCOptions.JDBC_QUERY_STRING): _*)

    env.spark.read.format(name).options(env.config).load
  }
}

object JDBCVertex {
  val JDBC_USER = "user"
  val JDBC_PASSWORD = "password"
}
