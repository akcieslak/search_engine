import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.fabric.xmlrpc.base.Data;



public class SearchServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		HttpSession session = request.getSession();
		
				
		String status = getParameterValue(request, STATUS);
				
		boolean statusok = status != null && status.equals(ERROR)?false:true;
		boolean redirected = status != null && status.equals(NOT_LOGGED_IN)?true:false;
		

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
		

		
		if (session.getAttribute(NAME) != null){
			JDBC jdbc;
			String date = "";
			ArrayList<String> lastlogs = new ArrayList<String>();
			try {
				jdbc = new JDBC();
				date = jdbc.getLastLogin((String)session.getAttribute(USERNAME));
				lastlogs = jdbc.lastLogs();
			} catch (SQLException e) {
				System.out.println("There was an error: " + e);
			}
			

			
			String name = (String) session.getAttribute(NAME);
			out.println("<div><center><header>Welcome " + name + "!</header></center>");
			
			
		
			
			out.println("<form name=\"query\" action=\"search\" method=\"post\">");
			out.println("<center>Query:");
			out.println("<input type=\"text\" name=\"query\"/></center>");
			out.println("<center><input type=\"submit\" name=\"button\" value=\"Search\"/>");
			out.println("<input type=\"submit\" name=\"button\" value=\"Private Search\"/></center>");
			out.println("<center><input type=\"submit\" name=\"button\" value=\"Search your own seed\"/></center>");
			
			out.println("<center><input type=\"submit\" name=\"button\" value=\"View Search History\"/></center>");
			out.println("<center><input type=\"submit\" name=\"button\" value=\"Logout\"/></center>");
			out.println("<center><input type=\"submit\" name=\"button\" value=\"Change Password\"/></center>");
			
			out.println("<center><header>Last 5 Logins: </header></center>");
			out.println("<center>");
			for (String temp : lastlogs){
				out.print(temp + "   ");
			}
			out.println("<br/></center>");
			out.println("<center><header>Your Last Login: " + date + "</header></center>");	
			
			
			out.println("</form></div>");
			
			
			
		}
		
		else {
			out.println("<div><center><header> Welcome! </header></center>");
			out.println("<form name=\"query\" action=\"search\" method=\"post\">");
			out.println("<center>Query:");
			out.println("<input type=\"text\" name=\"query\"/></center>");
			out.println("<center><input type=\"submit\" name=\"button\" value=\"Search\"/>");
			out.println("<input type=\"submit\" name=\"button\" value=\"Login\"/>");
			out.println("<input type=\"submit\" name=\"button\" value=\"Sign up\"/></center>");
			out.println("</form></div>");
		}
		
		out.println("</body></html>");

		out.println(footer());
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Boolean privateSearch = false;
		response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
		HttpSession session = request.getSession();
		
		String button = request.getParameter("button");
		if (button.equals("Login")){
			response.sendRedirect("/login");
			return;
		}
		
		if (button.equals("Sign up")){
			response.sendRedirect("/signup");
			return;
		}
		
		if (button.equals("Logout")){
			response.sendRedirect("/logout");
			return;
		}
		
		if (button.equals("View Search History")){
			response.sendRedirect("/searchhistory");
			return;
		}
		
		if (button.equals("Change Password")){
			response.sendRedirect("/changepassword");
			return;
		}
		
		if (button.equals("Search your own seed")){
			response.sendRedirect("/searchseed");
			return;
		}
		
		else {
			//POST /query
			if (button.equals("Private Search")){
				privateSearch = true;
			} else {
				privateSearch = false;
			}
			
			
			
			PrintWriter out = response.getWriter();
	
			String query = request.getParameter("query");
			//page to crawl
			
			//add the query to the sql w/ the username and timestamp
			if (session.getAttribute(USERNAME) != null && privateSearch == false){
				String username = (String) session.getAttribute(USERNAME);
				try {
					JDBC jdbc = new JDBC();
					jdbc.addSearchHistory(username, query);
				} catch (SQLException e) {
					System.out.println("There is an error: " + e);
				}
			}
			
			
			
			out.println("<html><header><center>Search: " + query + "</header></center>");
			out.println("<table style='width:100%'>");
			out.println("<th>Links</th>");
			out.println("<th>Snippets</th>");
			out.println("<style>table, th, td { "
					+ "border: 1px solid black;"
					+ "border-collapse: collapse;"
					+ "}"
					+ "th, td {"
					+ "padding: 15px;"
					+ "}</style>");
			
			MultiThreadedInvertedIndex index;
			URL u1 = new URL("http://www.dmoz.org/");
			WebCrawler webCrawler = new WebCrawler(u1, true, 5);
			try {
				webCrawler.parseLinks(webCrawler, u1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			webCrawler.shutdownWorkQueue();
			index = webCrawler.getIndex();	
			
			
			QueriesBuilder queriesBuilder = new QueriesBuilder(index, true);
			queriesBuilder.scanQuery(index, query);
			queriesBuilder.shutdownWorkQueue();
			
			for (String temp : queriesBuilder.getURLList(query)){

				// <a href="http://www.java2s.com" onClick='alert("You clicked the link")'>www.java2s.com</a>

//				out.println("<tr><td><header><a href="  + temp + "onClick = 'jdbc.addLink(" + (String) session.getAttribute(USERNAME) + "," + temp + ");'>" + temp + "</a></header></td></tr>");
				//out.println("<tr><td><header><a href="  + "\"temp\"" + "onClick = 'alert(" + "\"You clicked the link\"" + ")'>" + temp + "</a></header></td></tr>");
				//out.println("<tr><td><header><a href="  + temp + "onClick = 'myFunction(" + (String) session.getAttribute(USERNAME) + "," + temp + ");'>" + temp + "</a></header></td></tr>");
				out.println("<tr><td><header><a href="  + temp + ">" + temp + "</a></header></td><td>" + webCrawler.getSnippet(temp) + "</td></tr>");

			}
			
//			out.println("<script>");
//			out.println("function myFunction(String username, String url) {"
//					+ "jdbc.addLink(username, link)");
//			out.println("<script>");
//			<script>
//			function myFunction() {
//			    document.getElementById("field2").value = document.getElementById("field1").value;
//			}
//			</script>
			
			
			out.println("</table>");
			
			if (session.getAttribute(NAME) != null && privateSearch == false){
				out.println("<form name=\"logout\" action=\"logout\" method=\"post\">");
				out.println("<center><input type=\"submit\" name=\"button\" value=\"Logout\"/>");
				out.println("<input type=\"submit\" name=\"button\" value=\"Return\"/>");
				out.println("<input type=\"submit\" name=\"button\" value=\"View Search History\"/></center>");
				out.println("</form");
			}
			
			else if (session.getAttribute(NAME) != null && session.getAttribute(PRIVATE) != null){
				out.println("<form name=\"logout\" action=\"logout\" method=\"post\">");
				out.println("<center><input type=\"submit\" name=\"button\" value=\"Logout\"/>");
				out.println("<input type=\"submit\" name=\"button\" value=\"Return\"/>");
				out.println("</form");
			}
			
			else {
				out.println("<form name=\"logout\" action=\"search\" method=\"get\">");
				out.println("<center><input type=\"submit\" name=\"button\" value=\"Return\"/></center>");
				out.println("</form");
			}
			
			out.println("</body></html>");
					
			
			
		}

	}
	
	
	
}
