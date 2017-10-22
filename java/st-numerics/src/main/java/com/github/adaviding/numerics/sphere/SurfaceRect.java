package com.github.adaviding.numerics.sphere;

import com.github.adaviding.numerics.f2.Rect;
import com.github.adaviding.numerics.f3.Point;

/**
 * a rectangle on the surface of a sphere.  The rectangles sides are always parallel to lines of latitude or longitude.
 */
public class SurfaceRect extends Rect
{
	public SurfaceRect() {};
	public SurfaceRect(float left, float right, float top, float bottom) {super(left, right, top, bottom);}
	/**
	 * Determines whether a point is inside the rectangle.  Properly handles situations where the rectangle overlaps the international date line.
	 * @param p The point.
	 * @return True if the point is inside the rectangle or on the boundary, false otherwise.
	 */
	@Override
	public boolean contains(com.github.adaviding.numerics.f2.Point p)
	{
		if(this.right > this.left)
		{
			//	Regular:  Does not intersect international date line.
			return p.y >= this.bottom && p.y <= this.top && p.x >= this.left && p.x <= this.right;
		}
		else
		{
			//	Irregular:  intersects international date line.
			return p.y >=this.bottom && p.y <=this.top && (p.x <=this.left || p.x >=this.right);
		}
	}
	/**
	 * Determines whether a point is inside the rectangle.  Properly handles situations where the rectangle overlaps the international date line.
	 * @param x The horizontal coordinate of the point (or the longitude).
	 * @param y The vertical coordinate of the point (or the latitude).
	 * @return True if the point is inside the rectangle or on the boundary, false otherwise.
	 */
	@Override
	public boolean contains(float x, float y)
	{
		if(this.right > this.left)
		{
			//	Regular:  Does not intersect international date line.
			return y >= this.bottom && y <= this.top && x >= this.left && x <= this.right;
		}
		else
		{
			//	Irregular:  intersects international date line.
			return y>=this.bottom && y<=this.top && (x<=this.left || x>=this.right);
		}
	}
	/**
	 * Determines whether the coordinate is inside the rectangle.  Properly handles situations where the rectangle overlaps the international date line.
	 * @param x The coordinate.
	 * @return True if the coordinate is inside the rectangle or on the boundary, false otherwise.
	 */
	public boolean contains(LatLon x)
	{
		if(this.right > this.left)
		{
			//	Regular:  Does not intersect international date line.
			return x.lat>=this.bottom && x.lat<=this.top && x.lon>=this.left && x.lon<=this.right;
		}
		else
		{
			//	Irregular:  intersects international date line.
			return x.lat>=this.bottom && x.lat<=this.top && (x.lon<=this.left || x.lon>=this.right);
		}
	}
	/**
	 * Computes the centroid of the rectangle.  Properly handles situations where the rectangle overlaps the international date line.
	 * @return The centroid of the rectangle.
	 */
	public LatLon Centroid()
	{
		Point uvec = new LatLon(this.bottom,this.left).toUnitVector();
		uvec.add(new LatLon(this.top,this.right).toUnitVector());
		uvec = uvec.toUnitVector();
		return LatLon.fromUnitVector(uvec);
	}
	/**
	 * The signed distance of the coordinate from the rectangle.  distance is positive if the coordinate is inside the rectangle,
	 * negative if the coordinate is outside the rectangle.  distance is measured "as the crow flies" along the surface of the sphere.
	 *
	 * @param x The coordinate.
	 * @return The distance (in degrees) between the specified coordinate and the rectangle.  distance is negative if the coordinate
	 * is inside the rectangle, positive otherwise.
	 */
	public float signedDistance(LatLon x)
	{
		throw new RuntimeException("Not implemented");
	}
}
