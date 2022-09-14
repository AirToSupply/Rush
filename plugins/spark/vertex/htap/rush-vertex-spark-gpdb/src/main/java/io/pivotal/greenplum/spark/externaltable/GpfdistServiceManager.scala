package io.pivotal.greenplum.spark.externaltable

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.util.Try

import com.typesafe.scalalogging.LazyLogging
import io.pivotal.greenplum.spark.NetworkWrapper
import io.pivotal.greenplum.spark.conf.ConnectorOptions
import io.pivotal.greenplum.spark.util.TransactionData
import org.apache.commons.lang.StringUtils
import org.eclipse.jetty.server.Server

import org.apache.spark.SparkEnv
import org.apache.spark.sql.SparkSession

case class ServiceKey(port: List[Int])

object GpfdistServiceManager extends LazyLogging {

  // default to binding on a randomly allocated port
  private val bufferMap = new ConcurrentHashMap[String, Try[TransactionData]]
  private val sendBufferMap = new ConcurrentHashMap[String, PartitionData]
  private val servicesMap = new ConcurrentHashMap[ServiceKey, GpfdistService]()

  def getService(connectorOptions: ConnectorOptions): GpfdistService = {
    val ports = connectorOptions.port

    val key = ServiceKey(ports)
    logger.whenTraceEnabled(servicesMap.entrySet().asScala.foreach { e =>
      logger.trace(s"${GpfdistServiceManager.hashCode()}: [key: ${e.getKey}; value: ${e.getValue}]")
    })

    if (!servicesMap.containsKey(key)) {
      servicesMap.synchronized {
        if (!servicesMap.containsKey(key)) {
          val handler = new GpfdistHandler(bufferMap, sendBufferMap)
          val server = new Server()

          server.setHandler(handler)

          val serverHost = getServerHost(connectorOptions)
          val gpfdistService = new GpfdistService(
            serverHost,
            ports,
            bufferMap,
            sendBufferMap,
            new ServerWrapper(server),
            connectorOptions.timeoutInMillis)
          logger.debug(s"Service for $ports is being started....")
          gpfdistService.start()

          /**
           * spark local mode can run to fix source code
           */
          // if this is the driver (i.e., local mode), we need to stop the
          // service to prevent hanging when the application exits
          /*if (SparkEnv.get.executorId == "driver") {
            val sc = SparkSession.builder().getOrCreate().sparkContext
            sc.addSparkListener(new StopGpfdistServiceSparkListener(key))
          }*/
          servicesMap.put(key, gpfdistService)
        }
      }
    }

    val service = servicesMap.get(key)
    val serviceTimeout = service.getTimeout()
    if (serviceTimeout < connectorOptions.timeoutInMillis) {
      logger.warn(s"Unable to change the GpfdistService timeout after the service " +
        s"has started. GpfdistService started with timeout $serviceTimeout but " +
        s"a higher timeout (${connectorOptions.timeoutInMillis}) is configured. " +
        s"Ensure that the desired GpfdistService timeout is set for the first " +
        s"operation that accesses Greenplum.")
    }
    service
  }

  def stopAndRemove(key: ServiceKey): Unit = {
    val service = servicesMap.remove(key)
    if (service != null) {
      service.stop()
    } else {
      logger.warn(s"Unable to find service for $key")
    }
  }

  private def getServerHost(connectorOptions: ConnectorOptions): String = {
    if (StringUtils.isNotBlank(connectorOptions.networkInterface)) {
      val address = NetworkWrapper.getInet4AddressByNetworkInterfaceName(connectorOptions.networkInterface)
      if (address != null) {
        return address.getHostAddress
      }
    }
    ConnectorOptions.GPDB_DEFAULT_SERVER_HOST
  }

}
