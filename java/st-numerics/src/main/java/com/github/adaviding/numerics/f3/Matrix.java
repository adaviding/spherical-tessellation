package com.github.adaviding.numerics.f3;

import com.github.adaviding.numerics.F;

/**
 * Represents a 3 x 3 matrix of single-precision floating point values.
 */
public class Matrix
{
	//	Row 1
	public float m00;
	public float m01;
	public float m02;

	//	Row 2
	public float m10;
	public float m11;
	public float m12;

	//	Row 3
	public float m20;
	public float m21;
	public float m22;

	public Matrix()	{}

	/**
	 * Gets the matrix transpose.
	 *
	 * @return Returns the matrix transpose.
	 */
	public Matrix transpose()
	{
		Matrix output = new Matrix();

		output.m00 = this.m00;
		output.m01 = this.m10;
		output.m02 = this.m20;

		output.m10 = this.m01;
		output.m11 = this.m11;
		output.m12 = this.m21;

		output.m20 = this.m02;
		output.m21 = this.m12;
		output.m22 = this.m22;

		return output;
	}
	/**
	 * Tests to see whether the values of this matrix are equal to a comparator.
	 *
	 * @param m
	 * @return
	 */
	public boolean equals(Matrix m)
	{
		return 		this.m00 ==m.m00
				&&	this.m01 ==m.m01
				&&	this.m02 ==m.m02
				&&	this.m10 ==m.m10
				&&	this.m11 ==m.m11
				&&	this.m12 ==m.m12
				&&	this.m20 ==m.m20
				&&	this.m21 ==m.m21
				&&	this.m22 ==m.m22;
	}
	/**
	 * Tests to see whether the values of this matrix are equal to a comparator within a specified tolerance.
	 *
	 * @param m The comparator.
	 * @param tol The tolerance.  Typically a tiny, positive number.
	 * @return  True if the values are equal or nearly equal within the specified tolerance, false otherwise.
	 */
	public boolean equals(Matrix m, float tol)
	{
		return 		F.testEqual(this.m00,m.m00,tol)
				&&	F.testEqual(this.m01,m.m01,tol)
				&&	F.testEqual(this.m02,m.m02,tol)
				&&	F.testEqual(this.m10,m.m10,tol)
				&&	F.testEqual(this.m11,m.m11,tol)
				&&	F.testEqual(this.m12,m.m12,tol)
				&&	F.testEqual(this.m20,m.m20,tol)
				&&	F.testEqual(this.m21,m.m21,tol)
				&&	F.testEqual(this.m22,m.m22,tol);
	}
	/**
	 * Computes the result of a matrix-vector multiplication, where this matrix is multiplied by a right-adjacent vector.
	 *
	 * @param v  a vector on the right hand side of the matrix.
	 * @return The result of the matrix-vector multiplication.
	 */
	public Point multiply(Point v)
	{
		Point output = new Point();
		output.x = this.m00 * v.x + this.m01 * v.y + this.m02 * v.z;
		output.y = this.m10 * v.x + this.m11 * v.y + this.m12 * v.z;
		output.z = this.m20 * v.x + this.m21 * v.y + this.m22 * v.z;
		return output;
	}
	/**
	 * Computes a rotation matrix that would rotate the unit vector to the origin on the surface of a unit sphere (0,0,-1).
	 *
	 * @param uvec a unit vector
	 * @return a rotation matrix "output" that rotates a unit vector.  In other words:  (0,0,-1) = output.multiply(uvec).
	 * @url http://en.wikipedia.org/wiki/Rotation_matrix#In_three_dimensions
	 */
	public static Matrix rotationToOrign(Point uvec)
	{
		//	Ensure unit vector.
		uvec  = uvec.toUnitVector();

		//	Get angles
		double lat = Math.asin(uvec.y);
		double lon = Math.atan2(uvec.x,-uvec.z);

		float cosa = (float)Math.cos(lon);
		float cosb = (float)Math.cos(lat);
		float sina = (float)Math.sin(lon);
		float sinb = (float)Math.sin(lat);

		//	Set the rotation matrix.
		Matrix output = new Matrix();

		output.m00 = cosa;
		output.m01 = 0.0f;
		output.m02 = sina;

		output.m10 = -sina*sinb;
		output.m11 = cosb;
		output.m12 = cosa*sinb;

		output.m20 = -sina*cosb;
		output.m21 = -sinb;
		output.m22 = cosa*cosb;

		return output;
	}
	public static Matrix zero()
	{
		return new Matrix();
	}
	public static Matrix one()
	{
		Matrix output = new Matrix();
		output.m00 = output.m11 = output.m22 = 1.0f;
		return output;
	}
}
