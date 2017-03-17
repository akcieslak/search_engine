import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class QueriesBuilder extends Builder{
	
	private Path searchPath;
	private Boolean digitDelimiter;
	private Pattern pattern;
	private ArrayList<String> tempList;
	private MultiThreadedHashMap multiHashMap;

	
	public QueriesBuilder(MultiThreadedInvertedIndex index, String searchPath, Boolean digitDelimiter, int numberOfThreads){
		super(digitDelimiter, numberOfThreads);
		this.searchPath = FileSystems.getDefault().getPath(searchPath);
		this.digitDelimiter = digitDelimiter;
		this.pattern = getPattern(this.digitDelimiter);
		this.tempList = new ArrayList<String>();
		this.multiHashMap = new MultiThreadedHashMap();
	}
	
	public QueriesBuilder(MultiThreadedInvertedIndex index, Boolean digitDelimiter){
		super(digitDelimiter, 1);
		this.digitDelimiter = digitDelimiter;
		this.pattern = getPattern(this.digitDelimiter);
		this.tempList = new ArrayList<String>();
		this.multiHashMap = new MultiThreadedHashMap();
	}
	
	
	/**
	 * Returns the Pattern uses super to get Pattern from Builder
	 * @param digiDelimeter
	 */
	public Pattern getPattern(Boolean digiDelimeter){
		Pattern pattern = super.getPattern();
		return pattern;
	}
	
	
	/**
	 * Users super to call shutdown on the workQueue
	 * @param - workQueue
	 */
	public void shutdownWorkQueue(){
		super.shutdownWorkQueue();
	}
	
	
	/**
	 * Reads in the searchPath and reads the queries and adds it to the arrayList tempList 
	 * to keep track of the ordering of the queries. Then uses workQueue for each of the queries
	 * found to get the DocumentResultList of every query and adds to the multiHashMap.
	 */
	public void scanQuery(MultiThreadedInvertedIndex index){
		try(Scanner fileScanner = new Scanner(searchPath.toFile()).useDelimiter(pattern)){
			while (fileScanner.hasNextLine()){
				String nextToken = fileScanner.nextLine().toLowerCase();
				tempList.add(nextToken);
				workQueue.execute(new Searcher(multiHashMap, nextToken, index));
			}
		}
		catch (IOException e){
			System.out.println("There was an error:" + e);			
		}
		
	}
	
	
	public void scanQuery(MultiThreadedInvertedIndex index, String query){
		tempList.add(query);
		workQueue.execute(new Searcher(multiHashMap, query, index));
	}
	
	
	
	/**
	 * Writes to the searchOutputPath. Goes through the tempList array to keep the order
	 * of the queries and for every String, looks it up in the multiHashMap and writes
	 * to the searchOutputPath.
	 * @param - searchOutputPath : file to write to
	 */
	public void writeToFile(String searchOutputPath){
		try(BufferedWriter writeBuffer = new BufferedWriter
				(new FileWriter(Paths.get(searchOutputPath).toFile().getAbsolutePath()))){
			for (String temp : tempList){
				writeBuffer.write(multiHashMap.get(temp));
			}
		} catch (IOException ie) {
			System.out.println("There is an error:" + ie);
		}	
	}
	
	
	public ArrayList<String> getURLList(String temp){
		ArrayList<String> tempArray = new ArrayList<String>();
		return multiHashMap.getResults(temp);
	}
	
	

	
}
