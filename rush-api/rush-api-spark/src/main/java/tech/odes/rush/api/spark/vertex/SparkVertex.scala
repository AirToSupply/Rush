package tech.odes.rush.api.spark.vertex

import org.apache.spark.sql.DataFrame
import tech.odes.rush.api.spark.env.SparkEnvironment

trait SparkVertex extends Vertex[SparkEnvironment, DataFrame] {
  def name: String
}
