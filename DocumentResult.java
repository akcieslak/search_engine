
public class DocumentResult implements Comparable<DocumentResult> {
	/**
	 * DocumentResult Class used to keep each document
	 * and the document's score in an object. 
	 */
	
	private String doc;
	private double score;
	
	public DocumentResult(String doc, Double score){
		this.doc = doc;
		this.score = score;
	}

	
	/**
	 * Returns the doc associated with the DocumentResult.
	 * @return - the doc associated with the DocumentResult.
	 */
	public String getDoc() {
		return doc;
	}

	
	/**
	 * Returns the score associated with the DocumentResult.
	 * @return - the score associated with the DocumentResult.
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Compares the score of the DocumentResult passed in. 
	 * @return - -1 or 1
	 */
	@Override
	public int compareTo(DocumentResult o1) {
		if (o1.score == this.score){
			if (o1.doc.compareTo(this.doc) < 0){
				return 1;
			}
			return -1;
		}
		if(o1.score < this.score){
			return 1;
		} 

		else {
			return -1;
		}
	}
}
