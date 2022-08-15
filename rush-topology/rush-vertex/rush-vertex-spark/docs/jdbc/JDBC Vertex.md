# JDBC Vertex





### **Features**

JDBC represents most common relational databases (such as MySQL, Oracle and MSSQL etc.) or data sources with JDBC protocol, such as Hive. So, the vertex endpoint of name is **jdbc**.






### **Configuration**

For the attachment parameters of jdbc, please refer to ：[JDBC configuration](https://github.com/apache/spark/blob/master/sql/core/src/main/scala/org/apache/spark/sql/execution/datasources/jdbc/JDBCOptions.scala). 

Some important configurations are as follows：

| **Property Name** | Description                                                  | Require | **Default** | Read | Write |
| --------------- | ------------------------------------------------------------ | -------- | -------- | -------- | -------- |
| url             | Database connection address URL. e.g. MySQL is jdbc:mysql://ip:port/dbname?... | ✅ |   | ✅ | ✅ |
| driver          | Database driven class name. e.g. MySQL5.x is com.mysql.jdbc.Driver. | ✅ |   | ✅ | ✅ |
| user            | Database access username.                                    | ✅ |             | ✅ | ✅ |
| password        | Database access password.                                    | ✅ |   | ✅ | ✅ |
| dbtable         | Database table name.                                         | ✅ (query can not be specified) |   | ✅ | ✅ |
| query           | Table name or a table subquery.  <br />Both query and dbtable just be specified at the same time.                            | ✅ (dbtable can not be specified) |   | ✅ | ✅ |
| partitionColumn | The column used to partition. <br />Must be a number, date, or timestamp column in a related table. |  |  | ✅ |  |
| lowerBound      | The lower bound of partition column                          |  |  | ✅ |  |
| upperBound      | The upper bound of the partition column                      |  |  | ✅ |  |
| numPartitions   | The number of partitions. <br />The maximum number of partitions a table read can use for parallelism. This also determines the maximum number of concurrent JDBC connections |  |  | ✅ | ✅ |
| queryTimeout | Tthe number of seconds the driver will wait for a Statement object to execute to the given number of seconds. Zero means there is no limit, default: 0. |  | 0 | ✅ | ✅ |
| fetchsize | Pull the number of strips. <br />It determines how many rows to get for each round trip, which helps the performance of the jdbc driver. The jdbc driver defaults to a lower get size. |  | 0 | ✅ |  |
| sessionInitStatement | An option to execute custom SQL before fetching data from the remote DB. <br />Execute the SQL statement (or pl/ SQL block) customized by this option after opening each database session to the remote database and before starting reading data. Use it to implement the session initialization code. <br />Example: <br />option("sessionInitStatement", """BEGIN execute immediate 'alter session set "_serial_direct_read"=true'; END;""") |  |  | ✅ |  |
| customSchema | Custom data structure.<br />Used to customize the structure when reading data from the JDBC connector. For example: id DECIMAL(38,0),name STRING<br />You can also specify some fields, and other fields use the default type mapping.<br />The column name should be the same as the corresponding column name of the JDBC table. The user can specify the corresponding data type of sparksql without using the default value |  |  | ✅ |  |
| pushDownPredicate | An option to allow/disallow pushing down predicate into JDBC data source, default: true<br />In this case, spark will push down the filter to the JDBC data source as much as possible.<br/><br/>Otherwise, if set to false, no filter will be pushed down to the JDBC data source, so all filters will be processed by spark.<br/><br/>When spark performs predicate filtering faster than JDBC data sources, predicate pushdown is usually turned off. |  | true | ✅ |  |

You can refer to spark guide document about jdbc of [Data Source Option]((https://spark.apache.org/docs/latest/sql-data-sources-jdbc.html)).





### FAQ

（1）How to perform read optimization for tables that collect large amounts of data？

You need to specify the four parameters partitionColumn, lowerBound, upperBound, and numPartitions at the same time. 

**partitionColumn**：the field must be a numeric, date, or timestamp column from the table in question.

**lowerBound and upperBound**：Notice that `lowerBound` and `upperBound` are just used to decide the partition stride, not for filtering the rows in table. So all rows in the table will be partitioned and returned. 

**numPartitions**：The number of partitions. <br />The maximum number of partitions a table read can use for parallelism. This also determines the maximum number of concurrent JDBC connections

（2）Which domestic databases are currently supported？

Generally speaking, any database or engine with JDBC protocol can collect dataset.

Currently, only dameng databases are supported. At this time, you only need to specify '--dialect dm' in the startup command. More domestic databases will be supported in the future.

Here are some blogs about dameng database for reference：

a. [Java JDBC operation dameng database](https://eco.dameng.com/docs/zh-cn/app-dev/java-jdbc.html)

b. [Get started quickly with dameng database](https://eco.dameng.com/docs/zh-cn/start/index.html)

c. [Dameng database driver dependency](https://mvnrepository.com/artifact/com.dameng/DmJdbcDriver18)
