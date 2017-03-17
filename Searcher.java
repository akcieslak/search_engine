

public class Searcher implements Runnable{

	private MultiThreadedInvertedIndex multiIndex;
	private String nextToken;
	private MultiThreadedHashMap multiHashMap;

	
	
	public Searcher(MultiThreadedHashMap multiHashMap, String nextToken, MultiThreadedInvertedIndex index){
		this.multiIndex = index;
		this.nextToken = nextToken;
		this.multiHashMap = multiHashMap;
	}
	
	
	@Override
	public void run() {
		DocumentResultList documentResultList = multiIndex.search(nextToken);
		multiHashMap.put(nextToken, documentResultList);
	}
		

}
