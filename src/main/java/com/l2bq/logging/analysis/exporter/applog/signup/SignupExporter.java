package com.l2bq.logging.analysis.exporter.applog.signup;

import com.l2bq.logging.analysis.exporter.applog.AppLogExporter;


public class SignupExporter extends AppLogExporter{
	public SignupExporter() {
		typeName = "signup";
		
		fieldNames = new String[]{
				"time", "userId", "userType", "userName", "langType", "osType", "phone", "utcOffset"
			};
		fieldTypes = new String[]{
				"integer", "integer", "integer", "string", "integer", "integer", "string", "integer"	
			};
	}
}