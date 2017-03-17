import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Class that builds the InvertedIndex.
 * 
 * @author srollins
 *
 */
public class InvertedIndexBuilder extends Builder{

	/**
	
	It is up to you to design this class. 
	This class will need to recursively traverse an input directory and any time it finds a file
	that has the .txt extension (case insensitive!), it will process the file and add all words to 
	the InvertedIndex.
	
	**/
	
	private Path inputPath;
	private Boolean digitDelimiter;
	private Pattern pattern;
	private MultiThreadedInvertedIndex multiInvertedIndex;

	
	public InvertedIndexBuilder(String inputPath, Boolean digitDelimiter, int numberOfThreads){
		super(digitDelimiter, numberOfThreads);
		this.inputPath = FileSystems.getDefault().getPath(inputPath);
		this.digitDelimiter = digitDelimiter;
		this.pattern = super.getPattern();
		this.multiInvertedIndex = new MultiThreadedInvertedIndex();
		findTxt(this.inputPath);
		
	}

			
	/**
	 * Users super to call shutdown on the workQueue
	 * @param - workQueue
	 */
	public void shutdownWorkQueue(){
		super.shutdownWorkQueue();
	}
	

	/**
	 * Recursive function that looks at a Files and Directories to find the ending .txt/.TXT
	 * and if found, uses the workQueue to parse the file
	 * @param inputPath
	 */
	public void findTxt(Path inputPath){
		if (inputPath.toFile().isFile()){
			String fileName = inputPath.toString();
			
			if (fileName.toLowerCase().endsWith("txt")){
				workQueue.execute(new ParseFile(multiInvertedIndex, pattern, fileName, inputPath.toFile()));
			}
		}
		else{
			try {
				for (Path temp: Files.newDirectoryStream(inputPath)){
					findTxt(temp);
				}
			} catch (IOException e) {
				System.out.println("There was an error: " + e);
				
			}
					
		}
	}
		

	/**
	 * Getter function that returns the index
	 * @param fileName
	 */
	public MultiThreadedInvertedIndex getIndex(){
		return multiInvertedIndex;
	}
	
	
	public static void main(String[] args){

		
	}

	
}
