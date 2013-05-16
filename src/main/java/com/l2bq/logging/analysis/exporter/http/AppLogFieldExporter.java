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

package com.l2bq.logging.analysis.exporter.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogService.LogLevel;
import com.google.appengine.api.log.RequestLogs;
import com.google.gson.Gson;
import com.l2bq.logging.analysis.BigqueryFieldExporter;

/**
 * @author Junki Kim(wishoping@gmail.com)
 * @date 2013. 4. 29.
 */
public class AppLogFieldExporter implements BigqueryFieldExporter {
	private static final long KILO = 1024;
	private static final long MAX_SIZE = 40;
	
	private Map<LogLevel, Integer> LEVEL_PRIORITY = new HashMap<LogLevel, Integer>();
	private Map<LogLevel, String> LEVEL_PRIORITY_NAME = new HashMap<LogLevel, String>();
	
	private String [] fieldNames = new String[]{
		"level", "app_logs"	
	};
	
	private String [] fieldTypes = new String[]{
		"string", "string"	
	};
	
	private String level = "";
	private String logs = "";
	
	public AppLogFieldExporter()
	{
		super();
		
		LEVEL_PRIORITY.put(LogLevel.DEBUG, 0);
		LEVEL_PRIORITY.put(LogLevel.INFO, 1);
		LEVEL_PRIORITY.put(LogLevel.WARN, 2);
		LEVEL_PRIORITY.put(LogLevel.ERROR, 3);
		LEVEL_PRIORITY.put(LogLevel.FATAL, 4);
		
		LEVEL_PRIORITY_NAME.put(LogLevel.DEBUG, "Debug");
		LEVEL_PRIORITY_NAME.put(LogLevel.INFO, "Info");
		LEVEL_PRIORITY_NAME.put(LogLevel.WARN, "Warning");
		LEVEL_PRIORITY_NAME.put(LogLevel.ERROR, "Error");
		LEVEL_PRIORITY_NAME.put(LogLevel.FATAL, "Critical");
	}
	
	@Override
	public void processLog(RequestLogs log) {
		List<AppLogLine> logLines = log.getAppLogLines();
		if ( logLines == null || logLines.size() <= 0 )
			return;
		List<AppLogLineEntity> appLogs = new ArrayList<AppLogLineEntity>();
		
		LogLevel lvl = LogLevel.DEBUG;
		for (AppLogLine logLine : logLines )
		{
			if ( LEVEL_PRIORITY.get(lvl) < LEVEL_PRIORITY.get(logLine.getLogLevel()) )
			{
				lvl = logLine.getLogLevel();
			}
			
			AppLogLineEntity ent = new AppLogLineEntity();
			ent.setLogLevel(LEVEL_PRIORITY_NAME.get(lvl));
			ent.setLogMessage(logLine.getLogMessage());
			ent.setTimeUsec(logLine.getTimeUsec());
			
			appLogs.add(ent);
		}
		
		level = LEVEL_PRIORITY_NAME.get(lvl);
		logs = new Gson().toJson(appLogs);
	}

	@Override
	public Object getField(String name) {
		if ( name.equals("level"))
		{
			return level;
		}
		else if ( name.equals("app_logs"))
		{
			return logs;
		}
		
		return null;
	}

	@Override
	public int getFieldCount() {
		return fieldNames.length;
	}

	@Override
	public String getFieldName(int i) {
		return fieldNames[i];
	}

	@Override
	public String getFieldType(int i) {
		return fieldTypes[i];
	}

	@Override
	public boolean isAppLogProcessed()
	{
		return false;
	}

	@Override
	public List<String> getAppLogLines()
	{
		return null;
	}

	@Override
	public boolean nextAppLogLine()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
