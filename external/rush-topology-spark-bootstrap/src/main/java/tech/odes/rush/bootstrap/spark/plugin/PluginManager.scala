package tech.odes.rush.bootstrap.spark.plugin

import java.util.ServiceLoader

import tech.odes.rush.api.spark.edge.SparkEdge

import scala.collection.JavaConverters._
import tech.odes.rush.api.spark.vertex.SparkVertex
import tech.odes.rush.common.exception.RushException

object PluginManager {
  def vertexs = {
    ServiceLoader.load(classOf[SparkVertex]).asScala.map(vertex => (vertex.name -> vertex)).toMap
  }

  def vertex(name: String) = vertexs.get(name) match {
    case Some(v) => v
    case None => throw new RushException(s"Vertex [${name}] not found!")
  }

  def edges = {
    ServiceLoader.load(classOf[SparkEdge]).asScala.map(edge => (edge.name -> edge)).toMap
  }

  def edge(name: String) = edges.get(name) match {
    case Some(e) => e
    case None => throw new RushException(s"Edge [${name}] not found!")
  }
}
