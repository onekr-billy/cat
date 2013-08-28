package com.dianping.cat.report.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.Cat;
import com.dianping.cat.Constants;
import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.cat.consumer.cross.CrossAnalyzer;
import com.dianping.cat.consumer.cross.model.entity.CrossReport;
import com.dianping.cat.consumer.event.EventAnalyzer;
import com.dianping.cat.consumer.event.model.entity.EventReport;
import com.dianping.cat.consumer.matrix.MatrixAnalyzer;
import com.dianping.cat.consumer.matrix.MatrixReportFilter;
import com.dianping.cat.consumer.matrix.model.entity.MatrixReport;
import com.dianping.cat.consumer.problem.ProblemAnalyzer;
import com.dianping.cat.consumer.problem.model.entity.ProblemReport;
import com.dianping.cat.consumer.sql.SqlAnalyzer;
import com.dianping.cat.consumer.sql.model.entity.SqlReport;
import com.dianping.cat.consumer.state.StateAnalyzer;
import com.dianping.cat.consumer.state.model.entity.StateReport;
import com.dianping.cat.consumer.transaction.TransactionAnalyzer;
import com.dianping.cat.consumer.transaction.TransactionReportUrlFilter;
import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.core.dal.MonthlyReport;
import com.dianping.cat.core.dal.WeeklyReport;
import com.dianping.cat.helper.TimeUtil;
import com.dianping.cat.home.bug.entity.BugReport;
import com.dianping.cat.home.service.entity.ServiceReport;
import com.dianping.cat.message.Transaction;

public class CachedReportTask implements Task {

	private long m_end;

	@Inject
	private ReportService m_reportService;

	@Override
	public String getName() {
		return "Cached-Report-Task";
	}

	private WeeklyReport buildWeeklyReport(String domain, Date period, String name, String content) {
		WeeklyReport report = new WeeklyReport();

		report.setContent(content);
		report.setCreationDate(new Date());
		report.setDomain(domain);
		report.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
		report.setName(name);
		report.setPeriod(period);
		report.setType(1);
		return report;
	}

	private MonthlyReport buildMonthlyReport(String domain, Date period, String name, String content) {
		MonthlyReport report = new MonthlyReport();

		report.setContent(content);
		report.setCreationDate(new Date());
		report.setDomain(domain);
		report.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
		report.setName(name);
		report.setPeriod(period);
		report.setType(1);
		return report;
	}

	private void reloadCurrentMonthly() {
		Date start = TimeUtil.getCurrentMonth();
		Date end = TimeUtil.getCurrentDay();
		Set<String> domains = m_reportService.queryAllDomainNames(start, end, TransactionAnalyzer.ID);

		for (String domain : domains) {
			TransactionReport transactionReport = m_reportService.queryTransactionReport(domain, start, end);
			new TransactionReportUrlFilter().visitTransactionReport(transactionReport);

			m_reportService.insertMonthlyReport(buildMonthlyReport(domain, start, TransactionAnalyzer.ID,
			      transactionReport.toString()));

			EventReport eventReport = m_reportService.queryEventReport(domain, start, end);
			m_reportService
			      .insertMonthlyReport(buildMonthlyReport(domain, start, EventAnalyzer.ID, eventReport.toString()));
			ProblemReport problemReport = m_reportService.queryProblemReport(domain, start, end);
			m_reportService.insertMonthlyReport(buildMonthlyReport(domain, start, ProblemAnalyzer.ID,
			      problemReport.toString()));
			CrossReport crossReport = m_reportService.queryCrossReport(domain, start, end);
			m_reportService
			      .insertMonthlyReport(buildMonthlyReport(domain, start, CrossAnalyzer.ID, crossReport.toString()));
			MatrixReport matrixReport = m_reportService.queryMatrixReport(domain, start, end);
			new MatrixReportFilter().visitMatrixReport(matrixReport);
			m_reportService.insertMonthlyReport(buildMonthlyReport(domain, start, MatrixAnalyzer.ID,
			      matrixReport.toString()));
			SqlReport sqlReport = m_reportService.querySqlReport(domain, start, end);
			m_reportService.insertMonthlyReport(buildMonthlyReport(domain, start, SqlAnalyzer.ID, sqlReport.toString()));
		}
		String domain = "Cat";

		StateReport stateReport = m_reportService.queryStateReport(domain, start, end);
		m_reportService.insertMonthlyReport(buildMonthlyReport(domain, start, StateAnalyzer.ID, stateReport.toString()));
		BugReport bugReport = m_reportService.queryBugReport(domain, start, end);
		m_reportService.insertMonthlyReport(buildMonthlyReport(domain, start, Constants.REPORT_BUG, bugReport.toString()));
		ServiceReport serviceReport = m_reportService.queryServiceReport(domain, start, end);
		m_reportService.insertMonthlyReport(buildMonthlyReport(domain, start, Constants.REPORT_SERVICE, serviceReport.toString()));
	}

	private void reloadCurrentWeekly() {
		Date start = TimeUtil.getCurrentWeek();
		Date end = TimeUtil.getCurrentDay();
		Set<String> domains = m_reportService.queryAllDomainNames(start, end, TransactionAnalyzer.ID);

		for (String domain : domains) {
			TransactionReport transactionReport = m_reportService.queryTransactionReport(domain, start, end);
			new TransactionReportUrlFilter().visitTransactionReport(transactionReport);

			m_reportService.insertWeeklyReport(buildWeeklyReport(domain, start, TransactionAnalyzer.ID,
			      transactionReport.toString()));

			EventReport eventReport = m_reportService.queryEventReport(domain, start, end);
			m_reportService.insertWeeklyReport(buildWeeklyReport(domain, start, EventAnalyzer.ID, eventReport.toString()));
			ProblemReport problemReport = m_reportService.queryProblemReport(domain, start, end);
			m_reportService.insertWeeklyReport(buildWeeklyReport(domain, start, ProblemAnalyzer.ID,
			      problemReport.toString()));
			CrossReport crossReport = m_reportService.queryCrossReport(domain, start, end);
			m_reportService.insertWeeklyReport(buildWeeklyReport(domain, start, CrossAnalyzer.ID, crossReport.toString()));
			MatrixReport matrixReport = m_reportService.queryMatrixReport(domain, start, end);
			new MatrixReportFilter().visitMatrixReport(matrixReport);
			m_reportService
			      .insertWeeklyReport(buildWeeklyReport(domain, start, MatrixAnalyzer.ID, matrixReport.toString()));
			SqlReport sqlReport = m_reportService.querySqlReport(domain, start, end);
			m_reportService.insertWeeklyReport(buildWeeklyReport(domain, start, SqlAnalyzer.ID, sqlReport.toString()));
		}
		String domain = "Cat";

		StateReport stateReport = m_reportService.queryStateReport(domain, start, end);
		m_reportService.insertWeeklyReport(buildWeeklyReport(domain, start, StateAnalyzer.ID, stateReport.toString()));
		BugReport bugReport = m_reportService.queryBugReport(domain, start, end);
		m_reportService.insertWeeklyReport(buildWeeklyReport(domain, start, Constants.REPORT_BUG, bugReport.toString()));
		ServiceReport serviceReport = m_reportService.queryServiceReport(domain, start, end);
		m_reportService.insertWeeklyReport(buildWeeklyReport(domain, start, Constants.REPORT_SERVICE, serviceReport.toString()));
	}

	@Override
	public void run() {
		boolean active = true;

		while (active) {
			Date date = TimeUtil.getCurrentDay();
			long time = date.getTime();
			Calendar cal = Calendar.getInstance();

			//assume dailyreport job has been completed in two hours.
			if (time > m_end && cal.get(Calendar.HOUR_OF_DAY) >= 3) {
				Transaction t = Cat.newTransaction("System", "ReportReload");
				try {
					reloadCurrentWeekly();
					reloadCurrentMonthly();
					m_end = date.getTime();
					t.setStatus(Transaction.SUCCESS);
				} catch (Exception e) {
					Cat.logError(e);
					t.setStatus(e);
				} finally {
					t.complete();
				}
			}
			try {
				Thread.sleep(60 * 60 * 1000);
			} catch (InterruptedException e) {
				active = false;
			}
		}
	}

	@Override
	public void shutdown() {
	}

}