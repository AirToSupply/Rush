package tech.odes.rush.api.spark.env

import org.apache.spark.sql.{SaveMode, SparkSession}

class SparkEnvironment {
  private var __spark: SparkSession = null
  private var __path: String = null
  private var __config: Map[String, String] = Map.empty
  private var __saveMode: SaveMode = SaveMode.Append

  def spark = __spark
  def path = __path
  def config = __config
  def saveMode = __saveMode

  def this(spark: SparkSession) {
    this
    this.__spark = spark
  }

  def this(spark: SparkSession,
           path: String,
           config: Map[String, String],
           saveMode: SaveMode) {
    this(spark)
    this.__path = path
    this.__config = config
    this.__saveMode = saveMode
  }

  def this(spark: SparkSession,
           path: String,
           config: Map[String, String]) {
    this(spark, path, config, SaveMode.Append)
  }

  def this(spark: SparkSession,
           config: Map[String, String]) {
    this(spark, null, config, SaveMode.Append)
  }
}

object SparkEnvironment {

  def builder = new Builder

  class Builder {
    private var __spark: SparkSession = null
    private var __path: String = null
    private var __config: Map[String, String] = Map.empty
    private var __saveMode: SaveMode = SaveMode.Append

    def spark(spark: SparkSession): Builder = {
      this.__spark = spark
      this
    }

    def path(path: String): Builder = {
      this.__path = path
      this
    }

    def config(config: Map[String, String]): Builder = {
      this.__config = config
      this
    }

    def saveMode(saveMode: SaveMode): Builder = {
      this.__saveMode = saveMode
      this
    }

    def build: SparkEnvironment = new SparkEnvironment(__spark, __path, __config, __saveMode)
  }

}

