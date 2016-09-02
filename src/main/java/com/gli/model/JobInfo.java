package com.gli.model;

import java.util.List;

public class JobInfo {
	private String clientId;
	private String userName;
	private String jobName;
	private String jobGroupName;
	private String jobDescription;
	private String jobDateTime;
	private String jobEndtime;
	private List<String> jobExeDays;
	private List<String> jobExeWeeks;
	private List<String> jobExeMonths;
	private GLInfo glInfo;
	private String nextExecTime;
	private String status;

	public String getJobEndtime() {
		return jobEndtime;
	}

	public void setJobEndtime(String jobEndtime) {
		this.jobEndtime = jobEndtime;
	}

	public List<String> getJobExeDays() {
		return jobExeDays;
	}

	public void setJobExeDays(List<String> jobExeDays) {
		this.jobExeDays = jobExeDays;
	}

	public List<String> getJobExeWeeks() {
		return jobExeWeeks;
	}

	public void setJobExeWeeks(List<String> jobExeWeeks) {
		this.jobExeWeeks = jobExeWeeks;
	}

	public List<String> getJobExeMonths() {
		return jobExeMonths;
	}

	public void setJobExeMonths(List<String> jobExeMonths) {
		this.jobExeMonths = jobExeMonths;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getJobDateTime() {
		return jobDateTime;
	}

	public void setJobDateTime(String jobDateTime) {
		this.jobDateTime = jobDateTime;
	}

	public String getJobGroupName() {
		return jobGroupName;
	}

	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}

	public String getNextExecTime() {
		return nextExecTime;
	}

	public void setNextExecTime(String nextExecTime) {
		this.nextExecTime = nextExecTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public GLInfo getGlInfo() {
		return glInfo;
	}

	public void setGlInfo(GLInfo glInfo) {
		this.glInfo = glInfo;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
