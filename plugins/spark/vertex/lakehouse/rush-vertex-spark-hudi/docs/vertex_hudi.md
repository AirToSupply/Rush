# Apache Hudi Vertex





### **Features**

Hudi brings transactions, record-level updates/deletes and change streams to data lakes! So, the vertex endpoint of name is **hudi**.






### **Configuration**

For the more parameters of hudi, please refer to ：[Spark Datasource Configs](https://hudi.apache.org/docs/basic_configurations#SPARK_DATASOURCE). 

Some important configurations are as follows：

#### Read Options

For the more parameters of hudi, please refer to class of DataSourceReadOptions.

| **Property Name** | Description                                                  | Require | **Default** |
| --------------- | ------------------------------------------------------------ | -------- | -------- |
| path | hoodie table base path | ✅ |  |
| hoodie.datasource.query.type | Refer to DataSourceReadOptions.QUERY_TYPE.<br/><br/>Whether data needs to be read, in incremental mode (new data since an instantTime) (or) Read Optimized mode (obtain latest view, based on base files) (or) Snapshot mode (obtain latest view, by merging base and (if any) log files).<br/><br/>Values: snapshot\|read_optimized\|incremental |  | snapshot |
| hoodie.datasource.read.begin.instanttime | Refer to DataSourceReadOptions.BEGIN_INSTANTTIME.<br/><br/>Instant time to start incrementally pulling data from. The instanttime here need not necessarily correspond to an instant on the timeline. New data written with an instant_time > BEGIN_INSTANTTIME are fetched out. For e.g: ‘20170901080000’ will get all new data written after Sep 1, 2017 08:00AM. |  |   |
| hoodie.datasource.read.end.instanttime | Refer to DataSourceReadOptions.END_INSTANTTIME.<br/><br/>Instant time to limit incrementally fetched data to. New data written with an instant_time <= END_INSTANTTIME are fetched out. |  |             |
| as.of.instant | Refer to DataSourceReadOptions.TIME_TRAVEL_AS_OF_INSTANT.<br/><br/>The query instant for time travel. Without specified this option, we query the latest snapshot. |  |   |

#### Write Options

For the more parameters of hudi, please refer to class of DataSourceWriteOptions.

| Property Name                               | Description                                                  | Require | **Default**            |
| ------------------------------------------- | ------------------------------------------------------------ | ------- | ---------------------- |
| path                                        | hoodie table base path                                       | ✅       |                        |
| hoodie.datasource.write.operation           | Refer to DataSourceWriteOptions.OPERATION.<br/><br/>Whether to do upsert, insert or bulkinsert for the write operation. Use bulkinsert to load new data into a table, and there after use upsert/insert. bulk insert uses a disk based write path to scale to load large inputs without need to cache it.You also can refer to hudi source code about WriteOperationType. |         | upsert                 |
| hoodie.datasource.write.recordkey.field     | Refer to KeyGeneratorOptions.RECORDKEY_FIELD_NAME. <br/><br/>Record key field. Value to be used as the `recordKey` component of `HoodieKey`.Actual value will be obtained by invoking .toString() on the field value. Nested fields can be specified using the dot notation eg: `a.b.c` |         | uuid                   |
| hoodie.datasource.write.precombine.field    | Refer to HoodieWriteConfig.PRECOMBINE_FIELD_NAME.<br/><br/>Field used in preCombining before actual write. When two records have the same key value, we will pick the one with the largest value for the precombine field, determined by Object.compareTo(..). |         | ts                     |
| hoodie.datasource.write.partitionpath.field | Refer to KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.<br/><br/>partition path field. Value to be used at the partitionPath component of HoodieKey. Actual value ontained by invoking .toString() |         |                        |
| hoodie.datasource.write.keygenerator.class  | Refer to HoodieWriteConfig.KEYGENERATOR_CLASS_NAME.<br/><br/>Key generator class, that implements `org.apache.hudi.keygen.KeyGenerator` extract a key out of incoming records. |         |                        |
| hoodie.datasource.write.keygenerator.type   | Easily configure one the built-in key generators, instead of specifying the key generator class.Currently supports SIMPLE, COMPLEX, TIMESTAMP, CUSTOM, NON_PARTITION, GLOBAL_DELETE |         | SIMPLE                 |
| hoodie.table.name          | Refer to HoodieWriteConfig.TBL_NAME.<br/><br/>hoodie table name | ✅       |                        |
| hoodie.base.path                            | Refer to HoodieWriteConfig.BASE_PATH.<br/><br/>Base path on lake storage, under which all the table data is stored. Always prefix it explicitly with the storage scheme (e.g hdfs://, s3:// etc). Hudi stores all the main meta-data about commits, savepoints, cleaning audit logs etc in .hoodie directory under this base path directory. |         |                        |
| hoodie.upsert.shuffle.parallelism           | Parallelism to use for upsert operation on the table. Upserts can shuffle data to perform index lookups, file sizing, bin packing records optimally into file groups. |         | 200(hudi)<br/>32(rush) |
| hoodie.insert.shuffle.parallelism           | Parallelism for inserting records into the table. Inserts can shuffle data before writing to tune file sizes and optimize the storage layout. |         | 200(hudi)<br/>32(rush) |
| hoodie.bulkinsert.shuffle.parallelism       | For large initial imports using bulk_insert operation, controls the parallelism to use for sort modes or custom partitioning done before writing records to the table. |         | 200(hudi)<br/>32(rush) |
| hoodie.delete.shuffle.parallelism           | Parallelism used for “delete” operation. Delete operations also performs shuffles, similar to upsert operation. |         | 200(hudi)<br/>32(rush) |

#### Hive Sync Options

For the more parameters of hudi, please refer to class of HiveSyncConfigHolder and HoodieSyncConfig.

| Property Name                                         | Description                                                  | Require | **Default**                                                 |
| ----------------------------------------------------- | ------------------------------------------------------------ | ------- | ----------------------------------------------------------- |
| hoodie.datasource.hive_sync.enable                    | Refer to HiveSyncConfig.HIVE_SYNC_ENABLED.<br/><br/>When set to true, register/sync the table to Apache Hive metastore. |         | false                                                       |
| hoodie.datasource.meta.sync.enable                    | Refer to HoodieSyncConfig.META_SYNC_ENABLED.<br/><br/>Enable Syncing the Hudi Table with an external meta store or data catalog. |         | false                                                       |
| hoodie.datasource.hive_sync.database                  | Refer to HoodieSyncConfig.META_SYNC_DATABASE_NAME.<br/><br/>The name of the destination database that we should sync the hudi table to. |         | default                                                     |
| hoodie.datasource.hive_sync.table                     | Refer to HoodieSyncConfig.META_SYNC_TABLE_NAME.<br/><br/>The name of the destination table that we should sync the hudi table to. |         | unknown                                                     |
| hoodie.datasource.hive_sync.jdbcurl                   | Refer to HiveSyncConfig.HIVE_URL.<br/><br/>Hive metastore url |         | jdbc:hive2://localhost:10000                                |
| hoodie.datasource.hive_sync.username                  | Refer to HiveSyncConfig.HIVE_USER.<br/><br/>Hive user name to use |         | hive                                                        |
| hoodie.datasource.hive_sync.password                  | Refer to HiveSyncConfig.HIVE_PASS.<br/><br/>Hive password to use |         | Hive                                                        |
| hoodie.datasource.hive_sync.partition_fields          | Refer to  HoodieSyncConfig.META_SYNC_PARTITION_FIELDS.<br/><br/>Field in the table to use for determining hive partition columns. |         |                                                             |
| hoodie.datasource.hive_sync.partition_extractor_class | Refer to HoodieSyncConfig.META_SYNC_PARTITION_EXTRACTOR_CLASS.<br/><br/>Class which implements PartitionValueExtractor to extract the partition values, default 'SlashEncodedDayPartitionValueExtractor'. |         | org.apache.hudi.hive.SlashEncodedDayPartitionValueExtractor |





