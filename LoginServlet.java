import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.fabric.xmlrpc.base.Data;

/*
 * Allows a user to log in
 */
public class LoginServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		//if user is logged in, redirect
		if(session.getAttribute(USERNAME) != null) {
			response.sendRedirect(response.encodeRedirectURL("/search"));
			return;
		}
				
		String status = getParameterValue(request, STATUS);
				
		boolean statusok = status != null && status.equals(ERROR)?false:true;
		boolean redirected = status != null && status.equals(NOT_LOGGED_IN)?true:false;
		
		//output text box requesting user name
		PrintWriter out = prepareResponse(response);
		
		out.println("<html><body>");

		//if the user was redirected here as a result of an error
		if(!statusok) {
			out.println("<h3><font color=\"red\">Invalid Request to Login</font></h3>");
		} else if(redirected) {
			out.println("<h3><font color=\"red\">Log in first!</font></h3>");
		}
		
		out.println("<style>body {background-color:powderblue}"
				+ "div{"
				+ "height: 200px;"
				+ "width: 400px;"
				+ "position: fixed;"
				+ "top: 50%;"
				+ "left: 50%;"
				+ "margin-top: -100px;"
				+ "margin-left: -200px</style>");

		out.println("<div><center><form name=\"name\" action=\"verifyuser\" method=\"post\">");
		out.println("<center>Login Here</center>");
		out.println("Username:");
		out.println("<input type=\"text\" name=\"username\"/></center>");
		out.println("<center>Password:");
		out.println("<input type=\"text\" name=\"password\"/></center>");
		out.println("<center><input type=\"submit\" name=\"button\" value=\"Login\"/>");
		out.println("<input type=\"submit\" name=\"button\" value=\"Return\"/></center>");
		out.println("</form></div>");
		
		out.println("</body></html>");
		
	}
	
}
