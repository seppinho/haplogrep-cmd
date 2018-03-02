package genepi.haplogrep;

import core.SampleFile;

public class Session {
	
	String id;

	SampleFile currentSampleFile;

	public Session(String sessionId) {
		id = sessionId;
	}

	public SampleFile getCurrentSampleFile() {
		return currentSampleFile;
	}

	public void setCurrentSampleFile(SampleFile currentSampleFile) {
		this.currentSampleFile = currentSampleFile;
	}

	public String getID() {
		return id;
	}

}
