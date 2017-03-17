import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class WebCrawler extends Builder{

	private int PORT = 80;
	private MultiThreadedInvertedIndex multiInvertedIndex;
	private Pattern pattern;
	private int pendingCount;
	private Object pending;
	private HashMap<String, Boolean> urlMap;
	private HashMap<String, String> snippetMap;

	
	public WebCrawler(URL seedURL, Boolean digitDelimiter, int numberOfThreads){
		super(digitDelimiter, numberOfThreads);
		this.multiInvertedIndex = new MultiThreadedInvertedIndex();
		this.pattern = super.getPattern();
		this.pendingCount = 0;
		this.pending = new Object();
		urlMap = new HashMap<String, Boolean>();
		snippetMap = new HashMap<String, String>();

	}
	
	public WebCrawler(URL seedURL, Boolean digitDelimiter, int numberOfThreads, MultiThreadedInvertedIndex index){
		super(digitDelimiter, numberOfThreads);
		this.multiInvertedIndex = index;
		this.pattern = super.getPattern();
		this.pendingCount = 0;
		this.pending = new Object();
		urlMap = new HashMap<String, Boolean>();
		snippetMap = new HashMap<String, String>();
		
	}
	
	
	/**
	 * Users super to call shutdown on the workQueue
	 * @param - workQueue
	 */
	public void shutdownWorkQueue(){
		super.shutdownWorkQueue();	
	}

	
	/**
	 * Adds the first URL into the urlList in the multiInvertedIndex class. Starts the 
	 * workQueue with the first URL and calls the waitObject class in workQueue to wait
	 * on an object to ensure that all links are added to workQueue before returning.
	 * @param - workQueue
	 */
	public void parseLinks(WebCrawler webCrawler, URL seedURL) throws InterruptedException {
		canAdd(seedURL.getHost() + seedURL.getPath());
		addPendingCount();
		workQueue.execute(new CrawlFile(webCrawler, workQueue, multiInvertedIndex, pattern, seedURL));
		synchronized(pending){
			pending.wait();
		}

	}
	
	
	/**
	 * Getter function that returns the index
	 * @param fileName
	 */
	public MultiThreadedInvertedIndex getIndex(){
		return multiInvertedIndex;
	}
	
		
	/**
	 * Adds to the pendingCount variable (Called when a job is added to the workQueue)
	 */
    public synchronized void addPendingCount(){
    	pendingCount++;
    }
    
    
	/**
	 * Subtracts one from the pendingCount variable (Called when a job is finished in the 
	 * workQueue)
	 */
    public synchronized void minusPendingCount() throws InterruptedException{
    	pendingCount--;
		if (getPendingCount() == 0){
			synchronized(pending){
				pending.notify();
			}
		}
    }
    
    
	/**
	 * Returns the current pendingCount
	 * @param - pendingCount (int)
	 */
    public synchronized int getPendingCount(){
    	return pendingCount;
    }
    
 	
	/**
	 * Returns true if urlList size is less than 50, and false if 50.
	 * @return - boolean value
	 * @throws InterruptedException 
	 */
	public synchronized boolean canAdd(String url) throws InterruptedException{
		if(urlMap.size() >= 50){
			return false;
		} if (urlMap.containsKey(url)){
			return false;
		}
		urlMap.put(url, true);
		return true;
	}
	
	
	public synchronized void addSnippetMap(String url, String snippet){
		snippetMap.put(url, snippet);
	}
	
	public synchronized String getSnippet(String url){
		return snippetMap.get(url);
	}
	

	public static void main(String[] args) {
		

		
	}
	
	
	

	
}
