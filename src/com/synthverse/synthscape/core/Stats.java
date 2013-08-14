package com.synthverse.synthscape.core;

import java.util.EnumMap;

public class Stats {
    private EnumMap<Event, Integer> eventCounterMap = new EnumMap<Event, Integer>(Event.class);

    public Stats() {
	eventCounterMap.clear();
    }

    public void recordValue(Event event) {
	if (eventCounterMap.containsKey(event)) {
	    eventCounterMap.put(event, eventCounterMap.get(event) + 1);
	} else {
	    eventCounterMap.put(event, 1);
	}
    }

    public int getValue(Event event) {

	int result = eventCounterMap.containsKey(event) ? eventCounterMap.get(event) : 0;

	return result;
    }

    public void clear() {
	eventCounterMap.clear();
    }

    /**
     * This adds all values from this stat object to the one provided.
     * 
     * @param accumulatingStats
     */
    public void aggregateStatsTo(Stats accumulatingStats) {
	for (Event event : eventCounterMap.keySet()) {
	    if (accumulatingStats.eventCounterMap.containsKey(event)) {
		accumulatingStats.eventCounterMap.put(event,
			accumulatingStats.eventCounterMap.get(event) + eventCounterMap.get(event));
	    } else {
		accumulatingStats.eventCounterMap.put(event, eventCounterMap.get(event));
	    }
	}

    }

    public void printValues() {
	if (eventCounterMap.size() > 0) {
	    D.p("/////////////////////////////////");
	    for (Event event : eventCounterMap.keySet()) {
		D.p(event + " = " + eventCounterMap.get(event));
	    }
	    D.p("/////////////////////////////////");
	}
    }

}
