package tech.odes.rush.vertex.spark.file

import org.apache.spark.internal.Logging
import org.apache.spark.sql.DataFrame
import tech.odes.rush.api.spark.env.SparkEnvironment
import tech.odes.rush.api.spark.vertex.SparkVertex
import tech.odes.rush.common.exception.RushException
import tech.odes.rush.config.CheckConfigUtil

/**
 * Spark Vertex (CSV)
 */
class CSVVertex extends SparkVertex with Logging {

  override def name: String = "csv"

  override def in(env: SparkEnvironment, cell: DataFrame): Unit = {
    throw new RushException(s"Not support to import Spark Vertex [${name}] ")
  }

  override def out(env: SparkEnvironment, cell: DataFrame): DataFrame = {
    // check csv file path
    CheckConfigUtil.assertEmptyParam(env.path, s"Export Spark Vertex [${name}] option [path] must be specified!")

    // optimze
    var options = env.config
    options = options.updated(CSVVertex.CSV_OPTION_HEADER,
      options.getOrElse(CSVVertex.CSV_OPTION_HEADER, CSVVertex.CSV_OPTION_HEADER_DEFAULT_VAL))
    options = options.updated(CSVVertex.CSV_OPTION_INFER_SCHEMA,
      options.getOrElse(CSVVertex.CSV_OPTION_INFER_SCHEMA, CSVVertex.CSV_OPTION_INFER_SCHEMA_DEFAULT_VAL))

    env.spark.read.options(env.config).csv(env.path)
  }
}

object CSVVertex {
  val CSV_OPTION_HEADER = "header"
  val CSV_OPTION_HEADER_DEFAULT_VAL = "true"

  val CSV_OPTION_INFER_SCHEMA = "inferSchema"
  val CSV_OPTION_INFER_SCHEMA_DEFAULT_VAL = "true"
}
