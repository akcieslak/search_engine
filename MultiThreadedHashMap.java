import java.util.ArrayList;
import java.util.HashMap;


public class MultiThreadedHashMap {

	private HashMap<String, DocumentResultList> hashMap;
	private ReadWriteLock lock;
	
	
	//Creates a ThreadSafe HashMap that uses ReadWriteLock
	public MultiThreadedHashMap(){
		this.hashMap = new HashMap<String, DocumentResultList>();
		this.lock = new ReadWriteLock();
	}
	
	
	/**
	 * Adds the query and DocumentResultLocation into the HashMap and uses a 
	 * ReadWriteLock to make sure that the threads do not try to access the
	 * data structure at the same time.
	 * @param query - the query to be made the key
	 * @param tempDRl - DocumentResultList that is associated with the query
	 */
	public void put(String query, DocumentResultList tempDRL){
		lock.lockWrite();
		hashMap.put(query, tempDRL);
		lock.unlockWrite();
	}
	
	
	/**
	 * Returns the DocumentResultList associated with the query
	 * @param - query to be fetched from the HashMap
	 * @return - DocumentResultList associated with query
	 */
	public String get(String query){
		lock.lockRead();
		String temp = hashMap.get(query).toString();
		lock.unlockRead();
		return temp;
	}
	
	
	public ArrayList<String> getResults(String query){
		lock.lockRead();
		ArrayList<String> temp = hashMap.get(query).toArray();
		lock.unlockRead();
		return temp;
	}
	
	
	
	
}
