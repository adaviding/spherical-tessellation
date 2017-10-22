package com.github.adaviding.numerics.sphere;

import com.github.adaviding.numerics.D;

/**
 * Represents a spherical cap.  We use this to represent a circular area on the surface of a sphere.
 * http://en.wikipedia.org/wiki/Spherical_cap
 */
public class Cap
{
    /**
     * The location of the tip of the cap on the reference sphere.  This is the center of the cap.  The default is null.
     */
    public LatLon position;
    /**
     * The radius of the dome of the cap, measured "as the crow flies" in units of degrees on the surface of the reference
     * sphere.  This is not the same as the radius of the base (which is a disc beneath the surface of the sphere).
     *
     * If your goal is to compute distance on the Earth's surface, use one of the following formulas.
     *
     * 		[distance in Kilometers] 	= this.domeRadius * D.RadiansPerDegree * D.EarthRadiusKilometers
     *		[distance in Miles]         = this.domeRadius * D.RadiansPerDegree * D.EarthRadiusMiles
     */
    public float domeRadius;
    public Cap() { }
    public Cap(LatLon position, float domeRadius)
    {
        this.position = position;
        this.domeRadius = domeRadius;
    }
    /**
     * Expands the spherical cap to include the specified coordinate (if necessary).
     * @param p The coordinate that must be included in the spherical cap.
     */
    public void expandToInclude(LatLon p)
    {
        if(p!=null)
        {
            if (this.position == null)
            {
                this.position = new LatLon(p.lat, p.lon);
                if (Float.isNaN(this.domeRadius))
                    this.domeRadius = 0.0f;
                else if(this.domeRadius<0.0f)
                    this.domeRadius = 0.0f;
            }
            else
            {
                float dist = (float)LatLon.distRadians(this.position, p);
                if (Float.isNaN(this.domeRadius))
                    this.domeRadius = dist;
                else if(dist > this.domeRadius)
                    this.domeRadius = dist;
            }
        }
    }
    /**
     * The signed distance of the coordinate within the Cap.  Signed distance is positive if the coordinate is inside
     * the cap, and negative if the coordinate is outside the cap.  Distance is measured "as the crow flies" along the
     * surface of the sphere.
     * @param x The coordinate.
     * @return The distance (in degrees) between the specified coordinate and the cap.  distance is negative if the
     * coordinate is inside the cap, positive otherwise.
     */
    public double signDistRadians(LatLon x)
    {
        if (null == x || null == this.position || Float.isNaN(this.domeRadius))
            return Float.NaN;
        return LatLon.distRadians(x, this.position) - this.domeRadius * D.RadiansPerDegree;
    }
}