import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class DelayedSchedulerTest {

	@Test
	public final void test() {
		
		long currTime = System.currentTimeMillis();
		
		long testSet[] = {
				currTime + 11*1000,
				currTime + 8*1000,	
				currTime + 9*1000,	
				currTime + 14*1000,	
				currTime + 1*1000,					
				currTime + 6*1000,
				currTime + 8*1000,	
				currTime + 20*1000,	
				currTime + 17*1000,	
				currTime + 2*1000,					
		};
		
		DelayedScheduler scheduler = new DelayedScheduler();
		TestTask testTask;
		int taskId = 1;
		long maxDelay = -1;
		
		try {
			for (long test : testSet) {
				testTask = new TestTask(taskId++, test);	
				scheduler.scehduleTask(testTask, test);
				if (test > maxDelay)
					maxDelay = test;
			}
			
			// Allow enough time for all the delayed tasks to complete
			Thread.sleep (maxDelay + 1000);
		} catch (Exception e) {
		}
	}

	
	private class TestTask implements Runnable {
		
		int taskId;
		Date dateToRun;
		
		public TestTask(int taskId, long timeToRun) {
			this.taskId = taskId;
			dateToRun = new Date(timeToRun);
		}
		
		public void run() {
			Date currTime = new Date(System.currentTimeMillis());
			System.out.println("Task #" + taskId + " ran at " + currTime + "  Was scheduled for " + dateToRun);
		}
	}
}
