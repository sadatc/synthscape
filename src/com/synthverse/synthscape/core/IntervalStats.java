package com.synthverse.synthscape.core;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.Main;
import com.synthverse.util.LogUtils;

public class IntervalStats implements Constants {

	private static Logger logger = Logger.getLogger(IntervalStats.class
			.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	class SpeciesStats {
		EnumMap<Species, SummaryStatistics> statMap = new EnumMap<Species, SummaryStatistics>(
				Species.class);

		final void addValue(Species species, int value) {
			SummaryStatistics stats;
			if (statMap.containsKey(species)) {
				stats = statMap.get(species);
			} else {
				stats = new SummaryStatistics();
				statMap.put(species, stats);
			}

			stats.addValue(value);
		}

		final SummaryStatistics getValue(Species species) {
			SummaryStatistics result = null;
			if (statMap.containsKey(species)) {
				result = statMap.get(species);
			}

			return result;
		}

		final void clearAllStats() {
			for (SummaryStatistics stats : statMap.values()) {
				stats.clear();
			}
		}

	}

	// private EnumMap<Event, SpeciesStats> speciesEventIntervalStatsMap = new
	// EnumMap<Event, SpeciesStats>(Event.class);
	private EnumMap<EventType, SpeciesStats> speciesEventTypeIntervalStatsMap = new EnumMap<EventType, SpeciesStats>(
			EventType.class);

	private EnumMap<Event, SummaryStatistics> eventIntervalStatsMap = new EnumMap<Event, SummaryStatistics>(
			Event.class);
	private EnumMap<EventType, SummaryStatistics> eventTypeIntervalStatsMap = new EnumMap<EventType, SummaryStatistics>(
			EventType.class);

	private EnumMap<Event, Integer> eventLastStepMap = new EnumMap<Event, Integer>(
			Event.class);
	private EnumMap<EventType, Integer> eventTypeLastStepMap = new EnumMap<EventType, Integer>(
			EventType.class);

	private EnumSet<Event> events = EnumSet.noneOf(Event.class);
	private EnumSet<EventType> eventTypes = EnumSet.noneOf(EventType.class);

	public IntervalStats() {
	}

	public void recordValue(Event event, Species species, int step) {
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

			SummaryStatistics stats;

			if (eventIntervalStatsMap.containsKey(event)) {
				stats = eventIntervalStatsMap.get(event);
				stats.addValue(step);

			} else {
				stats = new SummaryStatistics();
				stats.addValue(step);
				eventIntervalStatsMap.put(event, stats);

			}

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
				addSpeciesEventTypeIntrval(species, eventType, interval);
			}
		} else {
			SummaryStatistics stats;

			if (eventTypeIntervalStatsMap.containsKey(eventType)) {
				stats = eventTypeIntervalStatsMap.get(eventType);
				stats.addValue(step);
				addSpeciesEventTypeIntrval(species, eventType, step);
			} else {
				stats = new SummaryStatistics();
				stats.addValue(step);
				eventTypeIntervalStatsMap.put(eventType, stats);
				addSpeciesEventTypeIntrval(species, eventType, step);
			}

			eventTypes.add(eventType);
		}
		eventTypeLastStepMap.put(eventType, step);

	}

	public SummaryStatistics getSpeciesEventTypeIntervalStats(Species species,
			EventType eventType) {
		SummaryStatistics result = null;

		if (speciesEventTypeIntervalStatsMap.containsKey(eventType)) {
			SpeciesStats speciesStats = speciesEventTypeIntervalStatsMap
					.get(eventType);
			if (speciesStats != null) {
				result = speciesStats.getValue(species);
			}

		}

		return result;
	}

	private void addSpeciesEventTypeIntrval(Species species,
			EventType eventType, int interval) {

		SpeciesStats stats;
		if (speciesEventTypeIntervalStatsMap.containsKey(eventType)) {
			stats = speciesEventTypeIntervalStatsMap.get(eventType);
		} else {
			stats = new SpeciesStats();
			speciesEventTypeIntervalStatsMap.put(eventType, stats);
		}
		stats.addValue(species, interval);

	}

	/*
	 * private void printSpeciesEventTypeInterval() { for (EventType type :
	 * eventTypes) { SpeciesStats speciesStats =
	 * speciesEventTypeIntervalStatsMap.get(type); for (Species species :
	 * speciesStats.statMap.keySet()) { D.p(species + " " + type + " " +
	 * speciesStats.statMap.get(species)); } }
	 * 
	 * }
	 */

	public void resetLastSteps() {
		// this simply clears the last steps...
		// should be called at the end of each simulation
		eventLastStepMap.clear();
		eventTypeLastStepMap.clear();
	}

	public void clear() {
		// this should be called at the end of each generation
		// here lets just clear the stats...so that they are not expensively
		// re-created

		// printSpeciesEventTypeInterval();

		for (Event event : events) {
			eventIntervalStatsMap.get(event).clear();
		}

		eventLastStepMap.clear(); // cheap to destroy...and re-create
		events.clear(); // we need to destroy

		// now do the same for event types

		// re-created
		for (EventType eventType : eventTypes) {
			eventTypeIntervalStatsMap.get(eventType).clear();

			speciesEventTypeIntervalStatsMap.get(eventType).clearAllStats();

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

				SummaryStatistics stats = eventTypeIntervalStatsMap.get(type);
				String msg = EMPTY_STRING;

				msg += " n=" + stats.getN();
				msg += "\tmin=" + stats.getMin();
				msg += "\tmax=" + stats.getMax();
				msg += "\tavg=" + stats.getMean();

				logger.info(type + ":" + msg);
			}
			logger.info("/////////////////////////////////");
			D.pause();
		}

	}

}
