# Rush Topology Runner Starter

​	

​		Apache spark is used as the execution engine to complete the hybrid calculation of rush topology. This module relies on a unified entry as a native driver. The supported topology modes are as follows：

| Topological Structure          | Runner Class                                                 |
| ------------------------------ | ------------------------------------------------------------ |
| vertex(source) -> vertex(sink) | tech.odes.rush.bootstrap.spark.starter.SimpleTopologyBootstrap |




### Features

​		Through Java SPI, the mixed computation of heterogeneous data sources is completed based on pluggable topology operators（vertex plugins and edge plugins）.

​		Currently, the following vertex heterogeneous data sources are supported:

| Vertex name                                                  | Vertex type | Build-in | Vertex export | Vertex import |
| ------------------------------------------------------------ | ----------- | -------- | ------------- | ------------- |
| [jdbc](../../rush-topology/rush-vertex/rush-vertex-spark/docs/jdbc/vertex_jdbc.md) | JDBC        | ✅        | ✅             | Doing         |
| [hudi](../../plugins/spark/vertex/lakehouse/rush-vertex-spark-hudi/docs/vertex_hudi.md) | LakeHouse   |          | ✅             | ✅             |
| [excel](../../plugins/spark/vertex/file/rush-vertex-spark-excel/docs/vertex_excel.md) | File        |          | ✅             |               |
| [csv](../../rush-topology/rush-vertex/rush-vertex-spark/docs/file/vertex_csv.md) | File        | ✅        | ✅             |               |
| [text](../../rush-topology/rush-vertex/rush-vertex-spark/docs/file/vertex_text.md) | File        | ✅        | ✅             |               |
| es                                                           | Doc         |          | doing         |               |
| mongo                                                        | Doc         |          | doing         |               |
| [greenplum](../../plugins/spark/vertex/lakehouse/rush-vertex-spark-gpdb/docs/vertex_gpdb.md) | HTAP        |          | doing         | ✅             |
| doris                                                        | MPP         |          | doing         | doing         |

【TIPS】

（1）The vertex plugin marked as **build-in** above is the default plugin, and in the module dependency of **rush-topology-spark-bootstrap**, it does not need to be extended and is used out of the box.

（2）The vertex plugin not marked as **build-in** above is an extension plugin, and when running the **Rush Application** through **spark-sumbit**, specify the required plugin through the **-- jar** parameter. These extension plugin dependencies are defined in the **plugins** directory in the project. When using the plugins need to be packaged and placed under the classpath that spark can load.





### Usage

​		It will be easier to complete heterogeneous data source calculation through rush topology spark bootstrap.You can obtain the JAR file through the following command:

```shell
mvn clean package -pl external/rush-topology-spark-bootstrap -am
```





### Bootstrap Runner

#### 1.SimpleTopologyBootstrap

​		**SimpleTopologyBootstrap** completes the **end-to-end** data collection and sharing of **a pair of vertexes**, and the current topology structure does not include the conversion processing of the edge operators.

【Options】

| Name                 | Description                                                  | Require | TIPS                                                         |
| -------------------- | ------------------------------------------------------------ | ------- | ------------------------------------------------------------ |
| --vertex-source-type | Vertex of topology source type.                              | ✅       | （1）The parameter value can refer to the **name** method of each **SparkVertex** implementation class.<br/>（2）The parameter value needs to refer to the configuration item in the vertex plugin document. |
| --vertex-sink-type   | Vertex of topology sink type.                                | ✅       | （1）The parameter value about source can refer to the **name** method of each **SparkVertex** implementation class.<br/>（2）The parameter value about sink needs to refer to the configuration item in the vertex plugin document. |
| --vertex-source      | Any **source** configuration that can be set in the properties file (using the CLI parameter --props) can also be passed command line using this parameter. This can be **repeated**. |         |                                                              |
| --vertex-sink        | Any sink configuration that can be set in the properties file (using the CLI parameter --props) can also be passed command line using this parameter. This can be **repeated**. |         |                                                              |
| --props              | Path to properties file on localfs or dfs, with configurations for simple rush topology. |         | For vertex related parameters, you need to pass **vertex.source.*** or **vertex.sink.*** The prefix is spliced to distinguish the self generated parameters of each plugin type. |
| --help（-h）         | Option helps                                                 |         |                                                              |

【Internal Options】

| Name     | Description                       | Require | Default |
| -------- | --------------------------------- | ------- | ------- |
| path     | refer to spark dataframe path     |         |         |
| saveMode | refer to spark dataframe saveMode |         | append  |

【Example】

The following is an example of how to use the simpletopologybotstrap runner with the collected excel file data entering the lake.Two methods are provided below:

（1）define options

​		command：

```shell
$SPARK_HOME/bin/spark-submit \
--master yarn \
--deploy-mode client \
--executor-memory 1g \
--driver-memory 2g \
--num-executors 2 \
--executor-cores 2 \
--jars hdfs://ns/lib/hudi-spark3.1-bundle_2.12-0.11.0.jar,hdfs://ns/rush/lib/rush-vertex-spark-* \
--class tech.odes.rush.bootstrap.spark.starter.SimpleTopologyBootstrap \
hdfs://ns/rush/run/rush-topology-spark-bootstrap-0.0.1.jar \
--vertex-source-type excel \
--vertex-sink-type hudi \
--vertex-source path=/dataset/test_simple.xlsx \
--vertex-source header=true \
--vertex-source inferSchema=true \
--vertex-source usePlainNumberFormat=true \
--vertex-sink path=/tmp/hoodie/tb \
--vertex-sink hoodie.datasource.write.precombine.field=id \
--vertex-sink hoodie.datasource.write.recordkey.field=id \
--vertex-sink hoodie.datasource.write.partitionpath.field=dt \
--vertex-sink hoodie.datasource.write.table.name=ods_order_detail
```

（2）define properties

​		command：

```shell
$SPARK_HOME/bin/spark-submit \
--master yarn \
--deploy-mode client \
--executor-memory 1g \
--driver-memory 2g \
--num-executors 2 \
--executor-cores 2 \
--jars hdfs://ns/lib/hudi-spark3.1-bundle_2.12-0.11.0.jar,hdfs://ns/rush/lib/rush-vertex-spark-* \
--class tech.odes.rush.bootstrap.spark.starter.SimpleTopologyBootstrap \
hdfs://ns/rush/run/rush-topology-spark-bootstrap-0.0.1.jar \
--vertex-source-type excel \
--vertex-sink-type hudi \
--props hdfs://ns/rush/conf/bootstrap/example.properties
```

​		example.properties:

```properties
vertex.source.path=/dataset/test_simple.xlsx
vertex.source.header=true
vertex.source.inferSchema=true
vertex.source.usePlainNumberFormat=true
vertex.sink.path=/tmp/hoodie/tb
vertex.sink.hoodie.datasource.write.precombine.field=id
vertex.sink.hoodie.datasource.write.recordkey.field=id
vertex.sink.hoodie.datasource.write.partitionpath.field=dt
vertex.sink.hoodie.datasource.write.table.name=ods_order_detail
```

