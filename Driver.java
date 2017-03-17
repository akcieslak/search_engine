import java.io.File;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;



public class Driver {

	public static void main(String[] args) throws Exception {
		
		Configuration config1 = new Configuration(FileSystems.getDefault().getPath("config.json"));
		
		try  {
			config1.init();

		} catch (InitializationException ie) {
			System.out.println("There was an error:" + ie);	
			return;
		} 

		String inputPath = config1.getInputPath();
		String outputPath = config1.getOutputPath();
		Boolean digitDelimiter = config1.useDigitDelimiter();
		String searchPath = config1.getSearchPath();
		String searchOutputPath = config1.getSearchOutputPath();
		int numberOfThreads = config1.getNumberThreads();
		
		MultiThreadedInvertedIndex index;
		
		URL u = null;
		try {
			u = new URL(inputPath);
		} catch (MalformedURLException me){
			System.out.println("There was an error: " + me);
		}
		
		if (u != null){
			WebCrawler webCrawler = new WebCrawler(u, digitDelimiter, numberOfThreads);
			webCrawler.parseLinks(webCrawler, u);
			webCrawler.shutdownWorkQueue();
			index = webCrawler.getIndex();		

			
			
		} else {
			InvertedIndexBuilder indexBuilder = new InvertedIndexBuilder(inputPath, digitDelimiter, numberOfThreads);
			index = indexBuilder.getIndex();
			indexBuilder.shutdownWorkQueue();
		}
		
		
		if (searchOutputPath == null){
			searchOutputPath = "results/default.txt";
		}
		
			
		
		if (searchPath != null){
			QueriesBuilder queriesBuilder = new QueriesBuilder(index, searchPath, digitDelimiter, numberOfThreads);
			queriesBuilder.scanQuery(index);
			queriesBuilder.shutdownWorkQueue();
			queriesBuilder.writeToFile(searchOutputPath);
		}
		
		if (outputPath != null){
			index.printToFile(Paths.get(outputPath));
		}

	}
}
