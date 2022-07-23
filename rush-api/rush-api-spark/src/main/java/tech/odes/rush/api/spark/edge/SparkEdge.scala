package tech.odes.rush.api.spark.edge

import org.apache.spark.sql.DataFrame
import tech.odes.rush.api.spark.env.SparkEnvironment

trait SparkEdge extends Edge[SparkEnvironment, DataFrame] {
  def name: String
}
