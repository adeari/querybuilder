package apps.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import apps.entity.Users;

/**
 * Servlet implementation class DownloadFile1
 */
@WebServlet("/DownloadFile1")
public class DownloadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		/* download file
		String filePath = "C:/log/csv/dy2a8ZU201506281407057.csv";
		File downloadFile = new File(filePath);
		FileInputStream inStream = new FileInputStream(downloadFile);
		ServletContext context = getServletContext();
        
        String mimeType = context.getMimeType(filePath);
        if (mimeType == null) {        
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
        
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
         
        OutputStream outStream = response.getOutputStream();
         
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
         
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        
        inStream.close();
        outStream.close();   
		*/
		/*response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			out.println("<!DOCTYPE html>"); // HTML 5
			out.println("<html><head>");
			out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
			out.println("<title>Download file</title></head>");
			out.println("<body>");
			out.println("<h1>Login</h1>"); // Prints "Hello, world!"
			HttpSession session = request.getSession(true);
			Users user =  (Users) session.getAttribute("userlogin");
			out.println(user.getUsername());
			out.println("</body></html>");
		} finally {
			out.close(); // Always close the output writer
		}*/
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//do nothing
	}

}
