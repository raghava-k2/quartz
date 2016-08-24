package com.kvr;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.kvr.model.JobInfo;
import com.kvr.service.SchedulerService;

public final class StartScheduler extends HttpServlet {
	private static final long serialVersionUID = -597564238282083211L;
	private static final Logger LOGGER = LoggerFactory.getLogger(StartScheduler.class);

	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String reqString = req.getRequestURI().split("/")[3];
		Gson gson = new Gson();
		PrintWriter writer = resp.getWriter();
		try {
			switch (reqString) {
			case "createjob":
				writer.write(gson.toJson(new SchedulerService().createNewJob((JobInfo) gson.fromJson(req.getReader(), JobInfo.class), req)));
				break;
			case "getjobdetails":
				writer.write(gson.toJson(new SchedulerService().getJobDetails(req.getParameter("jobName"), req)));
				break;
			case "deletejobs":
				writer.write(gson.toJson(new SchedulerService().deleteJobs(req, new JsonParser().parse(req.getReader()).getAsJsonArray())));
				break;
			case "updatejob":
				writer.write(gson.toJson(new SchedulerService().updateJob(req, (JobInfo) gson.fromJson(req.getReader(), JobInfo.class))));
				break;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		writer.flush();
	}
}
