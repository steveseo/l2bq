/*
‰ * Copyright 2012 Rewardly Inc.
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

package com.l2bq.logging.analysis.exporter.applog.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.RequestLogs;
import com.google.appengine.api.log.LogService.LogLevel;
import com.l2bq.logging.analysis.BigqueryFieldExporter;
import com.l2bq.logging.analysis.exporter.applog.AppLogExporter;

/**
 * Application 에서 생성되는 Log 내용을 Parsing 하여 결과를 Return
 * @author Junki Kim(jkkim@playearth.co.kr)
 * @date 2013. 4. 29.
 */
public class LoginExporter extends AppLogExporter {
	public LoginExporter() {
		typeName = "login";
		
		fieldNames = new String[]{
				"time", "userId", "userName", "langType", "clientVer", "osType"
			};
		fieldTypes = new String[]{
				"integer", "integer", "string", "integer", "string", "integer"	
			};
	}
}
