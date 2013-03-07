package sensornetwork;

import geom.Circle;
import network.Message;

public class SensedData extends Message {
	static Integer bitPerCircle = 50 * 1000 * 8;
	private Integer iterationNo;
	private Circle sensedCircle;

	public SensedData(SensorNode from, SensorNode to, Integer iterationNo, Circle sensedCircle) {
		super(from, to, bitPerCircle);
		this.iterationNo = iterationNo;
		this.sensedCircle = sensedCircle.clone();
	}

	public Integer getIterationNo() {
		return iterationNo;
	}

	public Circle getSensedCircle() {
		return sensedCircle;
	}
}
