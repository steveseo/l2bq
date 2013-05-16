package com.l2bq.logging.analysis.exporter.http;

public class AppLogLineEntity
{
	/*
	 * Log Level String 
	 */
	private String logLevel;
	
	/**
	 * Microsecond logging time 
	 */
	private long timeUsec;
	
	private String logMessage;
	
	public AppLogLineEntity()
	{
		// TODO Auto-generated constructor stub
	}

	public String getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(String logLevel)
	{
		this.logLevel = logLevel;
	}

	public long getTimeUsec()
	{
		return timeUsec;
	}

	public void setTimeUsec(long timeUsec)
	{
		this.timeUsec = timeUsec;
	}

	public String getLogMessage()
	{
		return logMessage;
	}

	public void setLogMessage(String logMessage)
	{
		this.logMessage = logMessage;
	}

}
