package com.github.adaviding.numerics.f2;

/**
 * This class represents the equation for a Line in 2D space.  The line is defined by 3 coefficients (a,bx,by) and
 *
 * 2 coordinates (x,y) as
 * 		0 = a + bx * x + by * y
 * subject to the constraint
 * 		1 = bx * bx + by * by
 */
public class Line
{
	/**
	 * The additive coefficient.
	 */
	public float a =0.0f;
	/**
	 * The coefficient that scales the x-ordinate.
	 */
	public float bx =1.0f;
	/**
	 * The coefficient that scales the y-ordinate.
	 */
	public float by =0.0f;
	/**
	 * Constructs a horizontal line intersecting the origin.
	 */
	public Line() {}
	/**
	 * Construct a line having the coefficient values specified.
	 */
	public Line(float a, float bx, float by)
	{
		this.bx = bx;
		this.by = by;
		this.a = a;
	}
	/**
	 * Constructs the line that intersects the given points.
	 * @param a a point of intersection.
	 * @param b Another point of intersection.
	 * @return The line that intersects points a and b.
	 */
	public static Line fromPoints(Point a, Point b)
	{
		if(a==null || b==null || a.isEmpty() || b.isEmpty() || a.equals(b))
			return null;

		double dx = b.x -a.x;
		double dy = b.y -a.y;
		double norm = Math.sqrt(dx*dx+dy*dy);
		Line output = new Line();
		output.bx = (float)(dy/norm);
		output.by = (float)(-dx/norm);
		output.a = -output.signedDistance(a);
		return output;
	}
	/**
	 * Returns the Euclidian norm of the vector (bx, by).  This line is in normal form if the norm is 1.0.
	 * @return  The Euclidian norm of (bx,by).  The expected value is 1.
	 * @url http://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm
	 */
	public float norm()
	{
		return (float)Math.sqrt(this.bx *this.bx +this.by *this.by);
	}
	/**
	 * Scales the coefficients to standard form, where this.norm() is 1.0.
	 */
	public void normalize()
	{
		float norm = this.norm();
		if(norm==0.0f)
		{
			this.bx = 1.0f;
			this.by = 0.0f;
		}
		else if(1.0f!=norm)
		{
			this.bx = this.bx / norm;
			this.by = this.by / norm;
			this.a = this.a / norm;
		}
	}
	/**
	 * Given two points which fall on the line, this function returns two perpendicular lines, each of which intersects one of the
	 * respective points.  The line segment a-->b includes only points where output[0].signedDistance(Point) and output[1].signedDistance(Point)
	 * evaluate as positive.
	 * @param a The point intersecting output[0]
	 * @param b The point intersecting output[1]
	 * @return Line[2], where output[0] intersects line a, and output[1] intersects line b.
	 */
	public Line[] segmentLimits(Point a, Point b)
	{
		Line[] output = new Line[2];
		output[0] = new Line();
		output[1] = new Line();
		output[0].bx = output[1].bx = this.by;
		output[0].by = output[1].by = -this.bx;
		output[0].a = -output[0].signedDistance(a);
		output[1].a = -output[1].signedDistance(b);
		if(output[0].signedDistance(b) < 0)
			output[0] = output[0].conjugate();
		if(output[1].signedDistance(a) < 0)
			output[1] = output[1].conjugate();
		return output;
	}
	/**
	 * Calculates the signed distance from the point to the line.  Signed distance is positive to one side of the line, negative
	 * to the other side.  The magnitude of the output is distance.
	 * @param p The point to which signed distance is calculated.
	 * @return The signed distance from the point to the line.
	 */
	public float signedDistance(Point p)
	{
		return this.bx * p.x + this.by * p.y + this.a;
	}
	/**
	 * Returns the conjugate line, intersecting the exact same points in 2D space, where the coefficients are of the same
	 * magnitude, but where the coefficients have opposite sign.
	 * @return The conjugate line.
	 */
	public Line conjugate()
	{
		return new Line(-this.a, -this.bx, -this.by);
	}
}
