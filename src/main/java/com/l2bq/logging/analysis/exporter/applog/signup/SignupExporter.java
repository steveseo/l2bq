package com.l2bq.logging.analysis.exporter.applog.signup;

import com.l2bq.logging.analysis.exporter.applog.AppLogExporter;


public class SignupExporter extends AppLogExporter{
	public SignupExporter() {
		typeName = "signup";
		
		fieldNames = new String[]{
				"accountId", "accountIdType", "accountName", "langType", "osType", "phone", "playerId", "tiem", "utcOffset", "uuid"
			};
		fieldTypes = new String[]{
				"string", "integer", "string", "integer", "integer", "string", "integer", "integer", "integer", "string"	
			};
	}
}