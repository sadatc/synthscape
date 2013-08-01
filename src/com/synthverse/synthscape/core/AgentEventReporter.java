package com.synthverse.synthscape.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AgentEventReporter implements Constants {

	private boolean flushConstantly = false;
	private String reportFileName = null;
	private BufferedWriter bufferedWriter = null;
	private final static char COMMA = ',';
	private static AgentEventReporter instance = null;

	@SuppressWarnings("unused")
	private AgentEventReporter() {
		throw new AssertionError("not allowed");
	}

	private AgentEventReporter(boolean flushConstantly, String reportFile)
			throws IOException {
		this.flushConstantly = flushConstantly;
		this.reportFileName = reportFile;
		openFile();
	}

	public static AgentEventReporter getInstance(boolean flushConstantly,
			String reportFile) throws IOException {
		if (instance == null) {
			instance = new AgentEventReporter(flushConstantly, reportFile);
		}
		return instance;
	}

	private void openFile() throws IOException {
		File file = new File(reportFileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		bufferedWriter = new BufferedWriter(new FileWriter(
				file.getAbsoluteFile(), true), REPORT_WRITER_BUFFER_SIZE);

	}

	public void reportEvent(String experiment, int simulationNumber,
			int generation, Species species, int agentId, int step, Event event)
			throws IOException {
		bufferedWriter.write(experiment);
		bufferedWriter.write(COMMA);
		bufferedWriter.write(simulationNumber);
		bufferedWriter.write(COMMA);
		bufferedWriter.write(generation);
		bufferedWriter.write(COMMA);
		bufferedWriter.write(species.getAbbreviation());
		bufferedWriter.write(COMMA);
		bufferedWriter.write(step);
		bufferedWriter.write(event.toString());
		bufferedWriter.newLine();

		if (this.flushConstantly) {
			bufferedWriter.flush();
		}

	}

	public void closeFile() throws IOException {
		bufferedWriter.close();
	}

}
