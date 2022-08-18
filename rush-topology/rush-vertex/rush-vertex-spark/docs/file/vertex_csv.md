# CSV Vertex





### **Features**

The vertex provide csv file to read and write.






### **Configuration**

If you run data by Spark engine. For the attachment parameters of cdv, please refer to ：[CSV configuration](https://spark.apache.org/docs/latest/sql-data-sources-csv.html). You also refer to the class of CSVOptions.

Some important configurations are as follows：

| **Property Name** | Description                                                  | Require | **Default** | Read | Write |
| --------------- | ------------------------------------------------------------ | -------- | -------- | -------- | -------- |
| header       | For reading, uses the first line as names of columns. For writing, writes the names of columns as the first line. Note that if the given path is a RDD of Strings, this header option will remove all lines same with the header if exists. CSV built-in functions ignore this option. |  | false(official)<br/>true(rush) | ✅ | ✅ |
| delimiter | column separator |  | , | ✅ | ✅ |
| quote | Sets a single character used for escaping quoted values where the separator can be part of the value. For reading, if you would like to turn off quotations, you need to set not `null` but an empty string. For writing, if an empty string is set, it uses `u0000` (null character). | | " | ✅ | ✅ |
| inferSchema | Infers the input schema automatically from data. It requires one extra pass over the data. CSV built-in functions ignore this option. |  | false(official)<br/>true(rush) | ✅ |  |
| sep     | Sets a separator for each field and value. This separator can be one or more characters. |  | , | ✅ | ✅ |
| lineSep  | Defines the line separator that should be used for parsing/writing. Maximum length is 1 character. CSV built-in functions ignore this option. |  | `\r`, `\r\n` and `\n` (for reading), `\n` (for writing) | ✅ | ✅ |
| dateFormat | Sets the string that indicates a date format. |  | yyyy-MM-dd | ✅ | ✅ |
| timestampFormat | Sets the string that indicates a timestamp format. |  | yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX] | ✅ | ✅ |
| timestampNTZFormat | Sets the string that indicates a timestamp without timezone format. |  | yyyy-MM-dd'T'HH:mm:ss[.SSS] | ✅ | ✅ |
| maxColumns | Defines a hard limit of how many columns a record can have. |  | 20480 | ✅ |  |
| maxCharsPerColumn | Defines the maximum number of characters allowed for any given value being read. By default, it is -1 meaning unlimited length |  | -1 | ✅ |  |
| ignoreLeadingWhiteSpace | A flag indicating whether or not leading whitespaces from values being read/written should be skipped. |  | `false` (for reading), `true` (for writing) | ✅ | ✅ |
| ignoreTrailingWhiteSpace | A flag indicating whether or not trailing whitespaces from values being read/written should be skipped. |  | `false` (for reading), `true` (for writing) | ✅ | ✅ |





### FAQ

（1）There are the following standard CSV files. The header of the file is the data set schema. After reading, the column name becomes_ c0, _ C1.

```tex
name;age;job
Jorge;30;Developer
Bob;32;Developer
```

​		The query data is as follows:

```shell
+-----+---+---------+                                                           
|  _c0|_c1|      _c2|
+-----+---+---------+
| name|age|      job|
|Jorge| 30|Developer|
|  Bob| 32|Developer|
+-----+---+---------+
```

​		At this time, you need to set the following parameters:

```shell
header=true
```

（2）Why is the data type of all columns string after reading the CSV file data？

​		You need to set the following parameters for automatic inference：

```shell
inferSchema=true
```

（3）If there are the following CSV files, how should I read the data set correctly?

```tex
name||age||job@Jorge||30||Developer@Bob||32||Developer
```

​	At this time, row and column separators need to be set at the same time：

```shell
sep=||
lineSep=@
header=true
```

（4）If there is a large number of spaces in the data in the following CSV file, what should be done？

```tex
id;content
          1;           hello world          
2                  ;good morning                    
    3;        good afternoon               
```

​		The query data is as follows:	

```shell
+-------------------+-------------------------------------+
|id                 |content                              |
+-------------------+-------------------------------------+
|          1        |           hello world               |
|2                  |good morning                         |
|    3              |        good afternoon               |
+-------------------+-------------------------------------+
```

​		You need to set the following parameters for automatic inference：

```shell
ignoreLeadingWhiteSpace=true
ignoreTrailingWhiteSpace=true
```

