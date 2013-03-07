package sensornetwork;

import geom.Point2D;
import network.Message;

public class EventData extends Message {
	static Integer bitPerPoint = 1 * 1000000 * 8;
	private Integer iterationNo;
	private Point2D point;

	public EventData(SensorNode from, SensorNode to, Integer iterationNo, Point2D point) {
		super(from, to, bitPerPoint);
		this.iterationNo = iterationNo;
		this.point = point.clone();
	}

	public Integer getIterationNo() {
		return iterationNo;
	}

	public Point2D getPoint() {
		return point;
	}
}
