package mobilesensornetwork;

@SuppressWarnings("serial")
public class SensorNetworkSimulator implements TimerListener {
	private Boolean gui = true;
	MobileSensorNetwork sensorNetwork;
	SensorNetworkCanvas sensorNetworkCanvas;

	public SensorNetworkSimulator(MobileSensorNetwork sensorNetwork, SensorNetworkCanvas sensorNetworkCanvas) {
		this.sensorNetwork = sensorNetwork;
		this.sensorNetworkCanvas = sensorNetworkCanvas;
	}

	public SensorNetworkSimulator(MobileSensorNetwork sensorNetwork) {
		this.sensorNetwork = sensorNetwork;
		this.sensorNetworkCanvas = new SensorNetworkCanvas(sensorNetwork);
	}

	public Boolean getGui() {
		return gui;
	}

	public void setGui(Boolean gui) {
		this.gui = gui;
	}

	public void start() {
		sensorNetwork.start();
		if (getGui()) {
			Timer timer = new Timer(this, sensorNetwork.get(0).getIterateInterval() / 30);
			timer.start();
			MobileSensorNetworkGUI gui = new MobileSensorNetworkGUI(sensorNetwork);
			gui.start();
			sensorNetworkCanvas.start();
		} else {
			while (!sensorNetwork.stopFlag) {
				sensorNetwork.iterate();
				sensorNetwork.move(sensorNetwork.get(0).getIterateInterval());
			}
			SpringVFRobot robot = (SpringVFRobot) sensorNetwork.get(0);
			Boolean converged = sensorNetwork.getIterationNo() < sensorNetwork.getMaxIteration();
			System.out.println(sensorNetwork.size() + "," + sensorNetwork.get(0).getIterateInterval() + "," + robot.getSpringConstant() + "," + robot.getDampingCoefficient() + "," + sensorNetwork.getIterationNo() + "," + sensorNetwork.alwaysConnected + "," + converged + "," + sensorNetwork.getSumMovedDistance() + "," + sensorNetwork.getsumConsumedEnergy() + "," + sensorNetwork.getMaxConsumedEnergy());
		}
	}

	public void iterate() {
		sensorNetwork.iterate();
		sensorNetwork.move(sensorNetwork.get(0).getIterateInterval());
	}

	// public void run() {
	// Integer fps = 5;
	// Double speed = 10.0;
	// Double iterateInterval = sensorNetwork.get(0).getIterateInterval();
	// long oldTime;
	// long idealSleep = ((long) (1000000000 / fps / speed));
	// long newTime = System.nanoTime();
	// long sleepTime;
	// long error = 0;
	// Double movedTime = 0.0;
	// if ((fps * iterateInterval) != Math.round(fps * iterateInterval)) {
	// System.err.println("fps * iterateInterval must be integer");
	// System.exit(1);
	// }
	// while (!sensorNetwork.stopFlag) {
	// oldTime = newTime;
	//
	// if (count % (fps * iterateInterval) == 0) {
	// movedTime = 0.0;
	// sensorNetwork.iterate();
	// }
	// if (count % (fps * iterateInterval) == (fps * iterateInterval) - 1) {
	// sensorNetwork.move(iterateInterval - movedTime);
	// } else {
	// sensorNetwork.move(1.0 / fps);
	// movedTime += 1.0 / fps;
	// }
	//
	// sensorNetworkCanvas.repaint();
	//
	// newTime = System.nanoTime();
	// sleepTime = idealSleep - (newTime - oldTime) - error;
	// if (sleepTime < 0) {
	// sleepTime = 0;
	// }
	// oldTime = newTime;
	// try {
	// Thread.sleep((long) (sleepTime / 1000000));
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// newTime = System.nanoTime();
	// error = (long) (newTime - oldTime - sleepTime);
	// count++;
	// }
	// }

	// public void run() {
	// Integer fps = 1;
	// Double speed = 10.0;
	// Integer count = 0;
	// Double iterateInterval = sensorNetwork.get(0).iterateInterval;
	// long oldTime;
	// long idealSleep = ((long) (1000000000 / fps / speed));
	// long newTime = System.nanoTime();
	// long sleepTime;
	// long error = 0;
	// Double movedTime = 0.0;
	// if ((fps * iterateInterval) != Math.round(fps * iterateInterval)) {
	// System.err.println("fps * iterateInterval must be integer");
	// System.exit(1);
	// }
	// while (!sensorNetwork.stopFlag) {
	// oldTime = newTime;
	//
	// if (count % (fps * iterateInterval) == 0) {
	// movedTime = 0.0;
	// sensorNetwork.iterate();
	// }
	// if (count % (fps * iterateInterval) == (fps * iterateInterval) - 1) {
	// sensorNetwork.move(iterateInterval - movedTime);
	// } else {
	// sensorNetwork.move(1.0 / fps);
	// movedTime += 1.0 / fps;
	// }
	//
	// sensorNetworkCanvas.repaint();
	//
	// newTime = System.nanoTime();
	// sleepTime = idealSleep - (newTime - oldTime) - error;
	// if (sleepTime < 0) {
	// sleepTime = 0;
	// }
	// oldTime = newTime;
	// try {
	// Thread.sleep((long) (sleepTime / 1000000));
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// newTime = System.nanoTime();
	// error = (long) (newTime - oldTime - sleepTime);
	// count++;
	// }
	// }
}