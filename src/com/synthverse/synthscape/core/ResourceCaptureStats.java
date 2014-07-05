package com.synthverse.synthscape.core;

import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.Main;
import com.synthverse.util.LogUtils;

public class ResourceCaptureStats implements Constants {

    private static Logger logger = Logger.getLogger(ResourceCaptureStats.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    public SummaryStatistics detectionToCaptureInterval = new SummaryStatistics();
    public SummaryStatistics extractionToCaptureInterval = new SummaryStatistics();
    public SummaryStatistics detections = new SummaryStatistics();
    public SummaryStatistics loads = new SummaryStatistics();
    public SummaryStatistics unloads = new SummaryStatistics();
    public SummaryStatistics touchedResources = new SummaryStatistics();

    public final void clearAll() {
	detectionToCaptureInterval.clear();
	extractionToCaptureInterval.clear();
	detections.clear();
	loads.clear();
	unloads.clear();
	touchedResources.clear();
    }

    final public void addData(ResourceStatus status) {
	if (status.captureStep != INVALID && status.detectionStep != INVALID
		&& status.detectionStep < status.captureStep) {
	    detectionToCaptureInterval.addValue(status.captureStep - status.detectionStep);
	}

	if (status.captureStep != INVALID && status.extractionStep != INVALID
		&& status.extractionStep < status.captureStep) {
	    extractionToCaptureInterval.addValue(status.captureStep - status.extractionStep);
	}

	if (status.numTimesDetected != INVALID) {
	    detections.addValue(status.numTimesDetected);
	}

	if (status.numTimesLoaded != INVALID) {
	    loads.addValue(status.numTimesLoaded);
	}

	if (status.numTimesUnloaded != INVALID) {
	    unloads.addValue(status.numTimesUnloaded);
	}

    }

    final public void printStats() {
	D.p("-----------------------------------------------");

	D.p("detectionToCaptureInterval=" + detectionToCaptureInterval);
	D.p("extractionToCaptureInterval=" + extractionToCaptureInterval);
	D.p("detections=" + detections);

	D.p("loads=" + loads);
	D.p("unloads=" + unloads);
	D.p("touchedResources=" + touchedResources);

	D.p("-----------------------------------------------");
	D.pause();

    }

}
