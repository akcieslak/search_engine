
public class FileInfo {
	
	private String nextToken;
	private String fileName;
	private int location;
	
	public FileInfo(String nextToken, String fileName, int location){
		this.nextToken = nextToken;
		this.fileName = fileName;
		this.location = location;
	}
	
	
	 /**
     * @return - nextToken
     */
	public String getNextToken(){
		return nextToken;
	}
	
	  /**
     * @return - fileName 
     */
	public String getFileName(){
		return fileName;
	}
	
	
	  /**
     * @return - location
     */
	public int getLocation(){
		return location;
	}
	
}
