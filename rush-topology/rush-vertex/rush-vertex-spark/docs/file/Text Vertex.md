# Text Vertex





### **Features**

The vertex provide text file to read and write.






### **Configuration**

If you run data by Spark engine. For the attachment parameters of cdv, please refer to ：[Text configuration](https://spark.apache.org/docs/latest/sql-data-sources-text.html). You also refer to the class of TextOptions.

Some important configurations are as follows：

| **Property Name** | Description                                                  | Require | **Default** | Read | Write |
| --------------- | ------------------------------------------------------------ | -------- | -------- | -------- | -------- |
| lineSep | Defines the line separator that should be used for reading or writing. |  | `\r`, `\r\n`, `\n` (for reading), `\n` (for writing) | ✅ | ✅ |
| wholetext | If true, read each file from input path(s) as a single row. |  | false | ✅ |  |





### FAQ

（1）How to read the data set in the entire text file as one piece of data？

​		The following parameters can be set:

```shell
wholetext=true
```

