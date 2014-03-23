package com.synthverse.synthscape.core;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.util.LogUtils;

public class Stats {

    private static Logger logger = Logger.getLogger(Stats.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    private EnumMap<Event, Integer> eventCounterMap = new EnumMap<Event, Integer>(Event.class);
    private Set<Event> events = new HashSet<Event>();

    public Stats() {
	eventCounterMap.clear();
    }

    public void recordValue(Event event) {
	if (eventCounterMap.containsKey(event)) {
	    eventCounterMap.put(event, eventCounterMap.get(event) + 1);
	} else {
	    eventCounterMap.put(event, 1);
	    events.add(event);
	}
    }

    public Set<Event> getEvents() {

	return events;
    }

    public int getValue(Event event) {
	int result = eventCounterMap.containsKey(event) ? eventCounterMap.get(event) : 0;
	return result;
    }

    public void clear() {
	eventCounterMap.clear();
	events.clear();
    }

    /**
     * This adds all values from this stat object to the one provided.
     * 
     * @param accumulatingStats
     */
    public void aggregateStatsTo(Stats accumulatingStats) {

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

}
