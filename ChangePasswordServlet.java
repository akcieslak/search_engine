import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangePasswordServlet extends BaseServlet{
	/*
	 * Allows a user to log in
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = prepareResponse(response);
		
		HttpSession session = request.getSession();
		if (session.getAttribute(NAME) == null){
			response.sendRedirect("/search");
		}
		
		out.println("<style>body {background-color:powderblue} "
				+ "div{"
				+ "height: 200px;"
				+ "width: 400px;"
				+ "position: fixed;"
				+ "top: 50%;"
				+ "left: 50%;"
				+ "margin-top: -100px;"
				+ "margin-left: -200px</style>");
		
		out.println("<div><center><form name=\"name\" action=\"changepassword\" method=\"post\">");
		out.println("Username: ");
		out.println("<input type=\"text\" name=\"username\"/></center>");
		out.println("<center>Old Password:");
		out.println("<input type=\"text\" name=\"oldpassword\"/></center>");
		out.println("<center>New Password:");
		out.println("<input type=\"text\" name=\"newpassword\"/></center>");
		out.println("<center><input type=\"submit\" name=\"button\" value=\"Reset\"/>");
		out.println("<input type=\"submit\" name=\"button\" value=\"Return\"/></center>");
		out.println("</form></div>");
		
		
		
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		JDBC jdbc;
		boolean verify;
		String button = request.getParameter("button");
		if (button.equals("Return")){
			response.sendRedirect("/search");
			return;
		}
		
		
		try {
			jdbc = new JDBC();
			verify = jdbc.verifyUser(username, oldpassword);
			if (verify){
				jdbc.changePassword(username, newpassword);
			} else {
				response.sendRedirect("/changepassword");		
				return;
			}
		} catch (SQLException e) {
			System.out.println("There was an error: " + e);
			response.sendRedirect(response.encodeRedirectURL("/changepassword?" + STATUS + "=" + ERROR));
			return;
		}
			
		response.sendRedirect("/search");		
	}
	
	

}
