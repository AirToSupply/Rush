package tech.odes.rush.vertex.spark.gpdb

import io.pivotal.greenplum.spark.conf.GreenplumOptions
import io.pivotal.greenplum.spark.externaltable.{GpfdistServiceManager, ServiceKey}
import org.apache.spark.SparkEnv
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
    cell.write.format(name).options(env.config).mode(env.saveMode).save()
    // if this is the driver (i.e., local mode), we need to stop the GpfdistService
    // to spark submit process to terminated
    if (SparkEnv.get.executorId == "driver") {
      logInfo(s"Spark Vertex [${name}] is terminating gpfdist service manually in local mode!")
      GpfdistServiceManager.stopAndRemove(ServiceKey(ConnectorOptionsWrapper.port(env.config)))
    }
  }

  override def out(env: SparkEnvironment, cell: DataFrame): DataFrame = {
    throw new RushException(s"Not support to export Spark Vertex [${name}] ")
  }
}
