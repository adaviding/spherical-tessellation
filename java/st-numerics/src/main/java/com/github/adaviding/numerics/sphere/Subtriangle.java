package com.github.adaviding.numerics.sphere;

import com.github.adaviding.numerics.f3.Plane;
import com.github.adaviding.numerics.f3.Point;

import javax.validation.constraints.NotNull;

/**
 * This class represents a spherical triangle covering a portion of the sphere's surface.  Except for the root node
 * (representing the entire sphere), each spherical triangle is a portion of the parent triangle.  Therefore, each
 * subtriangle is a node inside a hierarchy.  The entire hierarchy represents a uniform tessellation of the unit sphere.
 * All subtriangles are equilateral, and all subtriangles at a given depth are of equal size.
 *
 * The first layer of subtriangles is the "octants".  These are 8 spherical triangles created by dividing the root node
 * (which represents the entire sphere) into 8 spherical triangles which arise from three bisections of the sphere.
 * The bisectors are three orthogonal planes ...
 *
 * 	(1) the equator (i.e. the plane of 0 latitude),
 * 	(2) the grand meridian / international date line (i.e. the plane where longitude is 0 or 180), and
 * 	(3) the plane where longitude is -90 or +90
 *
 * ... which divide the sphere into 8 equally sized sections or "octants".
 *
 * The second layer of subtriangles is created by 4-secting octant into 4 equilateral triangles, resulting in 32 subtriangles at layer 2.
 *
 * The third layer of subtriangles  is created by 4-secting the triangles of the second layer, resulting in 128 subtriangles at layer 3.
 *
 * In general, the i-th layer is a uniform tessellation of the unit sphere consisting of 8*4^(i-1) equilateral spherical triangles of equal size.
 *
 * @url http://mathworld.wolfram.com/SphericalTriangle.html
 */
public class Subtriangle
{
	/**
	 * The hierarchical address of the node within the hierarchy.  The most significant entry (index = 0) identifies an
	 * octant of the sphere using only 3 bits, as described  here:
	 * {@literal
	 * 		0 -->	lat >= 0,   90 <= lon <= 180
	 * 		1 -->	lat >= 0,    0 <= lon <   90
	 * 		2 -->	lat >= 0,  -90 <= lon <    0
	 * 		3 -->	lat >= 0, -180 <  lon <  -90
	 * 		4 -->	lat <  0,   90 <= lon <= 180
	 * 		5 -->	lat <  0,    0 <= lon <   90
	 * 		6 -->	lat <  0,  -90 <= lon <    0
	 * 		7 -->	lat <  0, -180 <  lon <  -90
	 * }
	 *
	 * The less significant entries (index > 0) use 2 bits to identify an equilateral subtriangle.within an equilateral
	 * parent trianglge as follows:
	 * {@literal
	 * 		0 --> Triangle nearest the center of the parent triangle.
	 *  	1 --> Triangle of central longitude and extreme latitude (northward or southward).
	 *  	2 --> Triangle of lowest longitude (westward)
	 *  	3 --> Triangle of highest longitude (eastward)
	 *  }
	 */
	public byte[] address;
	/**
	 * The superordinate division, for which this node is a subdivision.  For the root node (representing the entire sphere),
	 * this member is null.
	 */
	public Subtriangle parent = null;
	/**
	 * The subdivisions.
	 */
	public Subtriangle[] children = null;
	/**
	 * Three planes of the spherical triangle.
	 * 	Plane 0 intersects vertices 0 and 1.
	 * 	Plane 1 intersects vertices 1 and 2.
	 * 	Plane 2 intersects vertices 2 and 0.
	 */
	public Plane[] planes;
	/**
	 * The vertices of the spherical triangle.  The length is 3.  All entries are unit vectors.
	 * 	Vertex 0 is of central longitude and extreme latitude.
	 * 	Vertex 1 is of lowest longitude (west)
	 * 	Vertex 2 is of highest longitude (east)
	 */
	public Point[] vertices;
	/**
	 * Calculates 3 planes which form 3 sides of a pyramid.  The tip of the pyramid is the center of the sphere (0,0,0).
	 * Each plane intersects (0,0,0) and two distinct vertices of a spherical triangle.
	 *
	 * These planes are useful because the signed distance to all 3 planes is positive for points lying on the subtriangle.
	 * If the signed distance for any plane is negative then the point does not lie on the subtriangle.
	 *
	 * @param vertices Three vertices lying on the surface of a sphere which define a spherical triangle.  Typically each
	 *                 vertex is a unit vector which lies on the surface of a unit sphere.
	 * @return The three planes.
	 */
	public static Plane[] calcPlanes(@NotNull Point[] vertices) {
		if (vertices == null || vertices.length < 3) {
			throw new RuntimeException("3 vertices are required to construct a plane");
		}

		Plane[] output = new Plane[3];

		Point mid = Point.zero();
		mid.add(vertices[0]);
		mid.add(vertices[1]);
		mid.add(vertices[2]);
		mid.x /= 3f;
		mid.y /= 3f;
		mid.z /= 3f;

		output[0] = calcPlane(vertices[0], vertices[1], mid);
		output[1] = calcPlane(vertices[1], vertices[2], mid);
		output[2] = calcPlane(vertices[2], vertices[0], mid);

		return output;
	}
	/**
	 * Calculates a plane that intersects (0,0,0) and the two points `a` and `b`.
	 * @param a One of the points that the plane must intersect.
	 * @param b One of the points that the plane must intersect.
	 * @param posSide  A point for which signed distance to the plane must be positive.
	 * @return The calculated plane.
	 */
	private static Plane calcPlane(Point a, Point b, Point posSide) {
		Point unitCross = Point.cross(a,b).toUnitVector();
		Plane output = new Plane(0f, unitCross.x, unitCross.y, unitCross.z);
		if(output.signedDistance(posSide)<0f)
			output = output.conjugate();
		return output;
	}
	public void updatePlanes() {
		this.planes = calcPlanes(this.vertices);
	}
	/**
	 * 32-bytes is capable of identifying any spherical triangle of ~3.8 square meters on the surface of a
	 * tessellated Earth sphere.  Superordinate triangles may also be addressed.
	 * @param address The address of the subtriangle, see {@link #address}.
	 * @return The 32-bit packed address.
	 */
	public static int pack32(byte[] address) {
		if (address == null)
			return 0;
		// Number of elements in the input vector should not exceed 13
		int idLength = Math.min(13, address.length);
		if (idLength==0)
			return 0;

		int output = (idLength << 27) + ((int)address[0] << 24);
		int shift = 22;
		for(int i=1; i<idLength; i++) {
			output += (int)address[i] << shift;
			shift -= 2;
		}
		return output;
	}
	/**
	 * 64-bytes is capable of addressing any spherical triangle of around 10^-9 square meters on the surface of a
	 * tessellated Earth sphere.  Superordinate triangles can also be addressed.
	 * @param address  The address of the subtriangle, see {@link #address}.
	 * @return  The 64-bit packed address.
	 */
	public static long pack64(byte[] address) {
		if (address == null)
			return 0L;
		// Number of elements in the input vector should not exceed 29
		int idLength = Math.min(29, address.length);
		if (idLength==0)
			return 0;

		long output = ((long)idLength << 59) + ((long)address[0] << 56);
		int shift = 54;
		for(int i=1; i<idLength; i++) {
			output += (int)address[i] << shift;
			shift -= 2;
		}

		return output;
	}
	/**
	 * Unpacks a subtriangle address.
	 * @param packed  The packed subtrianlge address.  See {@link #pack32(byte[])}.
	 * @return The unpacked subtriangle address.  See {@link #address}.
	 */
	public static byte[] unpack32(int packed) {
		int idLength = packed >> 27;
		byte[] output = new byte[idLength];
		if (idLength==0)
			return output;

		packed = (packed << 5);

		output[0] = (byte)(packed >> 29);
		packed = (packed << 3);

		for(int i=1; i<idLength; i++) {
			output[i] = (byte)(packed >> 30);
			packed = (packed << 2);
		}
		return output;
	}
	/**
	 * Unpacks a subtriangle address.
	 * @param packed  The packed subtrianlge address.  See {@link #pack64(byte[])}.
	 * @return The unpacked subtriangle address.  See {@link #address}.
	 */
	public static byte[] unpack64(long packed) {
		int idLength = (int)(packed >> 59);
		byte[] output = new byte[idLength];
		if (idLength==0)
			return output;

		packed = (packed << 5);

		output[0] = (byte)(packed >> 61);
		packed = (packed << 3);

		for(int i=1; i<idLength; i++) {
			output[i] = (byte)(packed >> 62);
			packed = (packed << 2);
		}
		return output;
	}
}