import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * Servlet invoked at login.
 * Creates cookie and redirects to main ListServlet.
 */
public class VerifyUserServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//VerifyUser does not accept GET requests. Just redirect to login with error status.
		response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		String username = request.getParameter(USERNAME);
		String password = request.getParameter(PASSWORD);
		Boolean verify = false;
		JDBC jdbc;
		String name = null;
		String button = request.getParameter("button");

		System.out.println(button);
		if (button.equals("Return")){
			response.sendRedirect("/search");
			return;
		}
		
		if(username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
			return;
		}
		
		try {
			jdbc = new JDBC();
			verify = jdbc.verifyUser(username, password);
			name = jdbc.getName(username);
		} catch (SQLException e) {
			System.out.println("There was an error: " + e);
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
		}
		
		
		if (verify){
			HttpSession session = request.getSession();
			session.setAttribute(USERNAME, username);
			session.setAttribute(PASSWORD, password);
			session.setAttribute(NAME, name);
			response.sendRedirect(response.encodeRedirectURL("/search"));
		} else {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
		}
		

		
		
		
	}
	
}
