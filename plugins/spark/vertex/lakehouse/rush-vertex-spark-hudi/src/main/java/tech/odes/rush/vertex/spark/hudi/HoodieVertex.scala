package tech.odes.rush.vertex.spark.hudi

import org.apache.hudi.common.table.HoodieTableConfig
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.hudi.hive.HiveSyncConfig
import org.apache.hudi.keygen.constant.KeyGeneratorOptions
import org.apache.hudi.sync.common.HoodieSyncConfig
import org.apache.spark.internal.Logging
import org.apache.spark.sql.DataFrame
import tech.odes.rush.api.spark.env.SparkEnvironment
import tech.odes.rush.api.spark.vertex.SparkVertex
import tech.odes.rush.config.CheckConfigUtil

import scala.collection.JavaConverters._

/**
 * Spark Vertex - Apache Hudi
 */
class HoodieVertex extends SparkVertex with Logging {

  override def name: String = "hudi"

  override def in(env: SparkEnvironment, cell: DataFrame): Unit = {
    // check hoodie basePath
    CheckConfigUtil.assertEmptyParam(env.path, s"Import Spark Vertex [${name}] option [path] must be specified!")
    // check hoodie.table.name
    CheckConfigUtil.assertAllExists(env.config.asJava, Array(HoodieWriteConfig.TBL_NAME.key): _*)
    // check hoodie.datasource.write.recordkey.field
    CheckConfigUtil.assertAllExists(env.config.asJava, Array(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key): _*)

    // optimze
    var options = env.config
    // hoodie.base.path
    options = options.updated(HoodieWriteConfig.BASE_PATH.key,
      options.getOrElse(HoodieWriteConfig.BASE_PATH.key, env.path))
    // hoodie.*.shuffle.parallelism
    options = options.updated(HoodieWriteConfig.UPSERT_PARALLELISM_VALUE.key,
      options.getOrElse(HoodieWriteConfig.UPSERT_PARALLELISM_VALUE.key,
        HoodieVertex.HOODIE_OPTERATION_SHUFFLE_PARALLELISM))
    options = options.updated(HoodieWriteConfig.DELETE_PARALLELISM_VALUE.key,
      options.getOrElse(HoodieWriteConfig.DELETE_PARALLELISM_VALUE.key,
        HoodieVertex.HOODIE_OPTERATION_SHUFFLE_PARALLELISM))
    options = options.updated(HoodieWriteConfig.INSERT_PARALLELISM_VALUE.key,
      options.getOrElse(HoodieWriteConfig.INSERT_PARALLELISM_VALUE.key,
        HoodieVertex.HOODIE_OPTERATION_SHUFFLE_PARALLELISM))
    options = options.updated(HoodieWriteConfig.BULKINSERT_PARALLELISM_VALUE.key,
      options.getOrElse(HoodieWriteConfig.BULKINSERT_PARALLELISM_VALUE.key,
        HoodieVertex.HOODIE_OPTERATION_SHUFFLE_PARALLELISM))

    cell.write.format(name)
      .options(options)
      .mode(env.saveMode)
      .save(env.path)
  }

  override def out(env: SparkEnvironment, cell: DataFrame): DataFrame = {
    CheckConfigUtil.assertEmptyParam(env.path, s"Export Spark Vertex [${name}] option [path] must be specified!")
    env.spark.read.format(name).options(env.config).load(env.path)
  }
}

object HoodieVertex {
  val HOODIE_OPTERATION_SHUFFLE_PARALLELISM = "32"
}