import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class WorkQueue {

    private final int nThreads;
    private final PoolWorker[] threads;
    private final LinkedList queue;
	private static final Logger logger = Logger.getLogger(WorkQueue.class.getName());
    
    private volatile boolean takingWork = true;

    public WorkQueue(int nThreads) {
    
        this.nThreads = nThreads;
        queue = new LinkedList();
        threads = new PoolWorker[nThreads];

        for (int i=0; i<nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
        
  
           
    }
    
    
    public void shutdown(){
    	takingWork = false;
		synchronized(queue) {
			queue.notifyAll();
		}
    	
    }
    
    
    public void awaitTermination() {
    	for (PoolWorker t : threads) {
    		try {
    			t.join();
    		} catch (Exception e) {
    			System.out.println("There was an error:" + e);			
    		}
    	}
    }
    
    
    public void execute(Runnable r) {
    	if (takingWork) {
    		synchronized(queue) {
    			queue.addLast(r);
    			queue.notifyAll();
    		}
    	}
    }
    

    private class PoolWorker extends Thread {
        public void run() {
            Runnable r;

            while (true) {
                synchronized(queue) {
                    while (queue.isEmpty() && takingWork) {
                        try {
                        
                            queue.wait();
                        }
                        catch (InterruptedException ignored) {
                        
                        }
                    }

                    if (!takingWork && queue.isEmpty()){
                    	break;
                    }
                    
                    r = (Runnable) queue.removeFirst();

                }

                // If we don't catch RuntimeException, 
                // the pool could leak threads
                try {
                    r.run();
                }
                catch (RuntimeException e) {
        			System.out.println("There was an error:" + e);			
                }

            }
        }
    }
}
