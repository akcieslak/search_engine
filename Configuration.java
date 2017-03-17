import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The Configuration class parses a JSON file containing configuration information and provides wrapper methods
 * to access the configuration data in the JSON object.
 * @author srollins
 *
 */
public class Configuration {

	
	/**
	 * Constants that store the keys used in the configuration file.
	 */
	public static final String INPUT_PATH = "inputPath";
	public static final String OUTPUT_PATH = "outputPath";
	public static final String DIGIT_DELIMITER = "digitDelimiter";	
	public static final String SEARCH_PATH = "searchPath";
	public static final String SEARCH_OUTPUT_PATH = "searchOutputPath";
	public static final String NUMBER_THREADS = "numberThreads";
	private Path path;

	
	/**
	 * Instance variables to store shared information.
	 * 
	 */
	private JSONObject jsonobject;

	
	/**
	 * Instantiates a Configuration object.
	 * @param path - the location of the file 			
	 */
	public Configuration(Path path) {
		this.path = path;
		this.jsonobject = null;
	}
	
	
	/**
	 * Initializes a Configuration object. Uses a JSONParser to parse the contents of the file. Hint:
	 * I used a helper method to validate the contents of the file once it was parsed. Note: you will need
	 * to implement your own exception class called InitializationException.
	 * @throws InitializationException - thrown in the following cases: (1) an IOException is generated when 
	 * 				accessing the file; (2) a ParseException is thrown when parsing the JSON contents of the file;
	 * 				(3) the file does not contain the inputPath key; (4) the file does not contain the digitDelimiter 
	 * 				key; (5) the digitDelimiter value is not a boolean.
	 */
	public void init() throws InitializationException {		
		JSONParser parser = new JSONParser();
		try(BufferedReader in = Files.newBufferedReader(path, Charset.forName("UTF-8"))){
			jsonobject = (JSONObject)parser.parse(in);
			
		} catch (IOException e){
			throw new InitializationException("Unable to open file");
			
		} catch (ParseException e) {
			throw new InitializationException("Unable to parse file");
		}
		
		checkFile();
	}
	
	
	/**
	 * Helper method to validate the contents of the file 
	 */
	public void checkFile() throws InitializationException {
		if (jsonobject.get(INPUT_PATH)==null){
			throw new InitializationException("inputPath not specified");
		} 
		
		if (jsonobject.get(DIGIT_DELIMITER)==null){
			throw new InitializationException("digitDelimiter not specified");
		}
		
		if (!(jsonobject.get(DIGIT_DELIMITER) instanceof java.lang.Boolean)){
			throw new InitializationException("digitDelimiter not a boolean");
		}
		
		Object numberThreads = jsonobject.get(NUMBER_THREADS);
		
		if (numberThreads != null) {
			if (numberThreads instanceof String){
				throw new InitializationException("numberThreads is not a valid integer value");
			}
			
			else if (!(numberThreads instanceof Long)){

				throw new InitializationException("numberThreads is not a valid integer value");
			}

			else if (Long.parseLong(numberThreads.toString()) > 1000 || (Long.parseLong(numberThreads.toString())  < 1 )){
				throw new InitializationException("numberThreads is not a valid integer value");
			}
			
		}
	
		
	}
	
	
	/**
	 * Returns the value of associated with the inputPath key in the JSON configuration file.
	 * @return - value associated with key inputPath
	 */
	public String getInputPath() {
		return (String)jsonobject.get(INPUT_PATH);
		
	}
	
	
	/**
	 * Returns the value of associated with the outputPath key in the JSON configuration file.
	 * @return - value associated with key outputPath - null if no outputPath specified
	 */
	public String getOutputPath() {
		return (String)jsonobject.get(OUTPUT_PATH);

	}

	
	/**
	 * Returns the value of associated with the digitDelimiter key in the JSON configuration file.
	 * @return - value associated with key digitDelimiter
	 */
	public boolean useDigitDelimiter() {
		return (Boolean)jsonobject.get(DIGIT_DELIMITER);
		
	}
	
	
	/**
	 * Returns the value of associated with the searchPath key in the JSON configuration file.
	 * @return - value associated with key searchPath
	 */	
	public String getSearchPath(){
		return (String)jsonobject.get(SEARCH_PATH);
	}
	
	
	/**
	 * Returns the value of associated with the searchOutputPath key in the JSON configuration file.
	 * @return - value associated with key searchOutputPath
	 */
	public String getSearchOutputPath(){
		return (String)jsonobject.get(SEARCH_OUTPUT_PATH);
	}
	
	/**
	 * Returns the value of associated with the numberThreads key in the JSON configuration file.
	 * @return - value associated with key numberThreads - returns 5 if not specified
	 */
	public int getNumberThreads(){
		if (jsonobject.get(NUMBER_THREADS) == null){
			return 5;
		}
		return Integer.parseInt(jsonobject.get(NUMBER_THREADS).toString());
	}
	
	
	/**
	 * Simple main method used for in-progress testing of Configuration class only.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		(new Configuration(FileSystems.getDefault().getPath("config.json"))).init();
		
	}

	
}
