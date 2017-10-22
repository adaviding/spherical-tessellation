package com.github.adaviding.numerics.f2;

import com.github.adaviding.numerics.F;

/**
 * 	a rectangle in 2-space.
 */
public class Rect
{
	public float left;
	public float right;
	public float top;
	public float bottom;

	public Rect() {}
	public Rect(float left, float right, float top, float bottom)
	{
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
	}
	/**
	 * Calculates the smallest possible rectangle that contains both points a and b.  This will produce a rectangle
	 * with corners defined by coordinate values of a and b.
	 * @param a Point a
	 * @param b Point b
	 * @return The smallest possible bounding rectangle.
	 */
	public static Rect bound(Point a, Point b)
	{
		Rect output = new Rect();
		output.left = Math.min(a.x,b.x);
		output.right = Math.max(a.x,b.x);
		output.bottom = Math.min(a.y,b.y);
		output.top = Math.max(a.y,b.y);
		return output;
	}
	/**
	 * Creates a new instance having identical values as this instance.
	 * @return  The new instance.
	 */
	public Rect clone()
	{
		return new Rect(this.left, this.right, this.top, this.bottom);
	}
	/**
	 * Determines whether a point is inside the rectangle.
	 * @param p The point.
	 * @return True if the point is inside the rectangle or on the boundary, false otherwise.
	 */
	public boolean contains(Point p)
	{
		return p.x >=this.left && p.x <=this.right && p.y >=this.bottom && p.y <=this.top;
	}
	/**
	 * Determines whether a point is inside the rectangle.
	 * @param x The horizontal coordinate of the point.
	 * @param y The vertical coordinate of the point.
	 * @return True if the point is inside the rectangle or on the boundary, false otherwise.
	 */
	public boolean contains(float x, float y)
	{
		return x>=this.left && x<=this.right && y>=this.bottom && y<=this.top;
	}
	/**
	 * Deserializes the instance from a human-readable string.
	 * @param s  The string represented as 4 comma separated values:  left,top,right,bottom.
	 * @return a new Rect having deserialized values.  May also return null.  May return Rect with NaN values.
	 */
	public static Rect fromLtrb(String s)
	{
		if(s==null || s.length()==0)
			return null;

		String[] ss = s.split(",");
		if(ss.length<4)
			return null;

		Rect output = new Rect();
		output.left = F.tryParse(ss[0].trim());
		output.top = F.tryParse(ss[1].trim());
		output.right = F.tryParse(ss[2].trim());
		output.bottom = F.tryParse(ss[3].trim());
		return output;
	}
	/**
	 * Calculates the height of the rectangle.
	 * @return The height.
	 */
	public float height()
	{
		return this.top -this.bottom;
	}
	/**
	 * Returns true if the rectangles share any area or part of an edge, false otherwise.
	 * @param a Rectangle a
	 * @param b Rectangle b
	 * @return True if the rectangles share any area or if their edges touch, false otherwise.
	 */
	public static boolean overlaps(Rect a, Rect b)
	{
		return 		a.contains(b.left,b.top) 		|| b.contains(a.left,a.top)
				||	a.contains(b.left,b.bottom) 	|| b.contains(a.left,a.bottom)
				||	a.contains(b.right,b.top) 		|| b.contains(a.right,a.top)
				||	a.contains(b.right,b.bottom) 	|| b.contains(a.right,a.bottom);
	}
	/**
	 * Serializes the instance as a human-readable string.
	 * @return  The string represented as 4 comma separated values:  left,top,right,bottom.
	 */
	public String toLrtbString()
	{
		return 			Float.toString(this.left)
				+","+	Float.toString(this.top)
				+","+	Float.toString(this.right)
				+","+	Float.toString(this.bottom);
	}
	/**
	 * Expresses the instance as a string.
	 * @return The string represented as 4 comma separated values:  left,top,right,bottom.
	 */
	public String toString()
	{
		return this.toLrtbString();
	}
	/**
	 * Calculates the width of the rectangle.
	 * @return The width.
	 */
	public float width() { return this.right -this.left;	}
}