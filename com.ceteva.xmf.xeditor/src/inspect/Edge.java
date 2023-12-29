package inspect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;

public class Edge {

	private Inspector source, target;
	private String name;
	private Vector<Waypoint> waypoints = new Vector<Waypoint>();
	private boolean hidden = false;

	public Edge(String name, Inspector source, Inspector target) {
		super();
		this.source = source;
		this.target = target;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Edge [name=" + name + "]";
	}

	public String getName() {
		return name;
	}

	public Inspector getSource() {
		return source;
	}

	public Inspector getTarget() {
		return target;
	}

	public void paintAsLine(Graphics2D g2) {
		Point p = new Point(source.getX(), source.getY());
		g2.drawLine((int) p.getX(), (int) p.getY(), target.getX(), target.getY());
	}

	public void paint(Graphics2D g2) {
		if (!hidden) {
			paintArrow(g2);
			paintName(g2);
			if (waypoints.size() == 0)
				paintAsLine(g2);
			else {
				paintPerpendicular(source, waypoints.get(0), g2);
				paintSegments(g2);
				paintPerpendicular(target, waypoints.get(waypoints.size() - 1), g2);
			}
		}
	}

	private void paintArrow(Graphics2D g2) {
		Vector<Point> points = getPoints();
		Point end = points.lastElement();
		Point prev = points.get(points.size() - 2);
		editor.EditorTextArea.drawArrow(g2, end.x, end.y, prev.x, prev.y, false, 20.0, 10.0, Color.black, Color.black);
	}

	private void paintName(Graphics2D g2) {
		Point end = endPoint();
		int width = g2.getFontMetrics().stringWidth(name);
		int height = g2.getFontMetrics().getHeight();
		if (target.getX() >= end.getX())
			g2.drawString(name, (int) end.getX() - width, (int) end.getY() - height);
		
		else if (target.getX() + target.getWidth() <= end.getX() )
			g2.drawString(name, (int) end.getX(), (int) end.getY() - height);
		
		else if(target.getY() >= end.getY()) 
			g2.drawString(name, (int) end.getX(), (int) end.getY() - height);
		
		else if(target.getY() + target.getHeight() <= end.getY()) 
			g2.drawString(name, (int) end.getX(), (int) end.getY() + height);
	}

	private void paintSegments(Graphics2D g2) {
		for (int i = 0; i < waypoints.size() - 1; i++) {
			g2.drawLine((int) waypoints.get(i).getX(), (int) waypoints.get(i).getY(), (int) waypoints.get(i + 1).getX(),
					(int) waypoints.get(i + 1).getY());
			g2.fillOval((int) waypoints.get(i + 1).getX() - 5, (int) waypoints.get(i + 1).getY() - 5, 10, 10);
		}
	}

	private void paintPerpendicular(Inspector inspector, Point p, Graphics2D g2) {
		Point ip = getInspectorPoint(inspector, p);
		g2.drawLine((int) p.getX(), (int) p.getY(), (int) ip.getX(), (int) ip.getY());
		g2.fillOval((int) p.getX() - 5, (int) p.getY() - 5, 10, 10);
	}

	private Point getInspectorPoint(Inspector inspector, Point p) {
		double px = p.getX();
		double py = p.getY();
		double ix = inspector.getX();
		double iy = inspector.getY();
		int width = inspector.getWidth();
		int height = inspector.getHeight();
		// North...
		if (py < iy) {
			if (px >= ix && px <= ix + width) {
				return new Point((int) px, (int) iy);
			} else
				return new Point((int) ix + (width / 2), (int) iy);
		} else {
			// South
			if (py > iy + height) {
				if (px >= ix && px <= ix + width) {
					return new Point((int) px, (int) iy + height);
				} else
					return new Point((int) ix + (width / 2), (int) iy + height);
			} else {
				if (px < ix) {
					// West
					if (py >= iy && py <= iy + height) {
						return new Point((int) ix, (int) py);
					} else
						return new Point((int) ix, (int) py + (height / 2));
				} else {
					// East
					if (py >= iy && py <= iy + height) {
						return new Point((int) ix + width, (int) py);
					} else
						return new Point((int) ix + width, (int) py + (height / 2));
				}
			}
		}

	}

	public double distanceTo(Point point) {
		int index = closestLineSegment(point);
		Point p1 = getPoints().get(index);
		Point p2 = getPoints().get(index + 1);
		Point p = closest(p1, p2, point);
		double distance = distance(p, point.x, point.y);
		return distance;
	}

	private Vector<Point> getPoints() {
		Vector<Point> points = new Vector<Point>();
		points.add(startPoint());
		points.addAll(waypoints);
		points.add(endPoint());
		return points;
	}

	private Point endPoint() {
		if (waypoints.size() == 0) {
			return target.getLocation();
		} else
			return getInspectorPoint(target, waypoints.get(waypoints.size() - 1));
	}

	private Point startPoint() {
		if (waypoints.size() == 0) {
			return source.getLocation();
		} else
			return getInspectorPoint(source, waypoints.get(0));
	}

	private int closestLineSegment(Point point) {
		double min = Double.MAX_VALUE;
		int closest = 0;
		Vector<Point> points = getPoints();
		for (int i = 0; i < points.size() - 1; i++) {
			Point closestPoint = closest(points.get(i), points.get(i + 1), point);
			if (distance(closestPoint, point.x, point.y) < min) {
				closest = i;
				min = distance(closestPoint, point.x, point.y);
			}
		}
		return closest;
	}

	private static double getSlope(Point pt1, Point pt2) {
		double deltaY = pt2.y - pt1.y;
		double deltaX = pt2.x - pt1.x;

		return deltaY / deltaX;
	}

	private static Point closest(Point pt1, Point pt2, Point p) {
		double u = ((p.x - pt1.x) * (pt2.x - pt1.x) + (p.y - pt1.y) * (pt2.y - pt1.y))
				/ (sqr(pt2.x - pt1.x) + sqr(pt2.y - pt1.y));

		// if (u > 1.0)
		// return (Point)pt2.clone();
		// else if (u <= 0.0)
		// return (Point)pt1.clone();
		// else
		// {
		double x = (pt2.x * u + pt1.x * (1.0 - u));
		double y = (pt2.y * u + pt1.y * (1.0 - u));
		double m = getSlope(pt1, pt2);
		double yQuick = m * (x - pt1.x) + pt1.y;

		return new Point((int) (x + 0.5), (int) (y + 0.5));
		// }
	}

	private static double sqr(double x) {
		return x * x;
	}

	private static double distance(Point from, double x, double y) {
		double deltaX = from.x - x;
		double deltaY = from.y - y;

		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

	public Waypoint addWaypoint(Point p) {
		int index = closestLineSegment(p);
		Point wp = closest(getPoints().get(index), getPoints().get(index + 1), p);
		Waypoint w = new Waypoint((int) wp.getX(), (int) wp.getY());
		waypoints.insertElementAt(w, index);
		return w;
	}

	public Waypoint getWaypoint(Point p) {
		for (Waypoint w : waypoints) {
			if (p.distance(w) < 5)
				return w;
		}
		return null;
	}

	public void checkWaypoints() {
		Vector<Point> points = getPoints();
		for (int i = 1; i < points.size() - 1; i++) {
			Point p = closest(points.get(i - 1), points.get(i + 1), points.get(i));
			if (distance(p, points.get(i).getX(), points.get(i).getY()) < 5 && points.size() > 3) {
				waypoints.removeElementAt(i - 1);
				break;
			}
		}
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

}
