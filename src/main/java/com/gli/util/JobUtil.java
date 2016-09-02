package com.gli.util;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.gli.job.DiaplyJob;
import com.gli.model.GLInfo;
import com.gli.model.JobInfo;

public class JobUtil {
	private static final String DATE_FORMAT2 = "EEE MMM dd yyyy HH:mm:ss";

	public static JobDetail createJob(JobInfo info, HttpServletRequest request) throws Exception {
		Trigger trigger = null;
		JobDetail detail = JobUtil.createJobDetail(info);
		if (info.getJobExeDays().size() == 7 && info.getJobExeMonths().size() == 12)
			trigger = createCalenderTrigger(info, detail);
		else
			trigger = createCronTrigger(info, detail);
		SchedulerUtil.getSchedulerInstance(request).scheduleJob(detail, trigger);
		return detail;
	}

	public static Boolean deleteJob(HttpServletRequest request, List<String> jobDetails) throws SchedulerException {
		Scheduler scheduler = SchedulerUtil.getSchedulerInstance(request);
		if (scheduler.checkExists(TriggerKey.triggerKey(jobDetails.get(0), jobDetails.get(1)))
				&& scheduler.checkExists(JobKey.jobKey(jobDetails.get(0), jobDetails.get(1)))) {
			scheduler.unscheduleJob(TriggerKey.triggerKey(jobDetails.get(0), jobDetails.get(1)));
			scheduler.deleteJob(JobKey.jobKey(jobDetails.get(0), jobDetails.get(1)));
		}
		return true;
	}

	public static JobDetail updateJob(HttpServletRequest request, JobInfo info) throws SchedulerException, ParseException {
		Scheduler scheduler = SchedulerUtil.getSchedulerInstance(request);
		Trigger trigger = null;
		JobDetail detail = JobUtil.updateExistingJobDetails(JobUtil.getExistingJobDetails(request, info), info);
		if (info.getJobExeDays().size() == 7 && info.getJobExeMonths().size() == 12)
			trigger = createCalenderTrigger(info, detail);
		else
			trigger = createCronTrigger(info, detail);
		scheduler.addJob(detail, true, true);
		scheduler.rescheduleJob(TriggerKey.triggerKey(info.getJobName(), info.getJobGroupName()), trigger);
		return detail;
	}

	private static JobDetail getExistingJobDetails(HttpServletRequest request, JobInfo info) throws SchedulerException {
		Scheduler scheduler = SchedulerUtil.getSchedulerInstance(request);
		return scheduler.getJobDetail(JobKey.jobKey(info.getJobName(), info.getJobGroupName()));
	}

	private static JobDetail updateExistingJobDetails(JobDetail detail, JobInfo info) {
		JobDataMap dataMap = detail.getJobDataMap();
		GLInfo glInfo = (GLInfo) dataMap.get(detail.getKey().getName());
		glInfo.setGlFileName(info.getGlInfo().getGlFileName());
		glInfo.setOutputFileName(info.getGlInfo().getOutputFileName());
		glInfo.setMapName(info.getGlInfo().getMapName());
		detail.getJobBuilder().withDescription(info.getJobDescription());
		return detail;
	}

	private static JobDetail createJobDetail(JobInfo info) {
		JobDataMap dataMap = new JobDataMap();
		dataMap.put(info.getJobName(), info.getGlInfo());
		return JobBuilder.newJob(DiaplyJob.class).withDescription(info.getJobDescription()).withIdentity(info.getJobName(), info.getJobGroupName())
				.setJobData(dataMap).storeDurably(false).build();
	}

	private static Trigger createCalenderTrigger(JobInfo info, JobDetail detail) throws ParseException {
		TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger().forJob(detail);
		if (StringUtil.isStringNullOrEmpty(info.getJobDateTime()))
			builder.startNow();
		else
			builder.startAt(DateUtil.getDate(info.getJobDateTime(), DATE_FORMAT2));
		builder.withDescription(info.getUserName() + info.getJobGroupName()).withIdentity(info.getJobName(), info.getJobGroupName())
				.withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule());
		if (!StringUtil.isStringNullOrEmpty(info.getJobEndtime()))
			builder.endAt(DateUtil.getDate(info.getJobEndtime(), DATE_FORMAT2));
		return builder.build();
	}

	private static Trigger createCronTrigger(JobInfo info, JobDetail detail) throws ParseException {
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		triggerBuilder.forJob(detail);
		if (!StringUtil.isStringNullOrEmpty(info.getJobDateTime())) {
			triggerBuilder.startAt(DateUtil.getDate(info.getJobDateTime(), DATE_FORMAT2));
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(buildCronExpression(DateUtil.getDate(info.getJobDateTime(), DATE_FORMAT2),
					info.getJobExeDays(), info.getJobExeMonths())));
		} else {
			triggerBuilder.startNow();
		}
		if (!StringUtil.isStringNullOrEmpty(info.getJobEndtime()))
			triggerBuilder.endAt(DateUtil.getDate(info.getJobEndtime(), DATE_FORMAT2));
		triggerBuilder.withDescription(info.getUserName() + info.getJobGroupName());
		triggerBuilder.withIdentity(info.getJobName(), info.getJobGroupName());
		return triggerBuilder.build();
	}

	private static String buildCronExpression(java.util.Date date, List<String> weekdays, List<String> months) {
		@SuppressWarnings("deprecation")
		String cronExp = date.getSeconds() + " " + date.getMinutes() + " " + date.getHours() + " " + "?" + " ";
		if (months.size() == 12 || months.size() == 0)
			cronExp += "*" + " ";
		else {
			for (String month : months)
				cronExp += month + ",";
			cronExp = cronExp.substring(0, cronExp.length() - 1) + " ";
		}
		if (weekdays.size() == 7 || weekdays.size() == 0)
			cronExp += "*";
		else {
			for (String weeks : weekdays)
				cronExp += weeks + ",";
			cronExp = cronExp.substring(0, cronExp.length() - 1);
		}
		System.out.println("generated cronExp : " + cronExp);
		return cronExp;
	}
}
