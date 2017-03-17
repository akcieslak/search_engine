import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.fabric.xmlrpc.base.Data;

/*
 * Allows a user to log in
 */
public class SignupServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
		HttpSession session = request.getSession();
		
		//if user is logged in, redirect
		if(session.getAttribute(NAME) != null) {
			response.sendRedirect(response.encodeRedirectURL("/search"));
			return;
		}
				
		String status = getParameterValue(request, STATUS);
				
		boolean statusok = status != null && status.equals(ERROR)?false:true;
		boolean redirected = status != null && status.equals(NOT_LOGGED_IN)?true:false;
		
		//output text box requesting user name
		PrintWriter out = prepareResponse(response);
		out.println("<html><body>");

		
		out.println("<style>body {background-color:powderblue}"
				+ "div{"
				+ "height: 200px;"
				+ "width: 400px;"
				+ "position: fixed;"
				+ "top: 50%;"
				+ "left: 50%;"
				+ "margin-top: -100px;"
				+ "margin-left: -200px</style>");

		
//		//if the user was redirected here as a result of an error
		if(!statusok) {
			out.println("<h3><font color=\"red\">Invalid Information</font></h3>");
		} else if(redirected) {
			out.println("<h3><font color=\"red\">Log in first!</font></h3>");
		}

		out.println("<div><center><form name=\"name\" action=\"signup\" method=\"post\"></center>");
		out.println("<center>Sign Up Here</center>");
		out.println("<center>Name:");
		out.println("<input type=\"text\" name=\"name\"/></center>");
		out.println("<center>Username:");
		out.println("<input type=\"text\" name=\"username\"/></center>");
		out.println("<center>Password:");
		out.println("<input type=\"text\" name=\"password\"/></center>");
		out.println("<center><input type=\"submit\" name=\"button\" value=\"Signup!\"/>");
		out.println("<input type=\"submit\" name=\"button\" value=\"Return\"/></center>");
		out.println("</form></div>");
		
		out.println("</body></html>");
	}
	
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String name = request.getParameter(NAME);
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		PrintWriter out = prepareResponse(response);
		HttpSession session = request.getSession();
		
		String button = request.getParameter("button");
		if (button.equals("Return")){
			response.sendRedirect("/search");
			return;
		}
		
		if (username == null || username.trim().equals("") || password == null || password.trim().equals("") || name == null || 
				name.trim().equals("")){
			response.sendRedirect("/signup");
			return;
		}
		
		//adds someone to the database
		try {
			JDBC jdbc = new JDBC();
			jdbc.signUp(name, username, password);
			session.setAttribute(NAME, name);
			session.setAttribute(USERNAME, username);
			session.setAttribute(PASSWORD, password);
			response.sendRedirect("/search");
		} catch (SQLException e) {
			response.sendRedirect(response.encodeRedirectURL("/signup?" + STATUS + "=" + ERROR));
			System.out.println("There is an error: " + e);
		}
	
		
	
	}
	
	
}
