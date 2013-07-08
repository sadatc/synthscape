package com.synthverse.synthscape.core;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class StatisticsManager {

	Set<String> universeStatistics = new LinkedHashSet<String>();
	Map<String, SummaryStatistics> universeSizeMap = new HashMap<String, SummaryStatistics>();
	Map<String, SummaryStatistics> universeSumMap = new HashMap<String, SummaryStatistics>();
	Map<String, SummaryStatistics> universeMinMap = new HashMap<String, SummaryStatistics>();
	Map<String, SummaryStatistics> universeMaxMap = new HashMap<String, SummaryStatistics>();
	Map<String, SummaryStatistics> universeMeanMap = new HashMap<String, SummaryStatistics>();
	Map<String, SummaryStatistics> universeSTDMap = new HashMap<String, SummaryStatistics>();

	Map<String, SummaryStatistics> sampleMap = new HashMap<String, SummaryStatistics>();

	private void _addSummaryValue(Map<String, SummaryStatistics> map,
			String statistics, double value) {
		SummaryStatistics container = null;
		if (map.containsKey(statistics)) {
			container = map.get(statistics);
		} else {
			container = new SummaryStatistics();
			map.put(statistics, container);
		}
		container.addValue(value);
	}

	public void _addUniverseSummaryValue(String statistics,
			SummaryStatistics summaryStatistics) {
		universeStatistics.add(statistics);
		_addSummaryValue(universeSizeMap, statistics + "_size_",
				summaryStatistics.getN());
		_addSummaryValue(universeSumMap, statistics + "_sum_",
				summaryStatistics.getSum());
		_addSummaryValue(universeMinMap, statistics + "_min_",
				summaryStatistics.getMin());
		_addSummaryValue(universeMaxMap, statistics + "_max_",
				summaryStatistics.getMax());
		_addSummaryValue(universeMeanMap, statistics + "_mean_",
				summaryStatistics.getMean());
		_addSummaryValue(universeSTDMap, statistics + "_std_",
				summaryStatistics.getStandardDeviation());
	}

	public void _addSampleSummaryValue(String statistics, double value) {
		_addSummaryValue(sampleMap, statistics, value);
	}

	private SummaryStatistics _getSummaryStatistics(
			Map<String, SummaryStatistics> map, String name) {
		return map.get(name);
	}

	public SummaryStatistics _getSampleSummaryStatistics(String name) {
		return _getSummaryStatistics(sampleMap, name);
	}

	private void _resetSummaryStatisticsCounters(
			Map<String, SummaryStatistics> map) {
		for (String statName : map.keySet()) {
			SummaryStatistics stat = map.get(statName);
			stat.clear();
		}
	}

	public void _resetSampleSummaryStatisticsCounters() {
		_resetSummaryStatisticsCounters(sampleMap);
	}

	public void _resetUniverseSummaryStatisticsCounters() {
		_resetSummaryStatisticsCounters(universeSizeMap);
		_resetSummaryStatisticsCounters(universeSumMap);
		_resetSummaryStatisticsCounters(universeMinMap);
		_resetSummaryStatisticsCounters(universeMaxMap);
		_resetSummaryStatisticsCounters(universeMeanMap);
		_resetSummaryStatisticsCounters(universeSTDMap);

	}

	private void _printSummaryStatistics(Map<String, SummaryStatistics> map) {
		for (String statName : map.keySet()) {
			D.p(statName + "|" + "Size:" + map.get(statName).getN() + "|Sum:"
					+ map.get(statName).getSum() + "|Min:"
					+ map.get(statName).getMin() + "|Max:"
					+ map.get(statName).getMax() + "|Mean:"
					+ map.get(statName).getMean() + "|STD:"
					+ map.get(statName).getStandardDeviation());
		}

	}

	public void _printSampleSummaryStatistics() {
		_printSummaryStatistics(sampleMap);
	}

	public void _printUniverseSummaryStatistics() {
		for (String statName : universeStatistics) {
			D.p(statName+":");
			D.p("Steps:["+universeSizeMap.get(statName+"_size_").getN()
					+"/"+universeSizeMap.get(statName+"_size_").getMin()
					+"/"+universeSizeMap.get(statName+"_size_").getMax()
					+"/"+universeSizeMap.get(statName+"_size_").getMean()
					+"/"+universeSizeMap.get(statName+"_size_").getStandardDeviation()+"]");

			
			D.p("Min:["+universeMinMap.get(statName+"_min_").getN()
					+"/"+universeMinMap.get(statName+"_min_").getMin()
					+"/"+universeMinMap.get(statName+"_min_").getMax()
					+"/"+universeMinMap.get(statName+"_min_").getMean()
					+"/"+universeMinMap.get(statName+"_min_").getStandardDeviation()+"]");

			D.p("Max:["+universeMaxMap.get(statName+"_max_").getN()
					+"/"+universeMaxMap.get(statName+"_max_").getMin()
					+"/"+universeMaxMap.get(statName+"_max_").getMax()
					+"/"+universeMaxMap.get(statName+"_max_").getMean()
					+"/"+universeMaxMap.get(statName+"_max_").getStandardDeviation()+"]");


			D.p("Mean:["+universeMeanMap.get(statName+"_mean_").getN()
					+"/"+universeMeanMap.get(statName+"_mean_").getMin()
					+"/"+universeMeanMap.get(statName+"_mean_").getMax()
					+"/"+universeMeanMap.get(statName+"_mean_").getMean()
					+"/"+universeMeanMap.get(statName+"_mean_").getStandardDeviation()+"]");

			
		
			
		}

	}

}
