package mobilesensornetwork;

public interface MobileSensorNetworkListener {
	public void iterated(Integer iteraionNo);

	public void moved(Double sumMovedDistance);

	public void connectivityChanaged(Boolean connectivity);
}
