package com.github.adaviding.numerics.f2;

import com.github.adaviding.numerics.F;

/**
 * Represents a coordinate in a 2D cartesian space.
 */
public class Point
{
	/**
	 * The horizontal ordinate.
	 */
	public float x;
	/**
	 * The vertical ordinate.
	 */
	public float y;
	public Point(){}
	public Point(float x, float y) { this.x = x; this.y = y; }
	/**
	 * Calculates a string representation of the instance as a pair of comma separated values.
	 * @return a string representation of the instance.
	 */
	public String toString() { return Float.toString(this.x) + "," + Float.toString(this.y); }
	/**
	 * Adds the values of the given point to the values of this instance.
	 * @param p The addend.
	 */
	public void add(Point p)
	{
		this.x += p.x;
		this.y += p.y;
	}
	/**
	 * Calculates the Euclidian distance between two points in standard 2D Cartesian space.
	 * @param a Point a
	 * @param b Point b
	 * @return The Euclidian distance.
	 */
	public static float distance(Point a, Point b)
	{
		double dx = b.x -a.x;
		double dy = b.y -a.y;
		return (float)Math.sqrt(dx*dx+dy*dy);
	}
	/**
	 * Determine if this point has values that are exactly equal to the comparator.
	 * @param other  The comparator.
	 * @return  True if points are equal, false otherwise.
	 */
	public boolean equals(Point other)
	{
		return this.x ==other.x && this.y ==other.y;
	}
	/**
	 * Determines if the points are equal within a specified tolerance.
	 * @param other  The comparator.
	 * @param tol  The tolerance for equality of floating point numbers.
	 * @return  True if this point is equal or nearly equal to the comparator within the specified tolerance, false otherwise.
	 */
	public boolean equals(Point other, float tol) { return F.testEqual(this.x,other.x,tol) && F.testEqual(this.y,other.y,tol); }
	/**
	 * Determines whether two line segments intersect, and gets the point of intersection (if it exists).
	 * @param a0 Point 0 on segment a
	 * @param a1 Point 1 on segment a
	 * @param b0 Point 0 on segment b
	 * @param b1 Point 1 on segment b
	 * @return If the line segments intersect, returns the point of intersection; otherwise returns null.
	 */
	public static Point intersection(Point a0, Point a1, Point b0, Point b1)
	{
		if(	null==a0 || null==a1 || null==b0 || null==b1
				|| a0.isEmpty() || a1.isEmpty() || b0.isEmpty() || b1.isEmpty()
				|| a0.equals(a1) || b0.equals(b1))
			return null;

		Line aLine = Line.fromPoints(a0,a1);
		float[] bDist = new float[] {aLine.signedDistance(b0), aLine.signedDistance(b1)};
		if(F.sign(bDist[0]) != F.sign(bDist[1]))
		{
			//	We know the line segment [b0,b1] intersects the line a.  Next we compute the location.
			bDist[0] = Math.abs(bDist[0]);
			bDist[1] = Math.abs(bDist[1]);
			float dist = bDist[0]+bDist[1];
			//	This is where they intersect.
			Point x = new Point
			(
				(bDist[1]*b0.x + bDist[0]*b1.x)/dist,
				(bDist[1]*b0.y + bDist[0]*b1.y)/dist
			);
			//	Is the intersection point within the segment bounded by [a0,a1]?  If so, return x.
			Line[] aSegs = aLine.segmentLimits(a0,a1);
			if(aSegs[0].signedDistance(x)>=0.0f && aSegs[1].signedDistance(x)>=0.0f)
				return x;
		}
		return null;
	}
	/**
	 * Determines if the coordiante is empty.  It is empty if either x or y is NaN.
	 * @return True if the coordinate is empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return Float.isNaN(this.x) || Float.isNaN(this.y);
	}

	/**
	 * Creates a new point (0,0).
	 * @return  The new point.
	 */
	public static Point zero() {
		return new Point(0f,0f);
	}
}
