/*
 * Copyright 2012 Rewardly Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.l2bq.logging.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService;
import com.google.appengine.api.log.LogService.LogLevel;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class StoreLogsInCloudStorageTask extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");

		String startMsStr = AnalysisUtility.extractParameterOrThrow(req, AnalysisConstants.START_MS_PARAM);
		long startMs = Long.parseLong(startMsStr);

		String endMsStr = AnalysisUtility.extractParameterOrThrow(req, AnalysisConstants.END_MS_PARAM);
		long endMs = Long.parseLong(endMsStr);

		String bucketName = AnalysisUtility.extractParameterOrThrow(req, AnalysisConstants.BUCKET_NAME_PARAM);
		String queueName = AnalysisUtility.extractParameterOrThrow(req, AnalysisConstants.QUEUE_NAME_PARAM);
		
		String logLevelStr = AnalysisUtility.extractParameterOrThrow(req, AnalysisConstants.LOG_LEVEL_PARAM);
		LogLevel logLevel = null;
		if (!"ALL".equals(logLevelStr)) {
			logLevel = LogLevel.valueOf(logLevelStr);
		}

		String exporterSetClassStr = AnalysisUtility.extractParameterOrThrow(req, AnalysisConstants.BIGQUERY_FIELD_EXPORTER_SET_PARAM);
		BigqueryFieldExporterSet exporterSet = AnalysisUtility.instantiateExporterSet(exporterSetClassStr);
		String schemaHash = AnalysisUtility.computeSchemaHash(exporterSet);

		List<String> fieldNames = new ArrayList<String>();
		List<String> fieldTypes = new ArrayList<String>();

		AnalysisUtility.populateSchema(exporterSet, fieldNames, fieldTypes);

		String respStr = generateExportables(startMs, endMs, bucketName, String.format("%s_%s", exporterSet.getPrefix(), schemaHash), exporterSet, fieldNames, fieldTypes, logLevel);
		Queue taskQueue = QueueFactory.getQueue(queueName);

		TaskOptions to = Builder.withUrl(AnalysisUtility.getRequestBaseName(req) + "/loadCloudStorageToBigquery?" + req.getQueryString())
				.method(Method.GET);
		
		taskQueue.add(to);
		resp.getWriter().println(respStr);

				
	}

	protected String generateExportables(long startMs, long endMs, String bucketName, String schemaHash,  BigqueryFieldExporterSet exporterSet, List<String> fieldNames, List<String> fieldTypes, LogLevel logLevel) throws IOException {
		List<BigqueryFieldExporter> exporters = exporterSet.getExporters();

		LogService ls = LogServiceFactory.getLogService();
		LogQuery lq = new LogQuery();
		lq = lq.startTimeUsec(startMs * 1000)
				.endTimeUsec(endMs * 1000)
				.includeAppLogs(true);

		if (logLevel != null) {
			lq = lq.minLogLevel(logLevel);
		}
		
		List<String> appVersions = exporterSet.applicationVersionsToExport();
		if (appVersions != null && appVersions.size() > 0) {
			lq = lq.majorVersionIds(appVersions);
		}

		String fileKey = AnalysisUtility.createLogKey(schemaHash, startMs, endMs);
		
		
		FancyFileWriter writer = new FancyFileWriter(bucketName, fileKey);
		Iterable<RequestLogs> logs = ls.fetch(lq);

		int resultsCount = 0;
		for (RequestLogs log : logs) {
			// filter logs
			if (exporterSet.skipLog(log)) {
				continue;
			}
			int exporterStartOffset = 0;
			int currentOffset = 0;
			boolean isAppLogProcessed = false;
			for (BigqueryFieldExporter exporter : exporters) {
				exporter.processLog(log);
				
				if ( exporter.isAppLogProcessed() )
				{
					isAppLogProcessed = true;
					int curOffset = 0;
					while ( exporter.nextAppLogLine() && exporter.getAppLogLines().size() > 0 )
					{
						curOffset = currentOffset;
						while (curOffset < exporterStartOffset + exporter.getFieldCount()) {
							if (curOffset > 0) {
								writer.append(",");
							}
							Object fieldValue = exporter.getField(fieldNames.get(curOffset));
							if (fieldValue == null) {
								throw new InvalidFieldException(
										"Exporter " + exporter.getClass().getCanonicalName() + 
										" didn't return field for " + fieldNames.get(curOffset));
							}
							
							writer.append(AnalysisUtility.formatCsvValue(fieldValue, fieldTypes.get(curOffset)));
							curOffset++;
						}
						writer.append("\n");
					}
					currentOffset = curOffset;
				}
				else
				{
					while (currentOffset < exporterStartOffset + exporter.getFieldCount()) {
						if (currentOffset > 0) {
							writer.append(",");
						}
						Object fieldValue = exporter.getField(fieldNames.get(currentOffset));
						if (fieldValue == null) {
							throw new InvalidFieldException(
									"Exporter " + exporter.getClass().getCanonicalName() + 
									" didn't return field for " + fieldNames.get(currentOffset));
						}
						
						writer.append(AnalysisUtility.formatCsvValue(fieldValue, fieldTypes.get(currentOffset)));
						currentOffset++;
					}
					
				}
				exporterStartOffset += exporter.getFieldCount();
			}
			if ( !isAppLogProcessed )
				writer.append("\n");
			
			
			resultsCount++;
		}
		writer.closeFinally();
		return "Saved " + resultsCount + " logs to gs://" + bucketName + "/" + fileKey;
	}
	
	protected String getDefaultBigqueryFieldExporterSetBag() {
		return "com.l2bq.logging.analysis.exporter.ExporterSetBag";
	}
}
