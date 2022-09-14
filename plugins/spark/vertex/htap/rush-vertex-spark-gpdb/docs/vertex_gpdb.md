# GreenPlum Vertex





### **Features**

Greenplum Database (GPDB) is an advanced, fully featured, open source data warehouse, based on PostgreSQL. It provides powerful and rapid analytics on petabyte scale data volumes. Uniquely geared toward big data analytics, Greenplum Database is powered by the world’s most advanced cost-based query optimizer delivering high analytical query performance on large data volumes. So, the vertex endpoint provides support for reading and writing **GPDB**.






### **Configuration**

Currently vertex uses the official connector. You can refer to [[VMware Tanzu Greenplum Connector for Apache Spark](https://docs.vmware.com/en/VMware-Tanzu-Greenplum-Connector-for-Apache-Spark/2.1/tanzu-greenplum-connector-spark/GUID-index.html)

For the more parameters of gpdb, please refer to [About Connector Options](https://docs.vmware.com/en/VMware-Tanzu-Greenplum-Connector-for-Apache-Spark/2.1/tanzu-greenplum-connector-spark/GUID-options.html) 

You can also refer to the code file about **GreenplumOptions.scala**.

If you want to know the usage and syntax of gpdb sql, please refer to [SQL Commands](https://docs.vmware.com/en/VMware-Tanzu-Greenplum/6/greenplum-database/GUID-ref_guide-sql_commands-sql_ref.html)

If you need all the dependency libraries about gpdb，please refer to [vmware-tanzu-greenplum releases](https://network.tanzu.vmware.com/products/vmware-tanzu-greenplum/#/releases)

If you want to know more about gpdb, please refer to [gpdb source code](https://github.com/greenplum-db/gpdb)

#### Extension Options

These parameters are the parameter options of the current vertex function.

| **Property Name** | Default                                          | Operation | **Description** |
| --------------- | ------------------------------------------------------------ | -------- | -------- |
| - | -       | -         | - |





### FAQ

**1.How to build the dependency libraries of g gpdb and spark?**

The artifactId of the dependency are not changed in the public maven service warehouse, so it needs to be built by itself. Here you can refer to the [official documentation of gpdb](https://network.tanzu.vmware.com/products/vmware-tanzu-greenplum/#/releases/). 

We select 【RELEASE 6.21.2】->【Greenplum Connectors】->【Greenplum Spark Connector 2.1.2 for Scala 2.12】. Then unzip the file to get a file named greenplum-connector-apache-spark-scala_2.12-2.1.2.jar. 

If you want to install package to develop by maven，the command is as follows:

```shell
mvn install:install-file \
-Dfile=/opt/module/greenplum-connector-apache-spark-scala_2.12-2.1.2.jar \
-DgroupId=io.pivotal.greenplum.spark \
-DartifactId=greenplum-spark_2.12 \
-Dversion=2.1.2 \
-Dpackaging=jar
```

If you want to deploy to private maven respority，the command is as follows:

```shell
mvn deploy:deploy-file \
-DgroupId=io.pivotal.greenplum.spark \
-DartifactId=greenplum-spark_2.12 \
-Dversion=2.1.2 \
-Dfile=/opt/module/greenplum-connector-apache-spark-scala_2.12-2.1.2.jar \
-Durl=http://<nexus-ip>:<nexus-port>/repository/maven-releases/ \
-DrepositoryId=releases
```

After completing the above construction, you can import the following coordinates into the pom.xml file:

```xml
<dependency>
    <groupId>io.pivotal.greenplum.spark</groupId>
    <artifactId>greenplum-spark_2.12</artifactId>
    <version>2.1.2</version>
</dependency>
```



**2.In overwrite write mode, database objects such as the constraints of the target table will be lost.**

In this mode, the target table will be deleted by default, and then a new table will be created, which will result in the loss of additional database objects (such as indexes, comments, etc.). The following parameters need to be specified：

```properties
truncate=true
```

The default value is `false`; the Connector drops and then re-creates the target table before it writes any data. When `true`, the Connector truncates the target table before writing any data.