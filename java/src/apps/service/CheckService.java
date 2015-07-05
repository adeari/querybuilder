package apps.service;

import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import apps.entity.Activity;
import apps.entity.FileSizeUsed;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.entity.UsersQuery;

public class CheckService {
	private static final Logger logger = Logger.getLogger(CheckService.class);

	public void userIsDeleted(Users user) {
		Session querySession = null;

		try {
			querySession = hibernateUtil.getSessionFactory().openSession();

			userIsDeleted(querySession, user);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
			if (querySession != null) {
				try {
					querySession.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void userIsDeleted(Session querySession, Users user)
			throws Exception {
		long userCount = 0;
		Criteria criteria = querySession.createCriteria(QueryData.class)
				.setProjection(Projections.rowCount());
		criteria.add(Restrictions.or(Restrictions.eq("modifiedBy", user),
				Restrictions.eq("createdBy", user)));
		userCount += (long) criteria.uniqueResult();

		criteria = querySession.createCriteria(UsersQuery.class).setProjection(
				Projections.rowCount());
		criteria.add(Restrictions.eq("userData", user));
		userCount += (long) criteria.uniqueResult();
		
		criteria = querySession.createCriteria(Activity.class).setProjection(
				Projections.rowCount());
		criteria.add(Restrictions.eq("userCreated", user));
		userCount += (long) criteria.uniqueResult();
		
		criteria = querySession.createCriteria(FileSizeUsed.class);
		criteria.add(Restrictions.eq("userOwner", user));
		List<FileSizeUsed> fileSizeUseds = criteria.list();
		for (FileSizeUsed fileSizeUsed : fileSizeUseds) {
			userCount += Double.valueOf(fileSizeUsed.getFilesize()).longValue();
		}
		

		Transaction trx = querySession.beginTransaction();

		if (userCount > 0) {
			user.setIsdeleted(false);
		} else {
			user.setIsdeleted(true);
		}
		querySession.update(user);

		trx.commit();
	}

	public void queryIsDeleted(QueryData queryData) {
		Session querySession = null;

		try {
			querySession = hibernateUtil.getSessionFactory().openSession();

			queryIsDeleted(querySession, queryData);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
			if (querySession != null) {
				try {
					querySession.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void queryIsDeleted(Session querySession, QueryData queryData)
			throws Exception {
		long cuntData = 0;

		Criteria criteria = querySession.createCriteria(UsersQuery.class)
				.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("queryData", queryData));
		cuntData += (long) criteria.uniqueResult();

		Transaction trx = querySession.beginTransaction();

		if (cuntData > 0) {
			queryData.setDeleted(false);
			;
		} else {
			queryData.setDeleted(true);
		}
		querySession.update(queryData);

		trx.commit();
	}
	
	public static boolean isValidEmailAddress(String email) {
		   boolean result = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   return result;
		}
}
