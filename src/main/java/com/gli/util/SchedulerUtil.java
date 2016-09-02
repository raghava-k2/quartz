package com.gli.util;

import javax.servlet.http.HttpServletRequest;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.ee.servlet.QuartzInitializerServlet;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerUtil {
	public static Scheduler getSchedulerInstance(HttpServletRequest request) throws SchedulerException {
		return ((StdSchedulerFactory) request.getServletContext().getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY)).getScheduler();
	}
}
