package com.l2bq.logging.analysis.exporter.applog.signup;

import java.util.Arrays;
import java.util.List;

import com.google.appengine.api.log.RequestLogs;
import com.l2bq.logging.analysis.BigqueryFieldExporter;
import com.l2bq.logging.analysis.BigqueryFieldExporterSet;

public class SignupExporterSet implements BigqueryFieldExporterSet {

	@Override
	public List<BigqueryFieldExporter> getExporters() {
		return Arrays.asList(
				(BigqueryFieldExporter)new SignupExporter());
	}
	
	@Override
	public boolean skipLog(RequestLogs log)
	{
		return false;
	}

	@Override
	public List<String> applicationVersionsToExport() {
		return null;
	}
	
	@Override
	public String getPrefix()
	{
		return "applog_signup";
	}
	
}