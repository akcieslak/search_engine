import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SearchSeedServlet extends BaseServlet{
	/*
	 * Allows a user to log in
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		HttpSession session = request.getSession();
		
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
		
		if (session.getAttribute(NAME) == null){
			response.sendRedirect("/search");
		}
		
		
		out.println("<div><center><header>Search With Your Seed Here " + session.getAttribute(NAME) + " !</header></center>");		
		
		out.println("<form name=\"query\" action=\"searchseed\" method=\"post\">");
		out.println("<center>Query:");
		out.println("<input type=\"text\" name=\"query\"/></center>");
		out.println("<center>Seed:");
		out.println("<input type=\"text\" name=\"seed\"/></center>");
		out.println("<center><input type=\"submit\" name=\"button\" value=\"Search\"/>");
		out.println("<input type=\"submit\" name=\"button\" value=\"Private Search\"/></center>");
		out.println("<center><input type=\"submit\" name=\"button\" value=\"Return\"/></center>");
		out.println("</form></div>");
		
		
		out.println("</body></html>");
		
	}
	
	
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			Boolean privateSearch = false;
			response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
			HttpSession session = request.getSession();
			
			String button = request.getParameter("button");
			if (button.equals("Return")){
				response.sendRedirect("/search");
				return;
			}  
			if (button.equals("Private Search")){
				privateSearch = true;
			}
			
			
			PrintWriter out = response.getWriter();
			
			String query = request.getParameter("query");
			String seed = request.getParameter("seed");
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
			URL u1 = new URL(seed);
			URL u2 = new URL("http://www.dmoz.org/");
			WebCrawler webCrawler1 = new WebCrawler(u1, true, 5);
			try {
				webCrawler1.parseLinks(webCrawler1, u1);
			} catch (InterruptedException e) {
				System.out.println("There was an error: " + e);
			}
			webCrawler1.shutdownWorkQueue();
			index = webCrawler1.getIndex();
			WebCrawler webCrawler2 = new WebCrawler(u2, true, 5, index);
			try {
				webCrawler2.parseLinks(webCrawler2, u2);
			} catch (InterruptedException e) {
				System.out.println("There was an error: " + e);
			}
			index = webCrawler2.getIndex();
			
			
			
			QueriesBuilder queriesBuilder = new QueriesBuilder(index, true);
			queriesBuilder.scanQuery(index, query);
			queriesBuilder.shutdownWorkQueue();

			
			for (String temp : queriesBuilder.getURLList(query)){
				if (webCrawler1.getSnippet(temp) == null){
					out.println("<tr><td><header><a href="  + temp + ">" + temp + "</a></header></td><td>" + webCrawler2.getSnippet(temp) + "</td></tr>");
				} else {
					out.println("<tr><td><header><a href="  + temp + ">" + temp + "</a></header></td><td>" + webCrawler1.getSnippet(temp) + "</td></tr>");

				}
				
			}
			out.println("</table>");
			
			out.println("<form name=\"return\" action=\"search\" method=\"get\">");
			out.println("<center><input type=\"submit\" name=\"button\" value=\"Return\"/>");
			out.println("</form");

			
		}
		
	
	

}
