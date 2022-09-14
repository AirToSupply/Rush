package  tech.odes.rush.vertex.spark.gpdb

import io.pivotal.greenplum.spark.ConnectorUtils
import io.pivotal.greenplum.spark.conf.{ConnectorOptions, Default}
import org.apache.commons.lang.StringUtils

object ConnectorOptionsWrapper {

  import ConnectorOptions._

  def port(parameters: Map[String, String], env: Map[String, String] = sys.env): List[Int] = {
    val value = parameters.getOrElse[String](GPDB_NETWORK_PORT, "0")
    val portString = if (StringUtils.startsWith(value, GPDB_ENV_VAR_PREFIX)) {
      env.getOrElse(value.substring(GPDB_ENV_VAR_PREFIX.length), "0")
    } else {
      value
    }
    ConnectorUtils.parsePortString(portString)
  }
}
