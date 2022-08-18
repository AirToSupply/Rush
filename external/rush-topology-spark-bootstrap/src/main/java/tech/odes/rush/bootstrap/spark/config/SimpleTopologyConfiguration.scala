package tech.odes.rush.bootstrap.spark.config

import com.beust.jcommander.Parameter
import com.beust.jcommander.internal.Lists
import tech.odes.rush.bootstrap.spark.util.IdentitySplitter

import scala.collection.JavaConverters._

class SimpleTopologyConfiguration extends Serializable {

  @Parameter(
    names = Array("--props"),
    description = "Path to properties file on localfs or dfs, with configurations for simple rush topology.")
  var propsFilePath: String = SimpleTopologyConfiguration.DEFAULT_DFS_SOURCE_PROPERTIES

  @Parameter(
    names = Array("--vertex-source-type"),
    description = "Vertex of topology source type.",
    required = true)
  var vertexSourceType: String = null

  @Parameter(
    names = Array("--vertex-sink-type"),
    description = "Vertex of topology sink type.",
    required = true)
  var vertexSinkType: String = null

  @Parameter(
    names = Array("--vertex-source"),
    description =
      "Any source configuration that can be set in the properties file (using the CLI parameter --props)" +
      "can also be passed command line using this parameter. This can be repeated.",
    splitter = classOf[IdentitySplitter])
  var vertexSourceConfigs = Lists.newArrayList[String]()

  @Parameter(
    names = Array("--vertex-sink"),
    description =
      "Any sink configuration that can be set in the properties file (using the CLI parameter --props)" +
      "can also be passed command line using this parameter. This can be repeated.",
    splitter = classOf[IdentitySplitter])
  var vertexSinkConfigs = Lists.newArrayList[String]()

  @Parameter(names = Array("--help", "-h"), help = true)
  var help: Boolean = false

  override def toString =
    s"""
       |=============================================
       |property file path: $propsFilePath
       |help: $help
       |vertexSourceType: $vertexSourceType
       |vertexSinkType: $vertexSinkType
       |vertex source configs:
       |${vertexSourceConfigs.asScala.toArray[String].map(e => "  |-- ".concat(e)).mkString("\n")}
       |vertex sink configs:
       |${vertexSinkConfigs.asScala.toArray[String].map(e => "  |-- ".concat(e)).mkString("\n")}
       |=============================================
       |""".stripMargin
}

object SimpleTopologyConfiguration {

  val DEFAULT_DFS_SOURCE_PROPERTIES: String =
    s"file://${System.getProperty("RUSH_HOME")}/conf/simple-topology.properties"

  val __VERTEX_SOURCE_PROPS_PREFIX = "vertex.source."
  val __VERTEX_SOURCE_PROPS_PATH = "path"

  val __VERTEX_SINK_PROPS_PREFIX = "vertex.sink."
  val __VERTEX_SINK_PROPS_PATH = "path"
  val __VERTEX_SINK_PROPS_SAVE_MODE = "saveMode"
  val __VERTEX_SINK_PROPS_SAVE_MODE_DEFAULT_VAL = "append"
}
