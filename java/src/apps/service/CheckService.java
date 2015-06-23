package apps.service;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import apps.controller.ChangePasswordWindow;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.entity.UsersQuery;

public class CheckService {
	private static final Logger logger = Logger
			.getLogger(CheckService.class);
	
	public void userIsDeleted(Users user) {
		Session querySession=null;
		
		try {
			querySession = hibernateUtil
					.getSessionFactory().openSession();
			
			long userCount = 0;
			Criteria criteria = querySession
					.createCriteria(QueryData.class).setProjection(Projections.rowCount());
			criteria.add(Restrictions.or(Restrictions.eq("modifiedBy", user), Restrictions.eq("createdBy", user)));
			userCount += (long) criteria.uniqueResult();
			
			criteria = querySession
					.createCriteria(UsersQuery.class).setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("userData", user));
			userCount += (long) criteria.uniqueResult();
			
			Transaction trx = querySession
					.beginTransaction();
			
			if (userCount > 0) {
				user.setIsdeleted(false);
			} else {
				user.setIsdeleted(true);
			}
			querySession.update(user);
			
			trx.commit();
			
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
	
	public void queryIsDeleted(QueryData queryData) {
		Session querySession=null;
		
		try {
			int cuntData = 0;
			querySession = hibernateUtil
					.getSessionFactory().openSession();
			
			Criteria criteria = querySession
					.createCriteria(UsersQuery.class).setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("queryData", queryData));
			cuntData += (int) criteria.uniqueResult();
			
			Transaction trx = querySession
					.beginTransaction();
			
			if (cuntData > 0) {
				queryData.setDeleted(false);;
			} else {
				queryData.setDeleted(true);
			}
			querySession.update(queryData);
			
			trx.commit();
			
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
}
