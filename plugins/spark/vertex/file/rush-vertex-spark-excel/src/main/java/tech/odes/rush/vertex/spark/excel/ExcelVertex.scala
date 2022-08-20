package tech.odes.rush.vertex.spark.excel

import org.apache.spark.internal.Logging
import org.apache.spark.sql.DataFrame
import tech.odes.rush.api.spark.cell._
import tech.odes.rush.api.spark.env.SparkEnvironment
import tech.odes.rush.api.spark.vertex.SparkVertex
import tech.odes.rush.common.exception.RushException
import tech.odes.rush.config.CheckConfigUtil
import tech.odes.rush.util.JacksonUtils

/**
 * Spark Vertex - Excel
 */
class ExcelVertex extends SparkVertex with Logging {

  override def name: String = "excel"

  override def in(env: SparkEnvironment, cell: DataFrame): Unit = {
    throw new RushException(s"Not support to import Spark Vertex [${name}] ")
  }

  override def out(env: SparkEnvironment, cell: DataFrame): DataFrame = {
    // check excel file path
    CheckConfigUtil.assertEmptyParam(env.path, s"Export Spark Vertex [${name}] option [path] must be specified!")

    // optimze
    var options = env.config
    options = options.updated(ExcelVertex.EXCEL_OPTION_HEADER,
      options.getOrElse(ExcelVertex.EXCEL_OPTION_HEADER, ExcelVertex.EXCEL_OPTION_HEADER_DEFAULT_VAL))
    options = options.updated(ExcelVertex.EXCEL_OPTION_INFER_SCHEMA,
      options.getOrElse(ExcelVertex.EXCEL_OPTION_INFER_SCHEMA, ExcelVertex.EXCEL_OPTION_INFER_SCHEMA_DEFAULT_VAL))

    val table = env.spark.read.format(name).options(env.config).load(env.path)

    val schemaReplaceEnable = env.config.getOrElse(DataFrameSchemaOptions.OPTION_SCHEMA_REPLACE_ENABLE,
      DataFrameSchemaOptions.OPTION_SCHEMA_REPLACE_ENABLE_DEFAULT_VAL).toBoolean
    if (schemaReplaceEnable) {
      DataFrames.replaceSchema(table, JacksonUtils.fromJson[Array[StructFieldReplaceMetadate]](
        env.config.get(DataFrameSchemaOptions.OPTION_SCHEMA_REPLACE).get))
    } else {
      table
    }
  }
}

object ExcelVertex {
  val EXCEL_OPTION_HEADER = "header"
  val EXCEL_OPTION_HEADER_DEFAULT_VAL = "true"

  val EXCEL_OPTION_INFER_SCHEMA = "inferSchema"
  val EXCEL_OPTION_INFER_SCHEMA_DEFAULT_VAL = "true"
}