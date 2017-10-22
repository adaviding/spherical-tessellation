package com.github.adaviding.numerics.sphere;

import com.github.adaviding.numerics.f2.Polygon;
import com.github.adaviding.numerics.f3.Matrix;
import com.github.adaviding.numerics.f3.Point;

import java.util.List;

/**
 * This class represents the properties of a polygon on the surface of a sphere.  It maintains correct functionality
 * when the polygon is intersected by the international date line, or when the polygon overlaps one of the poles.
 *
 * This class functions well for polygons which cover ~1/3 of the sphere or less.  If the polygon is any larger, there is
 * a danger that an instance of this class will represent the properties of the polygon's complement (i.e. another polygon
 * covering the exact opposite area of the sphere).
 *
 * Functionality breaks if any point on the polygon's perimeter is more than 180 degrees from the polygon centroid .
 */
public class SphericalPolygon
{
	/**
	 * a spherical cap surrounding the polygon, where cap.Position is the centroid of the polygon, and where cap.DomeRadius
	 * is the distance of the furthest polygon vertex from cap.Position.
	 *
	 * The centroid is calculated as the average position of N evenly spaced points sampled along the polygon's edge, taken
	 * as N approaches infinity, then scaled to intersect the surface of the unit 3-ball.
	 */
	public final Cap cap;
	/**
	 * The unit vector representation of the centroid, equivalent to {@link #cap#position.toUnitVector()}.
	 */
	protected final Point centroidUvec;
	/**
	 * a matrix that rotates a 3D unit vector to the coordinate system where the centroidUvec is represented as (0,0,-1).
	 *
	 * In other words...
	 * 		(0,0,-1) = this.centroidToOrigin.multiply(this.centroidUvec)
	 */
	protected final Matrix centroidToOrigin;
	/**
	 * The polygon vertices.  Each vertex is expressed in terms of its location on a sphere which has been rotated by
	 * the "centroidToOrigin" rotation.  On such a sphere, the polygon's centroid is exactly (y=Lat=0,x=Lon=0).
	 */
	protected final Polygon polygon;
	/**
	 * The perimeter length, in units of degrees.
	 *
	 * To get kilometers:
	 * 		this.perimeter * F.RadiansPerDegree * F.EarthRadiusKilometers
	 *
	 * To get miles:
	 * 		this.perimeter * F.RadiansPerDegree * F.EarthRadiusMiles
	 */
	public final float perimeter;
	/**
	 * Constructs a surface polygon based on the vertices specified.
	 * @param vertices The coordinate values of the polygon vertices.  This object and its contents are returned to the
	 *                 caller unchanged, and are not referenced by the newly instantiated object.
	 */
	public SphericalPolygon(List<LatLon> vertices)
	{
		if(vertices!=null && vertices.size() > 0)
			this.cap = new Cap();
		else
			this.cap = null;
		if(vertices==null || vertices.size() < 3)
		{
			this.centroidUvec = null;
			this.centroidToOrigin = null;
			this.polygon = null;
			this.perimeter = 0.0f;
			return;
		}

		this.polygon = new Polygon(vertices.size());

		//	----------------------------------------
		//	Compute the centroid as an f3.Point, by computing the weighted average of vectors.
		//	----------------------------------------
		Point avg = Point.zero();
		Point vec = null;

		LatLon pThis, pNext;
		double dPrev, dNext, d, dTotal=0.0f;
		double dx, dy;
		int iNext;

		pThis = vertices.get(vertices.size()-1);
		pNext = vertices.get(0);
		if(pThis==null || pNext==null || pThis.isEmpty() || pNext.isEmpty())
			throw new IllegalArgumentException("The list of vertices cannot contain a null or empty entry.");

		dNext = LatLon.distRadians(pNext,pThis);

		for(int i=0; i<vertices.size(); i++)
		{
			dPrev = dNext;
			pThis = pNext;
			iNext = i+1;

			if(iNext==vertices.size())
				pNext = vertices.get(0);
			else
				pNext = vertices.get(iNext);

			if(pNext==null || pNext.isEmpty())
				throw new IllegalArgumentException("The list of vertices cannot contain a null or empty entry.");

			dNext = LatLon.distRadians(pNext,pThis);
			d = dPrev+dNext;

			vec = pThis.toUnitVector();
			vec.multiply(d);
			dTotal += d;

			//	Weighted sum
			avg.add(vec);
		}

		//	----------------------------------------
		//	Set the centroid and perimeter
		//	----------------------------------------
		this.centroidUvec = avg.toUnitVector();
		this.centroidToOrigin = Matrix.rotationToOrign(this.centroidUvec);
		this.cap.position = LatLon.fromUnitVector(avg);
		this.perimeter = (float)(0.5*dTotal);

		//	----------------------------------------
		//	Construct the polygon by rotating coordinates to a sphere where the centroid would be exactly (0,0,-1).
		//	Calculate cap.DomeRadius.
		//	----------------------------------------
		for(int i=0; i<vertices.size(); i++)
		{
			//	Expand the cap to include all vertices.
			LatLon ll = vertices.get(i);
			this.cap.expandToInclude(ll);
			//	Rotate to the centroid-at-origin space.
			pThis = LatLon.fromUnitVector(this.centroidToOrigin.multiply(ll.toUnitVector()));
			//	Store as (Lon,Lat)
			this.polygon.vertices[i] = new com.github.adaviding.numerics.f2.Point(pThis.lon, pThis.lat);
		}
		this.polygon.ensureClockwise();
		this.polygon.onVerticesSet();
	}
	/**
	 * Determines if the coordinate lies inside the polygon (or on the edge of the polygon).
	 * @param x  The coordinate.
	 * @return Returns 1 if coordinate is inside the polygon, 0 if on the edge, and -1 if outside polygon.
	 */
	public int contains(LatLon x)
	{
		x = LatLon.fromUnitVector(this.centroidToOrigin.multiply(x.toUnitVector()));
		return this.polygon.contains(x.lon,x.lat);
	}
	/**
	 * Get the number of polygon vertices.
	 * @return The number of polygon vertices.
	 */
	public int numVertices()
	{
		if(this.polygon==null)
			return 0;
		return this.polygon.vertices.length;
	}
	/**
	 * Determines if the areas overlap between a SurfaceRect and this polygon.
	 * @param sr The SurfaceRect.
	 * @return True if the edges intersect, false otherwise.
	 */
	public boolean overlaps(SurfaceRect sr)
	{
		if(this.polygon==null || sr==null)
			return false;

		com.github.adaviding.numerics.f2.Point[] rect = null;
		if(sr.top >89.999)
		{
			//	Rectangle encompasses the whole globe.
			if(sr.bottom <-89.999)
				return true;

			//	Polygon from triangle at North pole
			rect = new com.github.adaviding.numerics.f2.Point[3];
			rect[0] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new LatLon(sr.bottom,sr.right).toUnitVector())).toPoint();
			rect[1] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new LatLon(sr.bottom,sr.left).toUnitVector())).toPoint();
			rect[2] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new Point(0,1,0))).toPoint();

		}
		else if(sr.bottom <-89.999)
		{
			//	Polygon from triangle at South pole
			rect = new com.github.adaviding.numerics.f2.Point[3];
			rect[0] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new LatLon(sr.top,sr.left).toUnitVector())).toPoint();
			rect[1] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new LatLon(sr.top,sr.right).toUnitVector())).toPoint();
			rect[2] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new Point(0,-1,0))).toPoint();
		}
		else
		{
			//	Polygon from Rectangle
			rect = new com.github.adaviding.numerics.f2.Point[4];
			rect[0] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new LatLon(sr.bottom,sr.right).toUnitVector())).toPoint();
			rect[1] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new LatLon(sr.bottom,sr.left).toUnitVector())).toPoint();
			rect[2] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new LatLon(sr.top,sr.left).toUnitVector())).toPoint();
			rect[3] = LatLon.fromUnitVector(this.centroidToOrigin.multiply(new LatLon(sr.top,sr.right).toUnitVector())).toPoint();
		}

		//	Check for vertex containment.
		for(com.github.adaviding.numerics.f2.Point pThis : rect)
		{
			if(this.polygon.contains(pThis) >= 0)
				return true;
		}

		//	Check for line intersection.
		com.github.adaviding.numerics.f2.Point pLast = rect[rect.length-1];
		for(com.github.adaviding.numerics.f2.Point pThis : rect)
		{
			if (this.polygon.intersects(pLast, pThis))
				return true;
			pLast = pThis;
		}

		return false;
	}
	/**
	 * The signed distance of the coordinate from the polygon.  distance is positive if the coordinate is inside the polygon,
	 * negative if the coordinate is outside the polygon.  distance is measured "as the crow flies" along the surface of the sphere.
	 *
	 * @param x The coordinate.
	 * @return The distance (in degrees) between the specified coordinate and the polygon.  distance is negative if the coordinate
	 * is inside the polygon, positive otherwise.
	 */
	public float signedDistance(LatLon x)
	{
		throw new RuntimeException("Not Implemented");
	}
}
