package com.kvr.job;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.kvr.model.GLInfo;

public class DiaplyJob implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail detail = context.getJobDetail();
		GLInfo glInfo = (GLInfo) detail.getJobDataMap().get(detail.getKey().getName());
		if ("longmap".equals(glInfo.getMapName())) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(detail.getJobClass() + "|" + detail.getKey().getName() + "|" + detail.getKey().getGroup());
		System.out.println(glInfo.getGlFileName() + "|" + glInfo.getMapName() + "|" + glInfo.getOutputFileName());
	}
}
