package com.github.adaviding.numerics.sphere;

import com.github.adaviding.numerics.f3.Point;

/**
 * This class represents a mosaic of spherical triangles that uniformly cover the surface of a sphere.  It is useful for
 * quickly mapping a LatLon coordinate to a numbered spherical triangle which contains the LatLon.  It is also useful for
 * quickly finding a collection of equilateral spherical triangles within a pre-defined search radius of a LatLon.
 *
 * This class is like a HashMap for LatLon coordinates.  It maps any LatLon coordinate to a subtriangle instead of a bin.
 *
 * The first layer of subtriangles is the "octants".  These are 8 spherical triangles created by dividing the root node
 * (which represents the entire sphere) using 3 orthogonal bisections.  The bisectors are three orthogonal planes ...
 *
 * 	(1) the equator (i.e. the plane of 0 latitude),
 * 	(2) the grand meridian / international date line (i.e. the plane of 0 and 180 longitude), and
 * 	(3) the plane of -90 and +90 longitude
 *
 * ... which divide the sphere into 8 equally sized sections or "octants".
 *
 * The second layer of subtriangles is created by 4-secting each octant into 4 equilateral triangles, resulting in 32 subtriangles at layer 2.
 *
 * The third layer of subtriangles  is created by 4-secting each subtriangle from layer 2, resulting in 128 subtriangles at layer 3.
 * The i-th layer is a uniform tessellation of the unit sphere consisting of 8*4^(i-1) equally sized equilateral spherical triangles.
 *
 * @url http://mathworld.wolfram.com/SphericalTriangle.html
 * @url http://en.wikipedia.org/wiki/Tessellation#Tessellations_with_triangles_and_quadrilaterals
 */
public class Tessellation
{
	/**
	 * The depth of the subtriangle hierarchy (from the root node).  This is the number of layers under the root, not including
	 * the root.  This is also the maximum allowed length of {@link Subtriangle#address}.
	 */
	private int depth;
	/**
	 * The root node representing the entire sphere.  The nodes underneath (at any depth) provide a uniform tessellation
	 * of the sphere.
	 */
	private Subtriangle root;
	/**
	 * Constructs a tessellation to the given depth.
	 * @param depth  The depth of the subtriangle hierarchy, where a depth of 0 is just the root node by itself.
	 *               This is the number of layers under the root, not including the root.  This is also the
	 *               maximum allowed length of {@link Subtriangle#address}.
	 */
	public Tessellation(int depth) {
		this.depth = depth;
		this.root = new Subtriangle();

		if(this.depth > 0) {
			this.root.children = new Subtriangle[8];

			Point left  = new Point(-1f, 0f, 0f);
			Point right = new Point( 1f, 0f, 0f);

			Point down = new Point(0f,-1f,0f);
			Point up   = new Point(0f, 1f,0f);

			Point backward = new Point(0f, 0f, -1f);
			Point forward  = new Point(0f, 0f,  1f);

			for(int i=0; i<8; i++) {
				Subtriangle sub = new Subtriangle();
				sub.parent = this.root;
				this.root.children[i] = sub;

				sub.vertices = new Point[3];

				if(i<4)
					sub.vertices[0] = up;
				else
					sub.vertices[0] = down;

				int iMod4 = i%4;

				if (iMod4 == 0) {
					sub.vertices[1] = backward;
					sub.vertices[2] = right;
				} else if (iMod4 == 1) {
					sub.vertices[1] = left;
					sub.vertices[2] = backward;
				} else if (iMod4 == 2) {
					sub.vertices[1] = forward;
					sub.vertices[2] = left;
				} else if (iMod4 == 3) {
					sub.vertices[1] = right;
					sub.vertices[2] = forward;
				}

				sub.updatePlanes();
			}
		}
	}
}