package mobilesensornetwork;

public class Timer implements Runnable {
	private TimerListener timerListener;
	private Double iterationInterval;
	private Boolean running;
	private Thread thread;

	public Timer(TimerListener timerListener, Double iterationInterval) {
		this.timerListener = timerListener;
		this.iterationInterval = iterationInterval;
	}

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		long idealSleepTime = (long) (1000000000L * iterationInterval);

		beforeTime = System.nanoTime();

		while (running) {
			timerListener.iterate();

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = idealSleepTime - timeDiff - overSleepTime;

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
