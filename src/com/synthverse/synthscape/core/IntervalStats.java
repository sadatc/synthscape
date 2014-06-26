package com.synthverse.synthscape.core;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.Main;
import com.synthverse.util.LogUtils;

public class IntervalStats {

    private static Logger logger = Logger.getLogger(IntervalStats.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    private EnumMap<Event, SummaryStatistics> eventIntervalStatsMap = new EnumMap<Event, SummaryStatistics>(Event.class);
    private EnumMap<Event, Integer> eventLastStepMap = new EnumMap<Event, Integer>(Event.class);
    private EnumSet<Event> events =  EnumSet.noneOf(Event.class);

    public IntervalStats() {
    }

    public void recordValue(Event event, int step) {
	if (eventLastStepMap.containsKey(event)) {
	    int lastStep = eventLastStepMap.get(event);
	    SummaryStatistics stats = eventIntervalStatsMap.get(event);
	    int interval = lastStep-step;
	    stats.addValue(interval);
	} else {
	    SummaryStatistics stats = new SummaryStatistics();
	    stats.addValue(step);
	    eventIntervalStatsMap.put(event, stats);
	    events.add(event);
	}
	eventLastStepMap.put(event, step);
    }

    public void clear() {
	// here lets just clear the stats...so that they are not expensively re-created
	for(Event event: events) {
	    eventIntervalStatsMap.get(event).clear();
	}
	
	eventLastStepMap.clear(); // cheap to destroy...and re-create
	events.clear(); // we need to destroy
    }
    

    
    
   
    public Set<Event> getEvents() {
	return events;
    }

    public SummaryStatistics getIntervalStats(Event event) {
	SummaryStatistics result = null;
	
	if(events.contains(event)) {
	    result = eventIntervalStatsMap.get(event);
	}
	return result;
    }
    
    
    public void printValues() {
	if (events.size() > 0) {
	    logger.info("/////////////////////////////////");
	    for (Event event : events) {
		logger.info(event + " = " + eventIntervalStatsMap.get(event));
	    }
	    logger.info("/////////////////////////////////");
	}
    }

   
    /*

    public void aggregateStatsTo(IntervalStats accumulatingStats) {

	for (Event event : getEvents()) {
	    if (accumulatingStats.eventCounterMap.containsKey(event)) {
		accumulatingStats.eventCounterMap.put(event, accumulatingStats.eventCounterMap.get(event)
			+ eventCounterMap.get(event));
	    } else {
		accumulatingStats.eventCounterMap.put(event, eventCounterMap.get(event));
		accumulatingStats.events.add(event);
	    }
	}

    }

    public void printValues() {
	if (eventCounterMap.size() > 0) {
	    logger.info("/////////////////////////////////");
	    for (Event event : getEvents()) {
		logger.info(event + " = " + eventCounterMap.get(event));
	    }
	    logger.info("/////////////////////////////////");
	}
    }

    @Override
    public String toString() {
	String str = "";
	if (eventCounterMap.size() > 0) {

	    for (Event event : getEvents()) {
		str += (event + ":" + eventCounterMap.get(event)+" ");
	    }

	}

	return str;
    }
    
    */

}
