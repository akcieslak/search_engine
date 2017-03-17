import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.fabric.xmlrpc.base.Data;

/*
 * Allows a user to log in
 */
public class SearchHistoryServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		Data data = (Data) getServletConfig().getServletContext().getAttribute(DATA);
		HttpSession session = request.getSession();
		
		//if user is logged in, redirect
		if(session.getAttribute(USERNAME) == null) {
			response.sendRedirect(response.encodeRedirectURL("/search"));
			return;
		}
				
		String status = getParameterValue(request, STATUS);
		
		//output text box requesting user name
		PrintWriter out = prepareResponse(response);
		
		ArrayList<String> history = new ArrayList<String>();
		ArrayList<String> timeStamps = new ArrayList<String>();

		
		out.println("<html><body>");
		
		try {
			JDBC jdbc = new JDBC();
			history = jdbc.getSearchHistory((String)session.getAttribute(USERNAME));
			timeStamps = jdbc.getTimeStamps((String)session.getAttribute(USERNAME));
		} catch (SQLException e) {
			System.out.println("There was an error: " + e);
		}
		
		out.println("<header><center>"  + (String)session.getAttribute(NAME) + "'s Search History: " + "</center></header>");

		out.println("<table style='width:100%'>");
		out.println("<th>Query</th>");
		out.println("<th>Time Stamp</th>");
		out.println("<style>table, th, td { "
				+ "border: 1px solid black;"
				+ "border-collapse: collapse;"
				+ "}"
				+ "th, td {"
				+ "padding: 15px;"
				+ "}</style>");

		
		for (int i = 0; i < history.size(); i++){
			out.println("<tr>");
			out.println("<td><center>"  + history.get(i) + "</center></td>");
			out.println("<td><center>"  + timeStamps.get(i) + "</center></td>");
			out.println("</tr>");
		}
		out.println("</table>");
		


		out.println("<form name=\"return\" action=\"searchhistory\" method=\"post\">");
		out.println("<center><input type=\"submit\" name=\"button\" value=\"Return\"/>");
		out.println("<input type=\"submit\" name=\"button\" value=\"Clear History\"/></center>");
		out.println("</form");

		out.println("</body></html>");
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String button = request.getParameter("button");
		if (button.equals("Return")){
			response.sendRedirect("/search");
			return;
		}
		
		if (button.equals("Clear History")){
			try {
				JDBC jdbc = new JDBC();
				jdbc.clearSearchHistory((String)session.getAttribute(USERNAME));
			} catch (SQLException e) {
				System.out.println("There was an error: " + e);
			}
			response.sendRedirect("/searchhistory");
			return;
		}
		
		
		
	}

	
	
	
}
