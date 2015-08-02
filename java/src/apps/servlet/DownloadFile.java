package apps.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import apps.entity.Activity;
import apps.entity.FilesData;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.entity.UsersQuery;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

/**
 * Servlet implementation class DownloadFile1
 */
@WebServlet("/DownloadFile1")
public class DownloadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DownloadFile.class);
	private ServiceMain serviceMain;
	
	private org.hibernate.Session _sessionSelect;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		showFormDownload(response, null);
	}

	private void showFormDownload(HttpServletResponse response, String message) {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println("<!DOCTYPE html>");
			out.println("<html><head>");
			out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
			out.println("<title>Download file</title></head>");
			out.println("<body>");
			out.println("<h1>Login</h1>");
			if (message != null) {
				out.println("<br/>" + message + "<br/>");
			}
			out.println("<form method=\"post\">");
			out.println("Username <input type=\"text\" name=\"username\">");
			out.println("Password <input type=\"password\" name=\"password\">");
			out.println("<button>Download</button>");
			out.println("</form>");
			out.println("</body></html>");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			out.close(); // Always close the output writer
		}
	}

	private void showMessage(HttpServletResponse response, String message) {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println("<!DOCTYPE html>");
			out.println("<html><head>");
			out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
			out.println("<title>Information</title></head>");
			out.println("<body>");
			out.println("<h1>" + message + "</h1>");
			out.println("</body></html>");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			out.close();
		}
	}

	private void downloadFile(HttpServletResponse response,
			String linkDownload, Users user) {
		serviceMain = new ServiceImplMain();
		try {
			_sessionSelect = hibernateUtil.getSessionFactory(_sessionSelect);
			FilesData filesData = null;
			if (user.getDivisi().equalsIgnoreCase("admin")) {
				Criteria criteria = _sessionSelect
						.createCriteria(FilesData.class);
				criteria.add(Restrictions.eq("downloadLink", linkDownload));
				filesData = (FilesData) criteria.uniqueResult();
			} else if (!user.getDivisi().equalsIgnoreCase("admin")) {
				Criteria criteria = _sessionSelect
						.createCriteria(Activity.class);
				criteria.createAlias("fileData", "fileData");
				criteria.add(Restrictions.eq("fileData.downloadLink",
						linkDownload));
				Activity activity = (Activity) criteria.uniqueResult();
				if (activity == null) {
					showMessage(response, "This file not exist");
					return;
				} else {
					criteria = _sessionSelect.createCriteria(QueryData.class);
					criteria.add(Restrictions.eq("named",
							activity.getQueryName()));
					QueryData queryData = (QueryData) criteria.uniqueResult();
					if (queryData == null) {
						showMessage(response, "This file not exist");
						return;
					} else {
						criteria = _sessionSelect.createCriteria(
								UsersQuery.class).setProjection(
								Projections.rowCount());
						criteria.add(Restrictions.eq("userData", user));
						criteria.add(Restrictions.eq("queryData", queryData));
						if (((long) criteria.uniqueResult()) == 0) {
							showMessage(response, "You don't have access for this file");
							return;
						} else {
							filesData = activity.getFileData();
						}
					}
				}
			}

			if (filesData == null) {
				showMessage(response, "This file not exist");
				return;
			} else {
				String filePath = serviceMain.getQuery("location."
						+ filesData.getFiletype().toLowerCase())
						+ "/" + filesData.getFilename();
				File downloadFile = new File(filePath);
				if (downloadFile.isFile()) {
					FileInputStream inStream = new FileInputStream(downloadFile);
					ServletContext context = getServletContext();

					String mimeType = context.getMimeType(filePath);
					if (mimeType == null) {
						mimeType = "application/octet-stream";
					}

					response.setContentType(mimeType);
					response.setContentLength((int) downloadFile.length());

					String headerKey = "Content-Disposition";
					String headerValue = String.format(
							"attachment; filename=\"%s\"",
							downloadFile.getName());
					response.setHeader(headerKey, headerValue);

					OutputStream outStream = response.getOutputStream();

					byte[] buffer = new byte[4096];
					int bytesRead = -1;

					while ((bytesRead = inStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, bytesRead);
					}

					inStream.close();
					outStream.close();
				} else {
					showMessage(response, "This file not exist");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		serviceMain = new ServiceImplMain();
		try {
			_sessionSelect = hibernateUtil.getSessionFactory(_sessionSelect);
			Criteria criteria = _sessionSelect.createCriteria(Users.class);
			criteria.add(Restrictions.eq("username",
					request.getParameter("username")));
			criteria.add(Restrictions.eq("pass",
					serviceMain.convertPass(request.getParameter("password"))));
			Users user = (Users) criteria.uniqueResult();
			if (user == null) {
				showFormDownload(response, "This user not exist");
			} else {
				downloadFile(response, request.getParameter("ridfil"), user);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}
	}

}
