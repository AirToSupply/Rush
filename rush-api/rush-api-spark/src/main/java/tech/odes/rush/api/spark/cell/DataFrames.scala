package tech.odes.rush.api.spark.cell

import org.apache.spark.sql.{Column, DataFrame, Row}
import org.apache.spark.sql.types._

case class StructField(name: String, `type`: String)
case class StructFieldReplaceMetadate(source: StructField, target: StructField)

object DataFrames {

  /**
   * Specify the data type to expand the row object
   *
   * @param row
   *
   * @param name
   *        field name
   *
   * @param dataType
   *        filed type
   *
   * @return Any
    */
  def unwrapRow(row: Row, name: String, dataType: DataType): Any = {
    val i = row.fieldIndex(name)
    dataType match {
      case IntegerType =>
        row.getInt(i)
      case LongType =>
        row.getLong(i)
      case DoubleType =>
        row.getDouble(i)
      case FloatType =>
        row.getFloat(i)
      case ShortType =>
        row.getShort(i)
      case ByteType =>
        row.getByte(i)
      case BooleanType =>
        row.getBoolean(i)
      case StringType =>
        row.getString(i)
      case BinaryType =>
        row.getAs[Array[Byte]](i)
      case TimestampType =>
        row.getAs[java.sql.Timestamp](i)
      case DateType =>
        row.getAs[java.sql.Date](i)
      case t: DecimalType =>
        row.getDecimal(i)
      case ArrayType(dt, _) =>
        row.getSeq[AnyRef](i).toArray
      case _ =>
        throw new IllegalArgumentException(s"Can't unwrap non-null value for field $name, $i")
    }
  }

  /**
   * Get the first row data of the specified column
   *
   * @param table
   *
   * @param name
   *        field name
   *
   * @return `Option[Any]`
   */
  def getFieldHeadValueByName(table: DataFrame, name: String): Option[Any] = {
    val fieldOption = table.schema.find(f => f.name == name)
    if (fieldOption.isEmpty) {
      return None
    }
    val dataType = fieldOption.get.dataType
    val results = table.select(name).head(1).map(row => unwrapRow(row, name, dataType))
    results.headOption
  }

  /**
   * Get the whole column of data according to the field name.
   * When the amount of data is large, the driver may oom. Use it with caution!
   * @param table
   *
   * @param name
   *        field name
   *
   * @return `List[Any]`
   */
  def getFieldValueByName(table: DataFrame, name: String): List[Any] = {
    val fieldOption = table.schema.find(f => f.name == name)
    if (fieldOption.isEmpty) {
      return List.empty[Any]
    }
    val dataType = fieldOption.get.dataType
    table.select(name).collect().map(row => unwrapRow(row, name, dataType)).toList
  }

  /**
   * Get all column data.
   * The driver may oom when the amount of data is large. Use it with caution!
   *
   * @param table
   *
   * @return `List[Seq[Any]]`
   */
  def getAllFieldValue(table: DataFrame): List[Seq[Any]] = {
    val schema = table.schema.map(f => (f.name, f.dataType))
    table.collect().map{ row =>
        schema.map(it => unwrapRow(row, it._1, it._2))
    }.toList
  }

  def replaceSchema(table: DataFrame, schema: Array[StructFieldReplaceMetadate]) = {
    val sourceStructType = schema.map(_.source)
    val targetStructType = schema.map(_.target)
    val columns = sourceStructType.zipWithIndex.map { e =>
      e match {
        case (sourceStructField: StructField, index: Int) =>
          val targetStructField = targetStructType(index)
          val col = new Column(sourceStructField.name)
          col.cast(targetStructField.`type`).as(targetStructField.name)
      }
    }
    table.select(columns: _*)
  }
}

/**
 * schema replace options
 */
object DataFrameSchemaOptions {
  /**
   * schema change enabled
   */
  val OPTION_SCHEMA_REPLACE_ENABLE = "schema.replace.enable"
  val OPTION_SCHEMA_REPLACE_ENABLE_DEFAULT_VAL = "false"
  /**
   * schema change json content，JSON format is as follows:
   *
   * [
   *    {
   *      "source": {"name": "_c0", "type": "INT"},
   *      "target": {"name": "_c01", "type": "STRING"}
   *    },
   *    {
   *      "source": {"name": "_c1", "type": "STRING"},
   *      "target": {"name": "_c11", "type": "INT"}
   *    }
   * ]
   *
   * source: source field metadata
   *
   * target: target field metadata
   */
  val OPTION_SCHEMA_REPLACE = "schema.replace"
}
