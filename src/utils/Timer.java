package utils;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class Timer implements Runnable {
	private TimerListener timerListener;
	private Double iterationInterval;
	private Thread thread;

	public Timer(TimerListener timerListener, Double iterationInterval) {
		this.timerListener = timerListener;
		this.iterationInterval = iterationInterval;
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread = null;
	}

	public Boolean isRunning() {
		return thread != null;
	}

	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		long idealSleepTime = (long) (1000000000L * iterationInterval);

		beforeTime = System.nanoTime();

		while (isRunning()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						timerListener.update();
					}
				});
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			System.out.print(timeDiff / 1000L + "us ");
			sleepTime = idealSleepTime - timeDiff - overSleepTime;
			System.out.println(sleepTime / 1000L + "us");
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime / 1000000L); // nano->ms
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else {
				overSleepTime = 0;
			}

			beforeTime = System.nanoTime();
		}
	}
}
