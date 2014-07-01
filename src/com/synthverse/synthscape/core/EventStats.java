package com.synthverse.synthscape.core;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.util.LogUtils;

public class EventStats {

    private static Logger logger = Logger.getLogger(EventStats.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    private EnumMap<Event, Integer> eventCounterMap = new EnumMap<Event, Integer>(Event.class);
    private EnumMap<EventType, Integer> eventTypeCounterMap = new EnumMap<EventType, Integer>(EventType.class);

    private Set<Event> events = EnumSet.noneOf(Event.class);
    private Set<EventType> eventTypes = EnumSet.noneOf(EventType.class);

    public EventStats() {
	eventCounterMap.clear();
	eventTypeCounterMap.clear();
    }

    public void recordValue(Event event) {
	if (eventCounterMap.containsKey(event)) {
	    eventCounterMap.put(event, eventCounterMap.get(event) + 1);
	} else {
	    eventCounterMap.put(event, 1);
	    events.add(event);
	}

	EventType type = event.getType();
	if (eventTypeCounterMap.containsKey(type)) {
	    eventTypeCounterMap.put(type, eventTypeCounterMap.get(type) + 1);
	} else {
	    eventTypeCounterMap.put(type, 1);
	    eventTypes.add(type);
	}
    }

    public Set<Event> getEvents() {
	return events;
    }

    public Set<EventType> getEventTypes() {
	return eventTypes;
    }

    public int getValue(Event event) {
	int result = eventCounterMap.containsKey(event) ? eventCounterMap.get(event) : 0;
	return result;
    }

    public int getTypeValue(EventType type) {
	int result = eventTypeCounterMap.containsKey(type) ? eventTypeCounterMap.get(type) : 0;
	return result;
    }

    public void clear() {
	eventCounterMap.clear();
	events.clear();
	eventTypeCounterMap.clear();
	eventTypes.clear();
    }

    /**
     * This adds all values from this stat object to the one provided.
     * 
     * @param accumulatingStats
     */
    public void aggregateStatsTo(EventStats accumulatingStats) {

	for (Event event : events) {
	    if (accumulatingStats.eventCounterMap.containsKey(event)) {
		accumulatingStats.eventCounterMap.put(event, accumulatingStats.eventCounterMap.get(event)
			+ eventCounterMap.get(event));
	    } else {
		accumulatingStats.eventCounterMap.put(event, eventCounterMap.get(event));
		accumulatingStats.events.add(event);
	    }
	}

	for (EventType type : eventTypes) {
	    if (accumulatingStats.eventTypeCounterMap.containsKey(type)) {
		accumulatingStats.eventTypeCounterMap.put(type, accumulatingStats.eventTypeCounterMap.get(type)
			+ eventTypeCounterMap.get(type));
	    } else {
		accumulatingStats.eventTypeCounterMap.put(type, eventTypeCounterMap.get(type));
		accumulatingStats.eventTypes.add(type);
	    }
	}

    }

    public void printEventStats() {
	for (Event event : events) {
	    D.p(event.name() + " = " + eventCounterMap.get(event));
	}
    }

    public void printEventTypeStats() {
	for (EventType type : eventTypes) {
	    D.p(type.name() + " = " + eventTypeCounterMap.get(type));
	}
    }

}
