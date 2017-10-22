package com.github.adaviding.numerics.f3;

/**
 * Defines a plane in 3D cartesian space.  The plane is defined by 4 coefficients (a,bx,by,bz) and 3 coordinates (x,y,z) as
 * 		0 = a + bx * x + by * y + bz * z
 * subject to the constraint
 * 		1 = bx * bx + by * by + bz*bz
 */
public class Plane
{
	/**
	 * The additive coefficient.
	 */
	public float a = 0.0f;
	/**
	 * The coefficient that scales the x-ordinate.
	 */
	public float bx = 1.0f;
	/**
	 * The coefficient that scales the y-ordinate.
	 */
	public float by = 0.0f;
	/**
	 * The coefficient that scales the z-ordinate.
	 */
	public float bz = 0.0f;
	/**
	 * Constructs a horizontal line intersecting the origin.
	 */
	public Plane() {}
	/**
	 * Construct a line having the coefficient values specified.
	 */
	public Plane(float a, float bx, float by, float bz)
	{
		this.a = a;
		this.bx = bx;
		this.by = by;
		this.bz = bz;
	}
	/**
	 * Returns the conjugate plane, intersecting the exact same points in 3D space, where the coefficients are of the same
	 * magnitude, but where the coefficients have opposite sign.
	 * @return The conjugate plane.
	 */
	public Plane conjugate()
	{
		return new Plane(-this.a, -this.bx, -this.by, -this.bz);
	}
	/**
	 * Constructs the plane that intersects the given points.
	 * @param a a point of intersection.
	 * @param b a point of intersection.
	 * @param c a point of intersection.
	 * @return The plane that intersects points a, b, and c.
	 */
	public static Plane fromPoints(Point a, Point b, Point c)
	{
		throw new RuntimeException("Not implemented");
	}
	/**
	 * Returns the Euclidian norm of the vector (bx,by,bz).  This line is in normal form if the norm is 1.0.
	 * @return  The Euclidian norm of (bx,by,bz).  The expected value is 1.
	 * @url http://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm
	 */
	public float norm()
	{
		return (float)Math.sqrt(this.bx *this.bx + this.by *this.by + this.bz *this.bz);
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
			this.bz = 0.0f;
		}
		else if(1.0f!=norm)
		{
			this.bx = this.bx / norm;
			this.by = this.by / norm;
			this.bz = this.bz / norm;
			this.a = this.a / norm;
		}
	}
	/**
	 * Calculates the signed distance from the point to the plane.  Signed distance is positive to one side of the plane, negative
	 * to the other side.  The magnitude of the output is distance.
	 * @param v  The point to which signed distance is calculated.
	 * @return The signed distance from the point to the line.
	 */
	public float signedDistance(Point v)
	{
		return this.a + this.bx * v.x + this.by * v.y + this.bz * v.z;
	}
}
