package tech.odes.rush.bootstrap.spark.starter

import java.util.{Collections, Objects}

import com.beust.jcommander.JCommander
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import tech.odes.rush.api.spark.env.SparkEnvironment

import scala.collection.JavaConverters._
import tech.odes.rush.bootstrap.spark.config.SimpleTopologyConfiguration
import tech.odes.rush.bootstrap.spark._
import tech.odes.rush.bootstrap.spark.plugin.TopologyPluginManager
import tech.odes.rush.util.config.{DFSPropertiesConfiguration, TypedProperties}
import tech.odes.rush.util.{Option, StringUtils}
import tech.odes.rush.util.fs.FSUtils

class SimpleTopologyBootstrap(val cfg: SimpleTopologyConfiguration,
                              val spark: SparkSession,
                              val conf: Configuration,
                              val props: Option[TypedProperties]) extends Logging {

  private var vertexSourceProps: Map[String, String] = null

  private var vertexSinkProps: Map[String, String] = null

  initialize

  def this(cfg: SimpleTopologyConfiguration,
           spark: SparkSession) =
    this(cfg, spark, spark.sparkContext.hadoopConfiguration, Option.empty())

  def this(cfg: SimpleTopologyConfiguration,
           spark: SparkSession,
           props: Option[TypedProperties]) =
    this(cfg, spark, spark.sparkContext.hadoopConfiguration, props)

  def this(cfg: SimpleTopologyConfiguration,
           spark: SparkSession,
           conf: Configuration) =
    this(cfg, spark, conf, Option.empty())

  private def initialize = {
    populateTopologyConfig(cfg)
  }

  private def populateTopologyConfig(cfg: SimpleTopologyConfiguration) = {
    // read from properties
    val config = if (Objects.isNull(cfg.propsFilePath)) {
      TypedProperties.EMPTY_PROPERTIES
    } else {
      DFSPropertiesConfiguration.readConfig(
        FSUtils.getFs(cfg.propsFilePath, spark.sparkContext.hadoopConfiguration),
        new Path(cfg.propsFilePath),
        Collections.emptyList()).getConfig
    }

    // read vertex source config
    this.vertexSourceProps = DFSPropertiesConfiguration.readProperties(
      config
        .keyFliter(_.startsWith(SimpleTopologyConfiguration.__VERTEX_SOURCE_PROPS_PREFIX))
        .keyProcess(_.replace(SimpleTopologyConfiguration.__VERTEX_SOURCE_PROPS_PREFIX, StringUtils.EMPTY_STRING)),
      cfg.vertexSourceConfigs).getConfig.toMap.asScala.toMap

    // read vertex sink config
    this.vertexSinkProps = DFSPropertiesConfiguration.readProperties(
      config
        .keyFliter(_.startsWith(SimpleTopologyConfiguration.__VERTEX_SINK_PROPS_PREFIX))
        .keyProcess(_.replace(SimpleTopologyConfiguration.__VERTEX_SINK_PROPS_PREFIX, StringUtils.EMPTY_STRING)),
      cfg.vertexSinkConfigs).getConfig.toMap.asScala.toMap
  }

  def print = {
    printFrame()
    logInfo("|  Vertex Options (Source)")
    printFrame()
    vertexSourceProps.foreach(option => {
      logInfo(s"|  ${option._1}: ${option._2}")
    })
    printFrame()

    logInfo("|")

    printFrame()
    logInfo("|  Vertex Options (Sink)")
    printFrame()
    vertexSinkProps.foreach(option => {
      logInfo(s"|  ${option._1}: ${option._2}")
    })
    printFrame()
  }

  private def printFrame(mark: String = "-", count: Int = 120): Unit = {
    val _c = mark * count
    logInfo(s"|${_c}")
  }

  def run = {
    // source
    val df = TopologyPluginManager
      .vertex(cfg.vertexSourceType)
      .out(
        SparkEnvironment.builder
          .spark(spark)
          .path(this.vertexSourceProps.getOrElse(
            SimpleTopologyConfiguration.__VERTEX_SOURCE_PROPS_PATH, StringUtils.EMPTY_STRING))
          .config(this.vertexSourceProps)
          .build,
        spark.emptyDataFrame)

    // write
    TopologyPluginManager
      .vertex(cfg.vertexSinkType)
      .in(
        SparkEnvironment.builder
          .spark(spark)
          .path(this.vertexSinkProps.getOrElse(
            SimpleTopologyConfiguration.__VERTEX_SINK_PROPS_PATH, StringUtils.EMPTY_STRING))
          .config(this.vertexSinkProps)
          .saveMode(this.vertexSinkProps.getOrElse(
            SimpleTopologyConfiguration.__VERTEX_SINK_PROPS_SAVE_MODE,
            SimpleTopologyConfiguration.__VERTEX_SINK_PROPS_SAVE_MODE_DEFAULT_VAL))
          .build,
        df)
  }

  def shutdown = {
    spark.close
  }
}

/**
 * Rush Simple Topology Bootstrap Runner
 */
object SimpleTopologyBootstrap extends Logging {

  private def parse(args: Array[String]) = {
    val cfg = new SimpleTopologyConfiguration
    val cmd = new JCommander(cfg, null, args: _*)
    if (cfg.help || args.length == 0) {
      cmd.usage
      System.exit(1)
    }
    cfg
  }

  def main(args: Array[String]): Unit = {
    logInfo(tech.odes.rush.__RUSH_BANNER)
    val cfg = parse(args)
    val spark = defaultSparkSession(
      s"rush-topology-runner [${cfg.vertexSourceType}] => [${cfg.vertexSinkType}]")
    val bootstrap = new SimpleTopologyBootstrap(cfg, spark)
    try {
      bootstrap.print
      // bootstrap.run
    } catch {
      case e: Exception => logError("SimpleTopologyBootstrap Failed!", e)
    } finally {
      bootstrap.shutdown
    }
  }
}
