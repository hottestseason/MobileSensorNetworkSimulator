import javax.swing.JFrame;

@SuppressWarnings("serial")
public class SensorNetworkSimulator implements Runnable {
	static Boolean gui = true;
	SensorNetwork sensorNetwork;
	SensorNetworkCanvas sensorNetworkCanvas;
	JFrame frame;
	protected Thread thread = new Thread(this);

	public SensorNetworkSimulator(SensorNetwork sensorNetwork, SensorNetworkCanvas sensorNetworkCanvas) {
		this.sensorNetwork = sensorNetwork;
		this.sensorNetworkCanvas = sensorNetworkCanvas;
		if (gui) {
			frame = new JFrame("MobileSensorNetworkSimulator");
			frame.add(sensorNetworkCanvas);
			frame.setSize(sensorNetworkCanvas.width, sensorNetworkCanvas.height + 50);
			frame.setVisible(true);
		}
	}

	public SensorNetworkSimulator(SensorNetwork sensorNetwork) {
		this(sensorNetwork, new SensorNetworkCanvas(sensorNetwork));
	}

	public void start() {
		sensorNetwork.start();
		if (gui) {
			frame = new JFrame("MobileSensorNetworkSimulator");
			frame.add(sensorNetworkCanvas);
			frame.setSize(sensorNetworkCanvas.width, sensorNetworkCanvas.height + 50);
			frame.setVisible(true);
			thread.start();
		} else {
			while (!sensorNetwork.stopFlag) {
				sensorNetwork.iterate();
				sensorNetwork.move(sensorNetwork.get(0).iterateInterval);
			}
			SpringVFRobot robot = (SpringVFRobot) sensorNetwork.get(0);
			Boolean converged = sensorNetwork.iterateNo < sensorNetwork.maxIteration;
			System.out.println(sensorNetwork.size() + " " + robot.springConstant + " " + robot.dampingCoefficient + " " + sensorNetwork.iterateNo + " " + sensorNetwork.sumMovedDistance + " " + sensorNetwork.alwaysConnected + " " + converged);
		}
	}

	public void run() {
		Integer fps = 10;
		Double speed = 20.0;
		Integer count = 0;
		Double iterateInterval = sensorNetwork.get(0).iterateInterval;
		long oldTime;
		long idealSleep = ((long) (1000000000 / fps / speed));
		long newTime = System.nanoTime();
		long sleepTime;
		long error = 0;
		Double movedTime = 0.0;
		if ((fps * iterateInterval) != Math.round(fps * iterateInterval)) {
			System.err.println("fps * iterateInterval must be integer");
			System.exit(1);
		}
		while (!sensorNetwork.stopFlag) {
			oldTime = newTime;

			if (count % (fps * iterateInterval) == 0) {
				movedTime = 0.0;
				sensorNetwork.iterate();
			}
			if (count % (fps * iterateInterval) == (fps * iterateInterval) - 1) {
				sensorNetwork.move(iterateInterval - movedTime);
			} else {
				sensorNetwork.move(1.0 / fps);
				movedTime += 1.0 / fps;
			}

			sensorNetworkCanvas.repaint();

			newTime = System.nanoTime();
			sleepTime = idealSleep - (newTime - oldTime) - error;
			if (sleepTime < 0) {
				sleepTime = 0;
			}
			oldTime = newTime;
			try {
				Thread.sleep((long) (sleepTime / 1000000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newTime = System.nanoTime();
			error = (long) (newTime - oldTime - sleepTime);
			count++;
		}
	}
}