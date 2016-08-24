package com.kvr.service;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.kvr.dao.CreateJobInfoDAO;
import com.kvr.model.JSONData;
import com.kvr.model.JobInfo;
import com.kvr.util.StringUtil;

public class SchedulerService {
	public JSONData createNewJob(JobInfo info, HttpServletRequest request) {
		CreateJobInfoDAO createJobInfoDAO = new CreateJobInfoDAO();
		JSONData data = new JSONData();
		data.setMsg(createJobInfoDAO.createJobforUser(info, request));
		if (data.getMsg() != null)
			data.setStatus("failure");
		else
			data.setStatus("success");
		return data;
	}

	public List<JobInfo> getJobDetails(String jobName, HttpServletRequest request) {
		return new CreateJobInfoDAO().getJobDetails(jobName, request);
	}

	public JSONData deleteJobs(HttpServletRequest request, JsonArray jsonArray) {
		CreateJobInfoDAO createJobInfoDAO = new CreateJobInfoDAO();
		JSONData data = new JSONData();
		for (Iterator<JsonElement> iterator = jsonArray.iterator(); iterator.hasNext();) {
			data.setMsg(createJobInfoDAO.deleteJob(request, iterator.next().getAsJsonPrimitive().getAsString()));
			if (StringUtil.isStringNullOrEmpty(data.getMsg())) {
				data.setStatus("success");
				continue;
			} else {
				data.setStatus("failure");
				break;
			}
		}
		return data;
	}

	public JSONData updateJob(HttpServletRequest request, JobInfo info) {
		CreateJobInfoDAO createJobInfoDAO = new CreateJobInfoDAO();
		JSONData data = new JSONData();
		data.setMsg(createJobInfoDAO.updateJobDetails(request, info));
		if (data.getMsg() != null)
			data.setStatus("failure");
		else
			data.setStatus("success");
		return data;
	}
}
