package  tech.odes.rush.vertex.spark.gpdb

import io.pivotal.greenplum.spark.conf.GreenplumOptions._
import io.pivotal.greenplum.spark.externaltable.{GpfdistServiceManager, ServiceKey}
import org.apache.spark.SparkEnv
import org.apache.spark.internal.Logging
import org.apache.spark.sql.DataFrame
import tech.odes.rush.api.spark.env.SparkEnvironment

object GreenplumWriter extends Logging {
  def gpfdist(env: SparkEnvironment, dataset: DataFrame) = {
    dataset.write.format("greenplum").options(env.config).mode(env.saveMode).save()
    // if this is the driver (i.e., local mode), we need to stop the GpfdistService
    // to spark submit process to terminated
    if (SparkEnv.get.executorId == "driver") {
      logInfo("Terminating gpfdist service manually in local mode!")
      GpfdistServiceManager.stopAndRemove(ServiceKey(ConnectorOptionsWrapper.port(env.config)))
    }
  }

  def jdbc(env: SparkEnvironment, dataset: DataFrame) = {
    var config = env.config
    val tableName = config.getOrElse(GPDB_SCHEMA_NAME, "public").concat(".").concat(
      config.getOrElse(GPDB_TABLE_NAME, "tmp"))
    config = config -
      GPDB_SCHEMA_NAME -
      GPDB_TABLE_NAME +
      (GPDB_TABLE_NAME -> tableName) +
      (GPDB_DRIVER -> "org.postgresql.Driver")
    dataset.write.format("jdbc").options(config).mode(env.saveMode).save()
  }
}
