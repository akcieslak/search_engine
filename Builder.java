import java.io.File;
import java.net.URL;
import java.util.regex.Pattern;


public class Builder {

	private Boolean digitDelimiter; 
	protected WorkQueue workQueue;
	
	public Builder(Boolean digitDelimiter, int numberOfThreads){
		this.digitDelimiter = digitDelimiter;	
		this.workQueue = new WorkQueue(numberOfThreads);
	}
	
	
	/**
	 * Returns the Pattern that should be used based on the Boolean digitDelimeter
	 */
	public Pattern getPattern(){
		if (digitDelimiter == true){
			return Pattern.compile("[^a-zA-Z]+");
			
		}
		else {
			return Pattern.compile("[^a-zA-Z0-9]+");
		}
	}
	
	
	/**
	 * Calls shutdown and join on the work queue.
	 */
	public void shutdownWorkQueue(){
		workQueue.shutdown();
		workQueue.awaitTermination();
	}
}
