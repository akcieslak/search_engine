import java.nio.file.Path;
import java.util.ArrayList;

public class MultiThreadedInvertedIndex extends InvertedIndex{
	
	private ReadWriteLock lock;

	public MultiThreadedInvertedIndex() {
		super();
		this.lock = new ReadWriteLock();
	}
	
	
	/**
	 * Uses the lockWrite to ensure thread safety and uses super to call add in InvertedIndex
	 * @param word - the word to be added
	 * @param fileName - the name of the document where the word is found
	 * @param location - the position in the document where the word is found.
	 */
	public void add(String word, String fileName, int location) {	
		lock.lockWrite();
		super.add(word, fileName, location);
		lock.unlockWrite();
	}
	
	
	/**
	 * Uses the lockWrite to ensure thread safety and uses super to call add in InvertedIndex
	 * Goes through the ArrayList and uses super
	 * @param ArrayList<FileInfo> - a list of FileInfos 
	 */
	public void addAll(ArrayList<FileInfo> tempList){
		lock.lockWrite();
		for (FileInfo info : tempList){
			super.add(info.getNextToken(), info.getFileName(), info.getLocation());
		}
		lock.unlockWrite();
	}
	
	
	/**
	 * Uses super to call toString to the InvertedIndex
	 * @return return - String 
	 */
	public String toString() {
		lock.lockRead();
		String temp = super.toString();
		lock.unlockRead();
		return temp;
	}
	
	
	/**
	 * Uses super to call printFile to the InvertedIndex
	 * @param fileName - filename to print to
	 */
	public void printToFile(Path fileName) {
		lock.lockWrite();
		super.printToFile(fileName);
		lock.unlockWrite();
	}
	
	
	/**
	 * Uses the lockWrite to ensure thread safety and uses super to call getDocCount in InvertedIndex
	 * @param fileName - the name of the document where the word is found
	 * @return int - int of doc counts associated with the fileName
	 */
	public int getDocCount(String fileName){
		lock.lockRead();
		int temp = super.getDocCount(fileName);
		lock.unlockRead();
		return temp;
	}
	
	
	/**
	 * Uses the lockWrite to ensure thread safety and uses super to call add in InvertedIndex
	 * @param String - which query to search for in the inverted index
	 * @return DocumentResultList that is associated with the query
	 */
	public DocumentResultList search(String query) {
		lock.lockRead();
		DocumentResultList temp = super.search(query);
		lock.unlockRead();
		return temp;
	}
	
	
	/**
	 * Uses super to call InvertedIndex to calculate the TFIF score
	 * @param - wordCount: number of that certain word in a doc
	 * @param - numOfDocs: number of docs that contain the word
	 * @param - numOfWords: number of all words in a single doc
	 * @param - size: number of all documents
	 * @return - the TFIDF score associated with param
	 */
	public double calculateTFIDF(double wordCount, double numOfDocs, double numOfWords, int size){
		lock.lockRead();
		double temp = super.calculateTFIDF(wordCount, numOfDocs, numOfWords, size);
		lock.unlockRead();
		return temp;
	}
	

	
}
