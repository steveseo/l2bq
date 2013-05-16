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

import java.util.List;

import com.google.appengine.api.log.RequestLogs;

/**
 * BigqueryFieldExporterSet holds a List of BigqueryFieldExporters.
 * 
 * Its primary purpose is to allow for specification of a set of BigqueryFieldExporterSet to
 * LogExportCronTask. Implementations must have a functional default
 * constructor.
 */
public interface BigqueryFieldExporterSetBag {
	
	
	/**
	 * Get the exporter Set in this BigqueryFieldExporterSetBag.
	 * 
	 * @return the exporter set list in the bag
	 */
	public List<BigqueryFieldExporterSet> getExporterSetList();

	/**
	 * Set the exporter set list in this BigqueryFieldExporterSetBag.
	 * @param setList the exporter set list to set 
	 */
	public void setExporterSetList(List<BigqueryFieldExporterSet> setList);
}
