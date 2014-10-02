import java.util.*;
import java.util.concurrent.locks.*;

public class DelayedScheduler {

	// private class to maintain the client-provided task info
	private class Task implements Comparable<Task> {
		private Runnable task;
		private long timeToRun;
		
		public Task(Runnable task, long timeToRun) {
			this.task = task;
			this.timeToRun = timeToRun;
		}
		
		// Comparator needed for sorting the Tasks in the PriorityQueue
		public int compareTo(Task task) {			
			return (int)(this.timeToRun - task.timeToRun);
		}		
	}

	// book-keeping items
	private PriorityQueue<Task> taskList;
	private Thread queueProcessor;
	private Lock lock; 
	
	public DelayedScheduler() {
		lock = new ReentrantLock();
		taskList = new PriorityQueue<Task>();
		queueProcessor = new Thread(new QueueProcessor());
		queueProcessor.start();
	}
	
	public void scehduleTask(Runnable taskToRun, long timeToRun) {		
		// synchronize modifications to the shared queue
		lock.lock();
		taskList.add(new Task(taskToRun, timeToRun));
		lock.unlock();
		
		// Poke the queue processor
		queueProcessor.interrupt();
	}
			
	private class QueueProcessor implements Runnable {
		
		// This is the thread that processes the queue of scheduled tasks and runs any tasks that are ready to be run.
		public void run() {
			
			while (true) {
				
				try {
					long timeToSleep = 1000;
					
					lock.lock();
					Task task = taskList.peek();
					while (task != null) {
						long timeDiff = task.timeToRun - System.currentTimeMillis();
						if (timeDiff <= 0) {
							task = taskList.poll();
							task.task.run();
							
							// Look for any other tasks that might be ready to run
							task = taskList.peek();
						} else {
							// No task to run. Go take a nap.
							timeToSleep = timeDiff;
							break;
						}
					}
					
					lock.unlock();
					Thread.sleep(timeToSleep);
				} catch (InterruptedException e) {
					System.out.println("InterruptedException");
				} catch (Exception e1) {
					System.out.println("Exception");
				} finally {
				}
			}
		}		
	}
}
