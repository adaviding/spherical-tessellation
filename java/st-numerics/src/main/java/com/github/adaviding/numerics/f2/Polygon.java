package com.github.adaviding.numerics.f2;

import com.github.adaviding.numerics.D;

/**
 * a polygon defined with respect to standard 2D Cartesian space.
 *
 * @author Almon David Ing, PhD aing
 */
public class Polygon extends Path
{
	/**
	 * Allocates memory for a polygon having the number of vertices specified.
	 *
	 * @param nVertices  The exact number of polygon vertices.
	 */
	public Polygon(int nVertices)
	{
		super(nVertices);
	}
	/**
	 * Ensures that the polygon is clockwise by reversing the vertices if necessary.
	 *
	 * @return True if the order of vertices was reversed, false if the polygon was already clockwise.
	 */
	public boolean ensureClockwise()
	{
		if(this.isClockwise())
			return false;
		this.reverse();
		return true;
	}
	/**
	 * Determines if the point p lies inside the polygon (or on the edge of the polygon).
	 *
	 * @param p  The coordinate in a 2D space.
	 * @return Returns 1 if p is inside the polygon, 0 if on the edge, and -1 if outside polygon.
	 */
	public int contains(Point p)
	{
		return this.contains(p.x,p.y);
	}
	/**
	 * Determines if the point (x,y) lies inside the polygon (or on the edge of the polygon).
	 *
	 * @param x  The horizontal ordinate in a 2D space.
	 * @param y  The vertical ordinate in a 2D space.
	 * @return Returns 1 if (x,y) is inside the polygon, 0 if on the edge, and -1 if outside polygon.
	 */
	public int contains(float x, float y)
	{
		if(this.vertices ==null || this.vertices.length < 3)
			return -1;
		if(this.tiny==null)
			this.onVerticesSet();
		float sqTiny = tiny*tiny;
		float sqTinyNeg = -sqTiny;

		int i;
		float x1, x2, y1, y2; //	The point-centered vertex coordinates for the current and next vertex.
		float dotProduct, crossProduct;
		int quad=0, prevQuad, diffQuad, sumDiffQuad=0, signCross;

		//	Start it off
		x2 = this.vertices[this.vertices.length-1].x -x;
		y2 = this.vertices[this.vertices.length-1].y -y;

		//	Denote quadrant affiliation for each vertex
		if(x2>0.0 && y2>0.0) quad = 0;
		else if(x2<=0.0 && y2>0.0) quad = 1;
		else if(x2<=0.0 && y2<=0.0) quad = 2;
		else if(x2>0.0 && y2<=0.0) quad = 3;

		//	For each vertex
		for(i=0; i<this.vertices.length; i++)
		{
			//	Update previous quadrant
			prevQuad = quad;

			//	vertices of interest
			x1=x2;
			y1=y2;
			x2=this.vertices[i].x -x;
			y2=this.vertices[i].y -y;

			//	Compute the cross-product and dot-product.
			dotProduct=x1*x2+y1*y2;
			crossProduct = x1*y2-x2*y1;
			if(crossProduct<sqTinyNeg) signCross=-1;
			else if(crossProduct>sqTiny) signCross=1;
			else signCross=0; // area of parallelogram suggests a tiny distance.

			if(signCross==0 && dotProduct <= 0.0f)
				return 0; 	//	The point lies on the polygon edge, or extremely close (within a distance of "tiny").

			//	Denote quadrant affiliation for each vertex
			if(x2>0.0 && y2>0.0) quad= 0;
			else if(x2<=0.0 && y2>0.0) quad= 1;
			else if(x2<=0.0 && y2<=0.0) quad= 2;
			else if(x2>0.0 && y2<=0.0) quad= 3;

			diffQuad = quad-prevQuad;

			//	Replace 3 by -1 and -3 by 1;
			if(diffQuad==3)
				diffQuad=-1;
			else if(diffQuad==-3)
				diffQuad=1;
				//	Any DiffQuad with a value of 2 should have the same sign as the cross product;
			else if(diffQuad==2 || diffQuad==-2)
				diffQuad = 2*signCross;

			//	Summation
			sumDiffQuad += diffQuad;
		}

		if(sumDiffQuad != 0)
			return 1;	//	The point lies inside of the polygon.

		//	The point lies outside of the polygon.
		return -1;
	}
	public int contains(double x, double y)
	{
		return this.contains((float)x,(float)y);
	}
	/**
	 * Determines if the polygon boundary intersects a line segment.
	 * @param a0  One edge of the line segment.
	 * @param a1  Another end of the line segment.
	 * @return  True if the path intersects the line segment, false otherwise.
	 */
	@Override
	public boolean intersects(Point a0, Point a1)
	{
		if( null == this.vertices)
			return false;

		Point bPrev;
		Point bThis = this.vertices[this.vertices.length-1];
		for(int i = 0; i<this.vertices.length; i++)
		{
			bPrev = bThis;
			bThis = this.vertices[i];
			if(	null != Point.intersection(a0,a1,bPrev,bThis) )
				return true;
		}

		return false;
	}
	/**
	 * Determines if the polygon is clockwise.  a clockwise polygon is one where the inside of the polygon tends to be rightward
	 * from any point along the perimeter path, if facing forward towards the next vertex in the sequence defining that path.
	 *
	 * @return True if the polygon is clockwise, false otherwise.
	 */
	public boolean isClockwise()
	{
		double curv, totalCurv = 0.0;
		double dx,dy;
		double or, orPrev;
		Point pThis = this.vertices[this.vertices.length-1];
		Point pNext = this.vertices[0];
		dy = pNext.y -pThis.y;
		dx = pNext.x -pThis.x;
		or = Math.atan2(dy,dx);
		for(int i = 0; i<this.vertices.length; i++)
		{
			orPrev = or;
			pThis = pNext;
			int iNext = i+1;
			if(iNext==this.vertices.length)
				iNext = 0;
			pNext = this.vertices[iNext];

			dy = pNext.y -pThis.y;
			dx = pNext.x -pThis.x;
			or = Math.atan2(dy,dx);

			curv = D.normalizeRadians(or-orPrev);
			totalCurv += curv;
		}
		return totalCurv < 0.0;
	}
	/**
	 * This method should be called after the vertices are set or changed.  If the vertices are changed and this
	 * method is not called, then subsequent function calls may yield inaccurate results.
	 */
	@Override
	public void onVerticesSet()
	{
		super.onVerticesSet();

		if(null == this.vertices || this.vertices.length<1)
			return;

		Point pLast = this.vertices[this.vertices.length-1];
		Point p = this.vertices[0];
		Point delta = new Point(p.x -pLast.x,p.y -pLast.y);
		this.pathLength += (float)Math.sqrt(delta.x *delta.x + delta.y *delta.y);
	}
	/**
	 * Reverses the sequence of polygon vertices.
	 */
	protected void reverse()
	{
		Point p = null;
		int i=0;
		int j = vertices.length-1;
		while(j>i)
		{
			p = this.vertices[i];
			this.vertices[i] = this.vertices[j];
			this.vertices[j] = p;
			i++;
			j--;
		}
	}
	/**
	 * Converts the polygon to a Well Known Text (WKT) string.
	 *
	 * @return The WKT string, something like:  "POLYGON((...))"
	 * @url http://en.wikipedia.org/wiki/Well-known_text
	 */
	public String toWkt()
	{
		StringBuilder sb = new StringBuilder("POLYGON((");
		if(this.vertices !=null && this.vertices.length > 0)
		{
			Point p;
			for(int i = 0; i<this.vertices.length; i++)
			{
				p = this.vertices[i];
				sb.append(Float.toString(p.x) + " " + Float.toString(p.y) + ", ");
			}
			p = this.vertices[0];
			sb.append(Float.toString(p.x) + " " + Float.toString(p.y));
		}
		sb.append("))");
		return sb.toString();
	}
}
