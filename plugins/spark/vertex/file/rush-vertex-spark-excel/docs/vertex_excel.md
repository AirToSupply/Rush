# Excel Vertex





### **Features**

Excel Vertex adopt ths third party package [spark-excel (0.17.1)](https://github.com/crealytics/spark-excel)

【TIPS】

(1) The above library need to be specified through the --jar parameter when submitting the spark application.

(2) If --jar parameter is not specified, you need to package via shading. 





### **Configuration**

For the parameters of excel, please refer to ：[spark-excel option configuration](https://github.com/crealytics/spark-excel/blob/main/src/main/scala/com/crealytics/spark/v2/excel/ExcelOptions.scala).

Some important configurations are as follows：

| **Property Name** | **Description**                                                  | **Require** | **Default** | **Read** | **Wriite** |
| --------------- | ------------------------------------------------------------ | -------- | -------- | --------------- | --------------- |
| header | Have header line when reading and writing.<br />**Note**: it is not recommended to set it to false, because it will prevent data from entering the other storages. |  | true | ✅ | ✅ |
| inferSchema | If true, the input data structure is automatically inferred from the data. |  | false(official)<br/>true(rush) | ✅ |   |
| dataAddress | Data address, default to everything. By default, the reading and writing starts from A1 on the first sheet page. |  | A1 | ✅ |             |
| ignoreAfterHeader | Number of rows to ignore after header. Only in reading | | 0 | ✅ | |
| treatEmptyValuesAsNulls | Treat empty values as null. |  | true | ✅ |   |
| setErrorCellsToFallbackValues | Convert the error cell to null . If true, any ERROR cell value (such as #N/A ) will be converted to a zero value of the column data type. | | false | ✅ | |
| usePlainNumberFormat | If true, format the cells without rounding and scientific notations. | | false | ✅ | |
| timestampFormat | Timestamp format. | | yyyy-mm-dd hh:mm:ss[.fffffffff] | ✅ | ✅ |
| maxRowsInMemory | Maximum number of memory operation lines. If this option is set, a streaming reader is used to help with large files.<br />Optional parameter for using a streaming reader which can help with big files (will fail if used with xls format files). | | | ✅ | |
| excerptSize | If the structure is set and inferred, it indicates the number of rows from which the structure is inferred. | | 10 | ✅ | |
| workbookPassword | Workbook password. | | | ✅ | |
| ignoreAfterHeader | Number of rows to ignore after header. Only in reading. | | 0 | ✅ | |





### FAQ

（1）Why the collected data is all string type？

You can specify the following parameters to automatically infer the data type.

```shell
inferSchema=true
```

（2）When an excel file has multiple sheets, can you specify that you need to read the data in the sheet?

For example, there are two sheets, tabelle1 and tabelle2, in the current excel file. Now I just want to collect the data in the sheet named tabelle2. The following parameters can be set：

```shell
dataAddress=Tabelle2!A1
```

If the sheet name contains characters such as spaces, it needs to be enclosed in single quotes. Like this below：

```shell
dataAddress='Tabelle2'!A1
```

（3）What should I do if I want to intercept part of the data in the sheet data as the collection object?

Examples are as follows：

![01.dataAddressByRange](./image/01.dataAddressByRange.jpg)

You can set the parameters like this：

```shell
dataAddress=Tabelle2!A1:C4
```

（4）When excel has a number type, excel sometimes performs a certain degree of rounding and scientific notation display. What should I do if I want to collect in the original way?

For example, the following excel is like this：

![02.plainNumber01](./image/02.plainNumber01.jpg)

At this time, the following parameters can be set：

```shell
usePlainNumberFormat=true
```

After treatment, it is as follows：

![03.plainNumber02](./image/03.plainNumber02.jpg)

（5）What should I do if the collected excel has a password？

You can set the following parameters：

```shell
options.workbookPassword=...
```

（6）Does it support reading multiple Excel files with consistent data structure？

For example, in the following example, we want to collect files in the partition quarter = 4 directory：

![04.multiRead](./image/04.multiRead.jpg)

load data path for ` /.../ca_dataset/2019/Quarter=4/*.xlsx`
