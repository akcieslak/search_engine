import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class CrawlFile implements Runnable{

	private MultiThreadedInvertedIndex multiInvertedIndex; 
	private Pattern pattern;
	private int PORT = 80;
	private URL url = null;
	private WorkQueue workQueue;
	private WebCrawler webCrawler;
	
	
	public CrawlFile(WebCrawler webCrawler, WorkQueue workQueue, MultiThreadedInvertedIndex invertedIndex, 
			Pattern pattern, URL link){
		this.webCrawler = webCrawler;
		this.workQueue = workQueue;
		this.multiInvertedIndex = invertedIndex;
		this.pattern = pattern;
		this.url = link;
	}
	
	
	@Override
	public void run() {
		try {
			readURL();
		} catch (InterruptedException e) {
			System.out.println("There was an error: " + e);
		}
	}
	
	
	/**
	 * Downloads the URL and gets all of the links that are found on that URL and then checks
	 * the validity of the URLs and for the valid URLs, adds the link to the workQueue, adds 
	 * to the pendingCount and then cleans the original URL and adds words to Inverted Index.
	 * If the pendingCount is 0, we notify our pending object in workQueue to shutdown, and
	 * if the size of the urlList is 50, we stop adding to the queue. 
	 */
	public void readURL() throws InterruptedException{
		String page = socketDownload(url.getHost(), url.getPath());
		ArrayList<String> links = HTMLLinkParser.listLinks(page);
		// Parses links and goes through the list of links form a page
		for (String tempLink : links){
			URL absolute = null;
			
			try{
				absolute = new URL(url, tempLink);
			} catch (MalformedURLException e) {
				absolute = null;
			}	
				
			
			if (absolute != null && webCrawler.canAdd(absolute.getHost() + absolute.getPath())){
				webCrawler.addPendingCount();
				workQueue.execute(new CrawlFile(webCrawler, workQueue, multiInvertedIndex,
						pattern, absolute));
			}

		}

		// Cleans input and adds to Inverted Index
		String cleanPage = cleanFile(page);
		//GET THE FIRST 100 CHARACTER
		String substring = cleanPage.substring(0, cleanPage.length()/100);
		webCrawler.addSnippetMap("http://" + url.getHost() + url.getPath(), substring);
		scanFile(this.pattern, "http://" + url.getHost() + url.getPath(), cleanPage);
		webCrawler.minusPendingCount();
	}
		
	
	/**
	 * Reads in the file and for every words adds to the multi threaded inverted index
	 * @param pattern - pattern to specify for the delimiter
	 * @param fileName - (String)the name of the document you are reading 
	 * @param file - the file that you are reading
	 */
	public void scanFile(Pattern pattern, String fileName, String page){
		ArrayList<FileInfo> tempList = new ArrayList<FileInfo>();
		int location = 1;
		try (Scanner fileScanner = new Scanner(page).useDelimiter(pattern)){
			while (fileScanner.hasNext()){
				String nextToken = fileScanner.next().toLowerCase();
				tempList.add(new FileInfo(nextToken, fileName, location));
				location++;
			}
		}
		multiInvertedIndex.addAll(tempList);
	}
	
	
	/**
	 * Calls HTMLCleaner to clean the html 
	 * @param - String html to be cleaned
	 * @return - the cleaned html 
	 */
	public String cleanFile(String html){
		return HTMLCleaner.cleanHTML(html);
	}
	
	
	/**
	 * @param - String host
	 * @param - String path
	 * @return
	 */
	public String socketDownload(String host, String path){
		StringBuffer buf = new StringBuffer();
		
		try (Socket sock = new Socket(host, PORT);
				OutputStream out = sock.getOutputStream(); 
				InputStream instream = sock.getInputStream(); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream))) { 
			String request = getRequest(host, path);
			out.write(request.getBytes());
			out.flush();

			String line = reader.readLine();
			while(line != null) {				
				buf.append(line + "\n");
				line = reader.readLine();
			}

		} catch (IOException e) {
			System.out.println("HTTPFetcher::download " + e.getMessage());
		}
		return buf.toString();
	}
	
	
	private static String getRequest(String host, String path) {
		String request = "GET " + path + " HTTP/1.1" + "\n" 
				+ "Host: " + host + "\n" 
				+ "Connection: close\n" 
				+ "\r\n";								
		return request;
	}
	

}

