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
import org.hibernate.criterion.Restrictions;

import apps.entity.Activity;
import apps.entity.FilesData;
import apps.entity.Users;
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
			out.println("Password <input type=\"text\" name=\"password\">");
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

	private void downloadFile(HttpServletResponse response, String linkDownload, Users user) {
		serviceMain = new ServiceImplMain();
		org.hibernate.Session sessionSelect = null;
		try {
			sessionSelect = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = sessionSelect.createCriteria(Activity.class);
			criteria.createAlias("fileData", "fileData");
			criteria.add(Restrictions.eq("fileData.downloadLink", linkDownload));
			if (!user.getDivisi().equalsIgnoreCase("admin")) {
				criteria.add(Restrictions.eq("userCreated", user));
			}
			Activity activity = (Activity) criteria.uniqueResult();
			if (activity == null) {
				showMessage(response, "This file not exist");
				return;
			}
			FilesData filesData = activity.getFileData();
			if (filesData == null) {
				showMessage(response, "This file not exist");
				return;
			} else {
				String filePath = serviceMain.getQuery("location."
						+ filesData.getFiletype().toLowerCase())+"/"
						+ filesData.getFilename();
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

		} finally {
			if (sessionSelect != null) {
				try {
					sessionSelect.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		serviceMain = new ServiceImplMain();
		org.hibernate.Session sessionSelect = null;
		try {
			sessionSelect = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = sessionSelect.createCriteria(Users.class);
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

		} finally {
			if (sessionSelect != null) {
				try {
					sessionSelect.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

}
