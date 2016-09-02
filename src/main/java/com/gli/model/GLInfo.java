package com.gli.model;

import java.io.Serializable;

public class GLInfo implements Serializable {
	private static final long serialVersionUID = -7329007911636837618L;
	private String glFileName;
	private String outputFileName;
	private String mapName;

	public String getGlFileName() {
		return glFileName;
	}

	public void setGlFileName(String glFileName) {
		this.glFileName = glFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

}
