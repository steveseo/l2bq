l2bq

This project is based on the Mache framework(https://github.com/StreakYC/mache)

Live example on Google App Engine: 

# Project Description
 - [Source Analysis of mache project](https://drive.google.com/folderview?id=0Bxujl8fMG4jSamFvRGpHNkN5akU&usp=sharing)

# Installation
 - Clone the repository from github:

```
$ git clone {insert repo url} 
```

 - Copy l2bq-0.0.1.jar to your project's war/WEB-INF/lib directory. Add the jar to your Eclipse build path.
 - If your project doesn't otherwise interact with BigQuery, you'll need to add it to project dependencies: Right click on your project, select Google->Add Google APIs->BigQuery API. Make sure you have the latest version of the Google Eclipse Plugin to enable this option.
 - Add the following snippet to your war/WEB-INF/web.xml file:

```
<servlet>
  <servlet-name>LogExportCronTask</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.LogExportCronTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>LogExportCronTask</servlet-name>
  <url-pattern>/logging/logExportCron</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>StoreLogsInCloudStorageTask</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.StoreLogsInCloudStorageTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>StoreLogsInCloudStorageTask</servlet-name>
  <url-pattern>/logging/storeLogsInCloudStorage</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>LoadCloudStorageToBigqueryTask</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.LoadCloudStorageToBigqueryTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>LoadCloudStorageToBigqueryTask</servlet-name>
  <url-pattern>/logging/loadCloudStorageToBigquery</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>DeleteCompletedCloudStorageFilesTask</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.DeleteCompletedCloudStorageFilesTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>DeleteCompletedCloudStorageFilesTask</servlet-name>
  <url-pattern>/logging/deleteCompletedCloudStorageFilesTask</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>BigqueryStatusServlet</servlet-name>
  <servlet-class>com.l2bq.logging.analysis.BigqueryStatusServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>BigqueryStatusServlet</servlet-name>
  <url-pattern>/logging/bigqueryStatus</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>DatastoreExportServlet</servlet-name>
  <servlet-class>com.l2bq.datastore.analysis.DatastoreExportServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>DatastoreExportServlet</servlet-name>
  <url-pattern>/logging/datastoreExport</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>BuiltinDatastoreToBigqueryCronTask</servlet-name>
  <servlet-class>com.l2bq.datastore.analysis.builtin.BuiltinDatastoreToBigqueryCronTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>BuiltinDatastoreToBigqueryCronTask</servlet-name>
  <url-pattern>/bqlogging/builtinDatastoreExport</url-pattern>
</servlet-mapping>

<servlet>
  <servlet-name>BuiltinDatastoreToBigqueryIngestorTask</servlet-name>
  <servlet-class>com.l2bq.datastore.analysis.builtin.BuiltinDatastoreToBigqueryIngesterTask</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>BuiltinDatastoreToBigqueryIngestorTask</servlet-name>
  <url-pattern>/bqlogging/builtinDatastoreToBigqueryIngestorTask</url-pattern>
</servlet-mapping>

<security-constraint>
  <web-resource-collection>
    <url-pattern>/logging/*</url-pattern>
  </web-resource-collection>
  <auth-constraint>
    <role-name>admin</role-name>
  </auth-constraint>
</security-constraint>
```
