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

    private EnumMap<Event, SummaryStatistics> eventIntervalStatsMap = new EnumMap<Event, SummaryStatistics>(
	    Event.class);
    private EnumMap<EventType, SummaryStatistics> eventTypeIntervalStatsMap = new EnumMap<EventType, SummaryStatistics>(
	    EventType.class);

    private EnumMap<Event, Integer> eventLastStepMap = new EnumMap<Event, Integer>(Event.class);
    private EnumMap<EventType, Integer> eventTypeLastStepMap = new EnumMap<EventType, Integer>(
	    EventType.class);

    private EnumSet<Event> events = EnumSet.noneOf(Event.class);
    private EnumSet<EventType> eventTypes = EnumSet.noneOf(EventType.class);

    public IntervalStats() {
    }

    public void recordValue(Event event, int step) {
	if (eventLastStepMap.containsKey(event)) {
	    int lastStep = eventLastStepMap.get(event);
	    SummaryStatistics stats = eventIntervalStatsMap.get(event);

	    int interval = step - lastStep;
	    if (interval > 0) {
		// not allowing 0 intervals will stop reporting the same
		// event on the same step

		stats.addValue(interval);
	    }
	} else {
	    SummaryStatistics stats = new SummaryStatistics();
	    stats.addValue(step);
	    eventIntervalStatsMap.put(event, stats);
	    events.add(event);
	}
	eventLastStepMap.put(event, step);

	// now do the same thing, but for even types...

	EventType eventType = event.getType();
	if (eventTypeLastStepMap.containsKey(eventType)) {
	    int lastStep = eventTypeLastStepMap.get(eventType);
	    SummaryStatistics stats = eventTypeIntervalStatsMap.get(eventType);
	    int interval = step - lastStep;

	    if (interval > 0) {
		// not allowing 0 intervals will stop reporting the same
		// event on the same step
		stats.addValue(interval);
	    }
	} else {
	    SummaryStatistics stats = new SummaryStatistics();
	    stats.addValue(step);
	    eventTypeIntervalStatsMap.put(eventType, stats);
	    eventTypes.add(eventType);
	}
	eventTypeLastStepMap.put(eventType, step);

    }

    public void clear() {
	// here lets just clear the stats...so that they are not expensively
	// re-created
	for (Event event : events) {
	    eventIntervalStatsMap.get(event).clear();
	}

	eventLastStepMap.clear(); // cheap to destroy...and re-create
	events.clear(); // we need to destroy

	// now do the same for event types

	// re-created
	for (EventType eventType : eventTypes) {
	    eventTypeIntervalStatsMap.get(eventType).clear();
	}

	eventTypeLastStepMap.clear(); // cheap to destroy...and re-create
	eventTypes.clear(); // we need to destroy

    }

    public Set<Event> getEvents() {
	return events;
    }

    public Set<EventType> getEventTypes() {
	return eventTypes;
    }

    public SummaryStatistics getIntervalStats(Event event) {
	SummaryStatistics result = null;

	if (events.contains(event)) {
	    result = eventIntervalStatsMap.get(event);
	}
	return result;
    }

    public SummaryStatistics getTypeIntervalStats(EventType eventType) {
	SummaryStatistics result = null;

	if (eventTypes.contains(eventType)) {
	    result = eventTypeIntervalStatsMap.get(eventType);
	}
	return result;
    }

    public void printEventTypeStats() {
	if (events.size() > 0) {
	    logger.info("/////////////////////////////////");
	    for (EventType type : eventTypes) {
		logger.info(type + " = " + eventTypeIntervalStatsMap.get(type));
	    }
	    logger.info("/////////////////////////////////");
	    D.pause();
	}

    }

    /*
     * 
     * public void aggregateStatsTo(IntervalStats accumulatingStats) {
     * 
     * for (Event event : getEvents()) { if
     * (accumulatingStats.eventCounterMap.containsKey(event)) {
     * accumulatingStats.eventCounterMap.put(event,
     * accumulatingStats.eventCounterMap.get(event) +
     * eventCounterMap.get(event)); } else {
     * accumulatingStats.eventCounterMap.put(event, eventCounterMap.get(event));
     * accumulatingStats.events.add(event); } }
     * 
     * }
     * 
     * public void printValues() { if (eventCounterMap.size() > 0) {
     * logger.info("/////////////////////////////////"); for (Event event :
     * getEvents()) { logger.info(event + " = " + eventCounterMap.get(event)); }
     * logger.info("/////////////////////////////////"); } }
     * 
     * @Override public String toString() { String str = ""; if
     * (eventCounterMap.size() > 0) {
     * 
     * for (Event event : getEvents()) { str += (event + ":" +
     * eventCounterMap.get(event)+" "); }
     * 
     * }
     * 
     * return str; }
     */

}
