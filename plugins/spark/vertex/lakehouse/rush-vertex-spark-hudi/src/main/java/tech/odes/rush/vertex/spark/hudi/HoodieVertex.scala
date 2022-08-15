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
    // check hoodie.datasource.write.table.name
    CheckConfigUtil.assertAllExists(env.config.asJava, Array(HoodieTableConfig.HOODIE_WRITE_TABLE_NAME_KEY): _*)
    // check hoodie.datasource.write.recordkey.field
    CheckConfigUtil.assertAllExists(env.config.asJava, Array(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key): _*)
    // check hoodie meta hive sync
    (env.config.get(HiveSyncConfig.HIVE_SYNC_ENABLED.key), env.config.get(HoodieSyncConfig.META_SYNC_ENABLED.key)) match {
      case (Some(hiveSyncEnabled), Some(metaSyncEnabled))
        if (hiveSyncEnabled.toBoolean && metaSyncEnabled.toBoolean) =>
        CheckConfigUtil.assertAllExists(env.config.asJava, Array(
          HoodieSyncConfig.META_SYNC_DATABASE_NAME.key,
          HoodieSyncConfig.META_SYNC_TABLE_NAME.key,
          HiveSyncConfig.HIVE_URL.key,
          HiveSyncConfig.HIVE_USER.key,
          HiveSyncConfig.HIVE_PASS.key): _*)
    }

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
    // hoodie.datasource.hive_sync.table
    (env.config.get(HiveSyncConfig.HIVE_SYNC_ENABLED.key), env.config.get(HoodieSyncConfig.META_SYNC_ENABLED.key)) match {
      case (Some(hiveSyncEnabled), Some(metaSyncEnabled))
        if (hiveSyncEnabled.toBoolean && metaSyncEnabled.toBoolean) =>
        options = options.updated(HoodieSyncConfig.META_SYNC_TABLE_NAME.key,
          options.getOrElse(HoodieSyncConfig.META_SYNC_TABLE_NAME.key,
            HoodieTableConfig.HOODIE_WRITE_TABLE_NAME_KEY))
    }

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