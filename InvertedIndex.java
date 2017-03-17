import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Maintains a mapping from a word to a list of documents and positions in those documents where the word was found.
 * See the project description for more information: https://github.com/CS212-S15/projects/blob/master/specifications/project1.md
 * @author srollins
 *
 */
public class InvertedIndex {

	/**
	 * It is up to you to decide what the data members for this class will look like, but
	 * make sure to think about efficiency!
	 * 
	 * Hint, think about how to use objects of type DocumentLocationMap.
	 */

	/**
	 * Constructor to instantiate a new InvertedIndex
	 */
	
	private TreeMap<String, DocumentLocationMap> invertedIndex;	
	private int count;
	private TreeMap<String, Integer> docCount;

	
	public InvertedIndex() {
		this.invertedIndex = new TreeMap<String, DocumentLocationMap>();
		this.docCount = new TreeMap<String, Integer>();
	}
	
	
	/**
	 * Adds a new word to the index. If the word is already in the index, the method simply adds a new document/position.
	 * 
	 * @param word - the word to be added
	 * @param fileName - the name of the document where the word is found
	 * @param location - the position in the document where the word is found.
	 */
	public void add(String word, String fileName, int location) {
		if (!this.invertedIndex.containsKey(word)){
			DocumentLocationMap info = new DocumentLocationMap(word);
			info.addLocation(fileName, location);
			this.invertedIndex.put(word, info);
		}
		else{//if it already exists
			this.invertedIndex.get(word).addLocation(fileName, location);
		}
		docCount.put(fileName, location);
		
	}

	
	/**
	 * Returns a string representation of the index. 
	 * See the project specification for the required format of the String representation
	 * of the index. Your output must match exactly to pass all tests.
	 */
	public String toString() {	
		StringBuffer invertedIndex = new StringBuffer();
		for(String word: this.invertedIndex.keySet()){
			invertedIndex.append(word + "\n");
			invertedIndex.append(this.invertedIndex.get(word).toString());
			invertedIndex.append("\n");

		}
		
		return invertedIndex.toString();
	}	
	
	
	/**
	 * Optional method. I used this method to save the string representation of the index to a file.
	 * @param fileName
	 */
	public void printToFile(Path fileName) {
		try (BufferedWriter writeBuffer = new BufferedWriter(new FileWriter(fileName.toFile().getAbsolutePath()))){
			for(String word: invertedIndex.keySet()){
				writeBuffer.write(word + "\n");
				writeBuffer.write(this.invertedIndex.get(word).toString());
				writeBuffer.write("\n");
			}
		} catch (IOException ie){
			System.out.println("There is an error:" + ie);			
		}
		
	}
	
	/**
	 * Returns the value associated with the FileName
	 * @return - value associated with key FileName
	 */
	public int getDocCount(String fileName){
		return docCount.get(fileName);
	}
	
	
	/** Takes in a query and searches the index to find the TFIDF with the help
	 * of the method calculateTFIDF.
	 * @param - String query : query to search
	 * @return - DocumentResultList, ordered from documents
	 * with the greatest score to documents with the smallest
	 */
	public DocumentResultList search(String query) {
		TreeMap<String, Double> treeMapResult = new TreeMap<String, Double>();	
		DocumentResultList documentResultList = new DocumentResultList(query);
		String[] queryArray = query.split("[^a-zA-Z0-9]+");	
		int size = docCount.size();
		
		for (String wordQuery : queryArray) {			
			if (invertedIndex.get(wordQuery) != null){
				int numOfDoc = invertedIndex.get(wordQuery).getCopyKeySet().size();

				
				for (String doc : invertedIndex.get(wordQuery).getCopyKeySet()){
					double score = 0;
					int numOfWords = getDocCount(doc);
					int wordCount = invertedIndex.get(wordQuery).getCount(doc);
					score = calculateTFIDF(wordCount, numOfDoc, numOfWords, size);
					
					if (!treeMapResult.containsKey(doc)){
						treeMapResult.put(doc, score);
					} else{
						treeMapResult.put(doc, treeMapResult.get(doc) + score);
					}
				}
		
			}
		}

		for (String doc : treeMapResult.keySet()){
			DocumentResult temp = new DocumentResult(doc, treeMapResult.get(doc));
			documentResultList.add(temp);
			
		}
		return documentResultList;
	}
	
	
	/**
	 * Calculates the TFIDF Score
	 * @param - wordCount: number of that certain word in a doc
	 * @param - numOfDocs: number of docs that contain the word
	 * @param - numOfWords: number of all words in a single doc
	 * @param - size: number of all documents
	 * @return - the TFIDF score associated with param
	 */
	public double calculateTFIDF(double wordCount, double numOfDocs, double numOfWords, int size){
		double score = 0;
		if (wordCount != 0 && numOfDocs != 0){
			double tf = (wordCount/numOfWords);
			double idf = Math.log10(size / numOfDocs);
			score = (tf * idf);
		}
		return score;
	}
	
		
	
	

	public static void main(String[] args) {	
		InvertedIndex ii = new InvertedIndex();
		ii.add("hello", "test1", 1);
		ii.add("hello", "test1", 10);
		ii.add("animal", "test1", 4);
		ii.add("hello", "test2", 8);
		ii.add("animal", "test2", 1);
		ii.add("hello", "test2", 4);
		ii.add("bob", "test3", 4);
		System.out.println(ii);
		ii.printToFile(Paths.get("results/result.txt"));
	}

	
}
