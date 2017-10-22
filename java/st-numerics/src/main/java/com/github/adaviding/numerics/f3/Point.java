package com.github.adaviding.numerics.f3;

import com.github.adaviding.numerics.F;

/**
 * Represents a single precision floating point coordinate in 3 space.
 *
 * We assume the left-handed coordinate system where...
 *
 *  	x-axis = thumb ... facing rightwards ... [-left +right]
 *  	y-axis = index finger ... facing upwards ... [-bottom +top]
 *  	z-axis = middle finger ... facing forwards ... [-Back +Front]
 */
public class Point
{
	public float x;
	public float y;
	public float z;
	/**
	 * Constructs a vector at the origin (0,0,0).
	 */
	public Point(){}
	/**
	 * Constructs a vector having the specified values.
	 */
	public Point(float x, float y, float z) { this.x = x;	this.y = y;	this.z = z;	}
	/**
	 * Adds the values of the given vector into this vector.
	 * @param v The given vector.
	 */
	public void add(Point v) {
		if(v!=null)
		{
			this.x += v.x;
			this.y += v.y;
			this.z += v.z;
		}
	}
	/**
	 * Computes the cross product between two vectors.
	 * @param a The first vector.
	 * @param b The second vector.
	 * @return The cross product.
	 */
	public static Point cross(Point a, Point b)
	{
		Point output = new Point();
		output.x = a.y*b.z - a.z*b.y;
		output.y = a.z*b.x - a.x*b.z;
		output.z = a.x*b.y - a.y*b.x;
		return output;
	}
	/**
	 * Computes the dot product between this vector and another.
	 * @param v  The other vector.
	 * @return The dot product.
	 * @url en.wikipedia.org/wiki/Dot_product
	 */
	public float dot(Point v)
	{
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	/**
	 * Checks to see if the vertices have equal values.
	 * @param v  The comparator.
	 * @return  Returns true if the values are equal to the values of the comparator, false otherwise.
	 */
	public boolean equals(Point v)
	{
		return this.x == v.x && this.y == v.y && this.z == v.z;
	}
	/**
	 * Determines if the points are equal within a specified tolerance.
	 * @param v  The comparator.
	 * @param tol  The tolerance for equality of floating point numbers.
	 * @return  True if this point is equal or nearly equal to the comparator within the specified tolerance, false otherwise.
	 */
	public boolean equals(Point v, float tol)
	{
		return F.testEqual(this.x,v.x,tol) && F.testEqual(this.y,v.y,tol) && F.testEqual(this.z,v.z,tol);
	}
	/**
	 * Deserializes the instance from a human-readable string.
	 * @param s  The string represented as 3 comma separated values:  x,y,z.
	 * @return a new Point having deserialized values, or null if the string was improperly formatted.
	 */
	public static Point fromString(String s)
	{
		if(s==null || s.length()==0)
			return null;

		String[] ss = s.split(",");
		if(ss.length<3)
			return null;

		Point output = new Point();
		output.x = F.tryParse(ss[0].trim());
		output.y = F.tryParse(ss[1].trim());
		output.z = F.tryParse(ss[2].trim());
		return output;
	}
	/**
	 * Determines if the coordiante is empty.  It is empty if any coordinate value is NaN.
	 * @return True if the coordinate is empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return Float.isNaN(this.x) || Float.isNaN(this.y) || Float.isNaN(this.z);
	}
	/**
	 * Multiplies the values of this instance by the scalar provided.
	 * @param s The scalar.
	 */
	public void multiply(double s) { this.x *= s; this.y *= s; this.z *= s; }
	/**
	 * Computes the result of a matrix-vector multiplication, where this vector is left-adjacent to the matrix.
	 * @param M  a matrix on the right hand side of this vector.
	 * @return The result of the matrix-vector multiplication.
	 */
	public Point multiply(Matrix M)
	{
		Point output = new Point();
		output.x = M.m00 * this.x + M.m10 * this.y + M.m20 * this.z;
		output.y = M.m01 * this.x + M.m11 * this.y + M.m21 * this.z;
		output.z = M.m02 * this.x + M.m12 * this.y + M.m22 * this.z;
		return output;
	}
	/**
	 * Calculates the length of the vector, also called the Euclidian norm.
	 */
	public double norm()
	{
		return Math.sqrt(this.x *this.x + this.y *this.y + this.z *this.z);
	}
	/**
	 * Expresses the instance as a string.
	 * @return The string represented as 3 comma separated values:  x,y,z.
	 */
	public String toString()
	{
		return 			Float.toString(this.x)
				+","+	Float.toString(this.y)
				+","+	Float.toString(this.z);
	}
	/**
	 * Creates a new vector where the values have been scaled so that the Euclidian norm is equal to 1.
	 * @return The new unit vector.
	 */
	public Point toUnitVector()
	{
		float norm = (float)this.norm();
		if(norm==0.0f)
			return new Point(0,0,-1);
		float scale = 1.0f/norm;
		return new Point(this.x *scale, this.y *scale, this.z *scale);
	}
	/**
	 * Creates a new point (0,0,0).
	 * @return  The new point.
	 */
	public static Point zero() {
		return new
				Point(0f,0f,0f);
	}
}
