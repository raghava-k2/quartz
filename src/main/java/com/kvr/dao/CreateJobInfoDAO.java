package com.kvr.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CalendarIntervalTriggerImpl;

import com.kvr.model.GLInfo;
import com.kvr.model.JobInfo;
import com.kvr.util.DBUtil;
import com.kvr.util.DateUtil;
import com.kvr.util.JobUtil;
import com.kvr.util.SchedulerUtil;
import com.kvr.util.StringUtil;

public class CreateJobInfoDAO extends GlobalDAO {
	private final String INSERT_JOB_USER_JOBS = "insert into user_jobs values(?,?,?,?)";
	private final String INSERT_USER_JOBS_DETAILS = "insert into user_jobs_details values(?,?,?,?,?,?,?)";
	private String SELECT_USER_JOBS = "select User_Jobs.id as clientId,user_name as userName,job_name as JobName,job_grp_name as jobGrpName,job_desc as jobDescription from "
			+ " User_Jobs INNER join User_Jobs_Details on (User_Jobs.id=User_Jobs_Details.id) ";
	private final String DELETE_USER_JOB_DETAILS_INFO = "delete from User_Jobs_Details where id=?";
	private final String DELETE_USER_JOB__INFO = "delete from User_Jobs where id=?";
	private final String UPDATE_USER_JOB_INFO = "update User_Jobs set modified_date=? where id=?";
	private final String UPDATE_USER_JOB_DETAILS_INFO = "update User_Jobs_Details set job_desc=?,job_date_time=?,modified_date=? where id=?";
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm a";
	private static final String DATE_FORMAT2 = "EEE MMM dd yyyy HH:mm:ss";

	public String createJobforUser(JobInfo info, HttpServletRequest request) {
		try {
			this.connection = DBUtil.getConnectionInstance();
			this.insertIntoUserJobsDetails(info, this.insertIntoUserJobs(info));
			this.addJobToscheduler(info, request);
		} catch (ObjectAlreadyExistsException e) {
			DBUtil.rollback();
			e.printStackTrace();
			return "There exists job with same group.Please enter different job details.";
		} catch (Exception e) {
			System.out.println(e);
			DBUtil.rollback();
			return "some error occured.Contact system admin.";
		} finally {
			DBUtil.closeConnection();
		}
		return null;
	}

	public List<JobInfo> getJobDetails(String jobName, HttpServletRequest request) {
		try {
			this.connection = DBUtil.getConnectionInstance();
			return getAllJobDetails(jobName, request);
		} catch (SQLException | SchedulerException e) {
			e.printStackTrace();
			return new ArrayList<JobInfo>();
		} finally {
			DBUtil.closeConnection();
		}
	}

	public String deleteJob(HttpServletRequest request, String jobId) {
		try {
			this.connection = DBUtil.getConnectionInstance();
			List<String> list = this.getJobDetails(jobId);
			this.deleteDetailJob(jobId);
			this.deleteHeaderJob(jobId);
			this.deleteQrtzJob(request, list);
		} catch (SQLException | SchedulerException e) {
			System.out.println(e);
			DBUtil.rollback();
			return "failed removing the jobId : " + jobId;
		} catch (Exception e) {
			System.out.println(e);
			DBUtil.rollback();
			return "some error occured.Contact system admin.";
		} finally {
			DBUtil.closeConnection();
		}
		return null;
	}

	public String updateJobDetails(HttpServletRequest request, JobInfo info) {
		try {
			this.connection = DBUtil.getConnectionInstance();
			this.updateUserJobDetails(info);
			this.updateHeaderUserJob(info);
			this.updateQrtzJob(request, info);
		} catch (SQLException | ParseException e) {
			System.out.println(e);
			DBUtil.rollback();
			return "Unable to update the Job";
		} catch (Exception e) {
			DBUtil.rollback();
			System.out.println(e);
			return "some error occured.Contact system admin.";
		} finally {
			DBUtil.closeConnection();
		}
		return null;
	}

	private Integer insertIntoUserJobs(JobInfo info) throws SQLException {
		int key = (int) (Math.random() * 123456789);
		PreparedStatement preparedStatement = this.connection.prepareStatement(INSERT_JOB_USER_JOBS);
		preparedStatement.setInt(1, key);
		preparedStatement.setString(2, info.getUserName());
		preparedStatement.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
		preparedStatement.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
		preparedStatement.execute();
		return key;
	}

	private Boolean insertIntoUserJobsDetails(JobInfo info, int parentKey) throws SQLException, ParseException {
		PreparedStatement preparedStatement = this.connection.prepareStatement(INSERT_USER_JOBS_DETAILS);
		preparedStatement.setInt(1, parentKey);
		preparedStatement.setString(2, info.getJobName());
		preparedStatement.setString(3, info.getJobDescription());
		if (StringUtil.isStringNullOrEmpty(info.getJobDateTime()))
			preparedStatement.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
		else
			preparedStatement.setTimestamp(4, new Timestamp(DateUtil.getDate(info.getJobDateTime(), DATE_FORMAT2).getTime()));
		preparedStatement.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
		preparedStatement.setTimestamp(6, new Timestamp(new java.util.Date().getTime()));
		preparedStatement.setString(7, info.getJobGroupName());
		return preparedStatement.execute();
	}

	private JobDetail addJobToscheduler(JobInfo info, HttpServletRequest request) throws Exception {
		return JobUtil.createJob(info, request);
	}

	private List<JobInfo> getAllJobDetails(String jobName, HttpServletRequest request) throws SQLException, SchedulerException {
		List<JobInfo> jobInfosList = new ArrayList<JobInfo>();
		JobInfo jobInfo = null;
		if (jobName != null && jobName.length() != 0)
			SELECT_USER_JOBS += " where lower(user_name) = lower(?)";
		PreparedStatement preparedStatement = this.connection.prepareStatement(SELECT_USER_JOBS);
		if (jobName != null && jobName.length() != 0)
			preparedStatement.setString(1, jobName);
		ResultSet resultSet = preparedStatement.executeQuery();
		for (; resultSet.next();) {
			jobInfo = new JobInfo();
			jobInfo.setClientId(resultSet.getString(1));
			jobInfo.setUserName(resultSet.getString(2));
			jobInfo.setJobName(resultSet.getString(3));
			jobInfo.setJobGroupName(resultSet.getString(4));
			jobInfo.setJobDescription(resultSet.getString(5));
			this.getQrtxTriggerDetails(jobInfo, request);
			this.getQrtxJobDetails(jobInfo, request);
			this.getCronDetails(jobInfo, request);
			jobInfosList.add(jobInfo);
		}
		return jobInfosList;
	}

	private JobInfo getQrtxTriggerDetails(JobInfo info, HttpServletRequest request) throws SQLException, SchedulerException {
		Scheduler scheduler = SchedulerUtil.getSchedulerInstance(request);
		Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(info.getJobName(), info.getJobGroupName()));
		if (trigger != null) {
			info.setJobDateTime(DateUtil.getStringDate(trigger.getStartTime(), DATE_FORMAT));
			info.setJobEndtime(DateUtil.getStringDate(trigger.getEndTime(), DATE_FORMAT));
			info.setNextExecTime(DateUtil.getStringDate(trigger.getNextFireTime(), DATE_FORMAT));
		}
		info.setStatus(scheduler.getTriggerState(TriggerKey.triggerKey(info.getJobName(), info.getJobGroupName())).toString());
		return info;
	}

	private JobInfo getQrtxJobDetails(JobInfo info, HttpServletRequest request) throws SchedulerException {
		Scheduler scheduler = SchedulerUtil.getSchedulerInstance(request);
		JobDetail detail = scheduler.getJobDetail(new JobKey(info.getJobName(), info.getJobGroupName()));
		if (detail != null)
			info.setGlInfo((GLInfo) detail.getJobDataMap().get(info.getJobName()));
		return info;
	}

	private JobInfo getCronDetails(JobInfo info, HttpServletRequest request) throws SchedulerException {
		Scheduler scheduler = SchedulerUtil.getSchedulerInstance(request);
		if (scheduler.getTrigger(TriggerKey.triggerKey(info.getJobName(), info.getJobGroupName())) != null) {
			if (!(scheduler.getTrigger(TriggerKey.triggerKey(info.getJobName(), info.getJobGroupName())) instanceof CalendarIntervalTriggerImpl)) {
				CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(info.getJobName(), info.getJobGroupName()));
				String[] cron = cronTrigger.getCronExpression().split(" ");
				info.setJobExeMonths(this.convertStringToList(cron[4], ","));
				info.setJobExeDays(this.convertStringToList(cron[5], ","));
			} else {
				info.setJobExeMonths(Arrays.asList("*"));
				info.setJobExeDays(Arrays.asList("*"));
			}
		}
		return info;
	}

	private List<String> convertStringToList(String value, String split) {
		List<String> list = new ArrayList<String>();
		for (String s : value.split(split))
			list.add(s);
		return list;
	}

	private List<String> getJobDetails(String id) throws SQLException {
		PreparedStatement preparedStatement = this.connection.prepareStatement(SELECT_USER_JOBS + " where User_Jobs.id=?");
		preparedStatement.setString(1, id);
		for (ResultSet resultSet = preparedStatement.executeQuery(); resultSet.next();) {
			return Arrays.asList(new String[] { resultSet.getString(3), resultSet.getString(4) });
		}
		return null;
	}

	private Boolean deleteDetailJob(String id) throws SQLException {
		PreparedStatement preparedStatement = this.connection.prepareStatement(DELETE_USER_JOB_DETAILS_INFO);
		preparedStatement.setString(1, id);
		return preparedStatement.execute();
	}

	private Boolean deleteHeaderJob(String id) throws SQLException {
		PreparedStatement preparedStatement = this.connection.prepareStatement(DELETE_USER_JOB__INFO);
		preparedStatement.setString(1, id);
		return preparedStatement.execute();
	}

	private Boolean deleteQrtzJob(HttpServletRequest request, List<String> jobDetails) throws SchedulerException {
		return JobUtil.deleteJob(request, jobDetails);
	}

	private Boolean updateUserJobDetails(JobInfo info) throws SQLException, ParseException {
		PreparedStatement preparedStatement = this.connection.prepareStatement(UPDATE_USER_JOB_DETAILS_INFO);
		preparedStatement.setString(1, info.getJobDescription());
		if (StringUtil.isStringNullOrEmpty(info.getJobDateTime()))
			preparedStatement.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
		else
			preparedStatement.setTimestamp(2, new Timestamp(DateUtil.getDate(info.getJobDateTime(), DATE_FORMAT2).getTime()));
		preparedStatement.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
		preparedStatement.setString(4, info.getClientId());
		preparedStatement.executeUpdate();
		return true;
	}

	private Boolean updateHeaderUserJob(JobInfo info) throws SQLException {
		PreparedStatement preparedStatement = this.connection.prepareStatement(UPDATE_USER_JOB_INFO);
		preparedStatement.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
		preparedStatement.setString(2, info.getClientId());
		preparedStatement.executeUpdate();
		return true;
	}

	private JobDetail updateQrtzJob(HttpServletRequest request, JobInfo info) throws SchedulerException, ParseException {
		return JobUtil.updateJob(request, info);
	}
}
