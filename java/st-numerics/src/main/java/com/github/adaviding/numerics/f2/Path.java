package com.github.adaviding.numerics.f2;

/**
 * a sequence of 2D vertices.
 */
public class Path
{
	/**
	 * The sequence of vertices that define the path.
	 */
	public final Point[] vertices;
	/**
	 * Instantiates a new path with the specified number of vertices.
	 * @param numVertices The number of vertices.
	*/
	public Path(int numVertices)
	{
		if(numVertices < 0)
			throw new IllegalArgumentException("The number of vertices cannot be negative.");
		this.vertices = new Point[numVertices];
		for(int i=0; i<numVertices; i++)
			this.vertices[i] = new Point();
	}
	/**
	 * Determines if the path intersects a line segment.
	 * @param a0  One edge of the line segment.
	 * @param a1  Another end of the line segment.
	 * @return  True if the path intersects the line segment, false otherwise.
	 */
	public boolean intersects(Point a0, Point a1)
	{
		if(null == this.vertices)
			return false;

		Point bPrev;
		Point bThis = this.vertices[0];
		for(int i = 1; i<this.vertices.length; i++)
		{
			bPrev = bThis;
			bThis = this.vertices[i];
			if(null != Point.intersection(a0,a1,bPrev,bThis))
				return true;
		}

		return false;
	}
	/**
	 * This method should be called after the vertices are set or changed.  If the vertices are changed and this
	 * method is not called, then subsequent calls to methods of this instance may yield inaccurate results.
	 */
	public void onVerticesSet()
	{
		this.pathLength = 0.0f;
		this.tiny = 0.0f;

		if(null == this.vertices || this.vertices.length<1)
			return;

		Point delta = new Point();
		Point pLast = null;
		Point p = this.vertices[0];
		this.boundingRect = new Rect(p.x,p.x,p.y,p.y);
		for(int i = 1; i<this.vertices.length; i++)
		{
			pLast = p;
			p = this.vertices[i];
			delta.x = p.x -pLast.x;
			delta.y = p.y -pLast.y;
			this.pathLength += (float)Math.sqrt(delta.x *delta.x + delta.y *delta.y);
			this.boundingRect.left = Math.min(this.boundingRect.left, p.x);
			this.boundingRect.right = Math.max(this.boundingRect.right, p.x);
			this.boundingRect.bottom = Math.min(this.boundingRect.bottom, p.y);
			this.boundingRect.top = Math.max(this.boundingRect.top, p.y);
		}
		this.tiny = (1e-6f / Math.min(this.boundingRect.width(),this.boundingRect.height()));
	}
	/**
	 * The bounding rectangle.  Calculated and stored internally by the function this.onVerticesSet().
	 */
	protected Rect boundingRect = null;
	/**
	 * The length of the path.  Calculated and stored internally by the function this.onVerticesSet().
	 */
	protected Float pathLength = null;
	/**
	 * a value that is nearly zero relative to the size of the polygon, but larger than zero.  Calculated by the function this.onVerticesSet().
	 */
	protected Float tiny = null;
	/**
	 * Gets the smallest rectangle surrounding this path.
	 *
	 * CAUTION:  This method will only return the correct result if this.onVerticesSet() was called after the last alteration
	 * of polygon vertices.
	 *
	 * @return The smallest bounding rectangle.
	 */
	public Rect getBoundingRect()
	{
		if(this.boundingRect==null)
			this.onVerticesSet();
		return this.boundingRect;
	}
}