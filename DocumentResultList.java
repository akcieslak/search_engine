import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.text.html.HTMLDocument.Iterator;


public class DocumentResultList  {
	
	/**
	 * Document Result List is a TreeSet of DocumentResults ordered from
	 * DocumentResults with the highest score, to document results with
	 * the lowest. 
	 */
	
	private TreeSet<DocumentResult> treeSet;
	private String query;

	
	public DocumentResultList(String query){
		this.treeSet = new TreeSet<DocumentResult>();
		this.query = query;

	}


	/**
	 * Adds the DocumentResult object to the TreeSet
	 * @param - DocumentResult object
	 */
	public void add(DocumentResult o1) {
		this.treeSet.add(o1);
	}
	
	
	/**
	 * Helper toString for printing to a file
	 */
	public String toString() {
		StringBuffer result = new StringBuffer(query + "\n");
				
		java.util.Iterator<DocumentResult> iterator = treeSet.descendingIterator();
		while (iterator.hasNext()){
			result.append(iterator.next().getDoc());
			result.append("\n");
		}
		
		result.append("\n");

		return result.toString();
	}
	
	
	public ArrayList<String> toArray() {
		ArrayList<String> tempArray = new ArrayList<String>();
		java.util.Iterator<DocumentResult> iterator = treeSet.descendingIterator();
		while (iterator.hasNext()){
			tempArray.add(iterator.next().getDoc());
		}
		
		return tempArray;
		
	}
	
	

}
