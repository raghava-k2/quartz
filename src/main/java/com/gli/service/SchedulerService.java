package com.gli.service;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.gli.dao.CreateJobInfoDAO;
import com.gli.model.JSONData;
import com.gli.model.JobInfo;
import com.gli.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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
