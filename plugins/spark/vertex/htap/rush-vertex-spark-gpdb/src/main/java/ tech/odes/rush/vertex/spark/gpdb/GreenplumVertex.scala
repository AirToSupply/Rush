package tech.odes.rush.vertex.spark.gpdb

import io.pivotal.greenplum.spark.conf.GreenplumOptions
import org.apache.spark.internal.Logging
import org.apache.spark.sql.DataFrame
import tech.odes.rush.api.spark.env.SparkEnvironment
import tech.odes.rush.api.spark.vertex.SparkVertex
import tech.odes.rush.common.exception.RushException
import tech.odes.rush.config.CheckConfigUtil

import scala.collection.JavaConverters._

/**
 * Spark Vertex - GPDB
 */
class GreenplumVertex extends SparkVertex with Logging {

  override def name: String = "greenplum"

  override def in(env: SparkEnvironment, cell: DataFrame): Unit = {
    CheckConfigUtil.assertAllExists(env.config.asJava, Array(
      GreenplumOptions.GPDB_URL,
      GreenplumOptions.GPDB_USER,
      GreenplumOptions.GPDB_PASSWORD,
      GreenplumOptions.GPDB_TABLE_NAME): _*)
    import GreenplumVertex._
    env.config.getOrElse(GPDB_EXTRA_OPTION_DIRECT, GPDB_EXTRA_OPTION_DIRECT_DEFAULT_VAL).toBoolean match {
      case true =>
        GreenplumWriter.jdbc(env, cell)
      case _ =>
        GreenplumWriter.gpfdist(env, cell)
    }
  }

  override def out(env: SparkEnvironment, cell: DataFrame): DataFrame = {
    throw new RushException(s"Not support to export Spark Vertex [${name}] ")
  }
}

object GreenplumVertex {
  val GPDB_EXTRA_OPTION_DIRECT = "direct"
  val GPDB_EXTRA_OPTION_DIRECT_DEFAULT_VAL = "false"
}
