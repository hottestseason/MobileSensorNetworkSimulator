package geom;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

public class CircleTest {
	@Test
	public void testGetPoints() {
		HashSet<Point2D> points = new Circle(new Point2D(4, 4), 2.0).getPoints(5);
		assertTrue(points.contains(new Point2D(5.0, 5.0)));
	}
}
