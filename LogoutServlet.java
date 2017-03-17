import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class LogoutServlet extends BaseServlet{
	/*
	 * Allows a user to log in
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		HttpSession session = request.getSession();
		session.invalidate();
		response.sendRedirect(("/search"));
		
		
		
		
		
		
	}
	
	
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			String button = request.getParameter("button");
			if (button.equals("Return")){
				response.sendRedirect("/search");
				return;
			}
			if (button.equals("View Search History")){
				response.sendRedirect("/searchhistory");
				return;
			}
			
			HttpSession session = request.getSession();
			session.invalidate();
			response.sendRedirect(("/search"));
			
			
			
		}
	
	

}
