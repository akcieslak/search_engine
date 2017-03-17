import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class ParseFile implements Runnable{

	private MultiThreadedInvertedIndex multiInvertedIndex; 
	private Pattern pattern;
	private String fileName;
	private File file;
	private ArrayList<FileInfo> tempList;
	
	
	public ParseFile(MultiThreadedInvertedIndex invertedIndex, Pattern pattern, String fileName, File file){
		this.multiInvertedIndex = invertedIndex;
		this.pattern = pattern;
		this.fileName = fileName;
		this.file = file;
		this.tempList = new ArrayList<FileInfo>();
	}
	
	
	@Override
	public void run() {
		scanFile(this.pattern, this.fileName, this.file);
		multiInvertedIndex.addAll(tempList);
	}
	
	
	/**
	 * Reads in the file and for every words adds to the multi threaded inverted index
	 * @param pattern - pattern to specify for the delimiter
	 * @param fileName - (String)the name of the document you are reading 
	 * @param file - the file that you are reading
	 */
	public void scanFile(Pattern pattern, String fileName, File file){
		int location = 1;
		try(Scanner fileScanner = new Scanner(file).useDelimiter(pattern)){
			while (fileScanner.hasNext()){
				String nextToken = fileScanner.next().toLowerCase();
				tempList.add(new FileInfo(nextToken, fileName, location));
				location++;
			}
		}
		catch (IOException e){
			System.out.println("There was an error:" + e);			
		}
	}

}
