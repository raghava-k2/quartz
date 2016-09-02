package com.gli.job;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.gli.model.GLInfo;

public class DiaplyJob implements Job {
	@Override
	public synchronized void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail detail = context.getJobDetail();
		GLInfo glInfo = (GLInfo) detail.getJobDataMap().get(detail.getKey().getName());
		if ("longmap".equals(glInfo.getMapName())) {
			try {
				for (int i = 0; i < 100; i++) {
					System.out.println("processing the job : " + i);
					Thread.sleep(5000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(detail.getJobClass() + "|" + detail.getKey().getName() + "|" + detail.getKey().getGroup());
		System.out.println(glInfo.getGlFileName() + "|" + glInfo.getMapName() + "|" + glInfo.getOutputFileName());
	}
}
