package com.l2bq.logging.analysis.exporter;

import java.util.Arrays;
import java.util.List;

import com.l2bq.logging.analysis.BigqueryFieldExporterSet;
import com.l2bq.logging.analysis.BigqueryFieldExporterSetBag;
import com.l2bq.logging.analysis.exporter.applog.login.LoginExporterSet;
import com.l2bq.logging.analysis.exporter.applog.signup.SignupExporterSet;
import com.l2bq.logging.analysis.exporter.http.HttpFieldExporterSet;

public class ExporterSetBag implements BigqueryFieldExporterSetBag
{
	private List<BigqueryFieldExporterSet> fieldExporterSet;

	public ExporterSetBag()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<BigqueryFieldExporterSet> getExporterSetList()
	{
		fieldExporterSet = Arrays.asList( 
									new HttpFieldExporterSet(),
									new LoginExporterSet(),
									new SignupExporterSet()); 
		
		return fieldExporterSet;
	}

	@Override
	public void setExporterSetList(List<BigqueryFieldExporterSet> setList)
	{
		this.fieldExporterSet = setList;
	}

}
