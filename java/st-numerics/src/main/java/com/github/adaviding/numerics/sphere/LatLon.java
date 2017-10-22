package com.github.adaviding.numerics.sphere;

import com.github.adaviding.numerics.D;
import com.github.adaviding.numerics.F;
import com.github.adaviding.numerics.f3.Matrix;
import com.github.adaviding.numerics.f3.Point;

import javax.validation.constraints.NotNull;

/**
 * A coordinate on the surface of a 3-ball.  Latitude and longitude are expressed in degrees.
 * Elevation is not represented.
 *
 * http://en.wikipedia.org/wiki/Geographic_coordinate_system#Geographic_latitude_and_longitude
 * http://en.wikipedia.org/wiki/Ball_(mathematics)
 */
public class LatLon {
    /**
     * The latitude in the range of [-90,90] degrees.
     */
    public float lat;
    /**
     * The longitude in the range of [-180,180) degrees.
     */
    public float lon;

    public LatLon(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }
    public LatLon(double lat, double lon) {
        this.lat = (float)lat;
        this.lon = (float)lon;
    }
    /**
     * Uses the Haversine method to compute the distance (in radians) between two points "as the crow flies"
     * along the surface of a perfect sphere.
     * @param a One of the coordinates.
     * @param b The other coordinate.
     * @return The length (in radians) of the shortest arc connecting two points on the surface of a sphere.
     */
    public static double distRadians(@NotNull LatLon a, @NotNull LatLon b) {
        double dLon = D.RadiansPerDegree * (b.lon - a.lon);
        double dLat = D.RadiansPerDegree * (b.lat - a.lat);
        double shdlon = Math.sin(0.5 * dLon);
        double shdlat = Math.sin(0.5 * dLat);
        double x = shdlat * shdlat + Math.cos(D.RadiansPerDegree * a.lat) * Math.cos(D.RadiansPerDegree * b.lat) * shdlon * shdlon;
        return 2.0 * Math.atan2(Math.sqrt(x), Math.sqrt(1.0 - x));
    }
    /**
     * Creates a new instance from the point:  {Lat=Y,Lon=X}
     * @return The new instance where {Lat=Y,Lon=X}
     */
    public static LatLon fromPoint(com.github.adaviding.numerics.f2.Point p)
    {
        return new LatLon(p.y,p.x);
    }
    /**
     * Constructs a LatLon from a unit vector (i.e. a point on the surface of a unit sphere).
     *
     *  Here we assume the left-handed coordinate system where...
     *
     *  	x-axis = thumb ... facing rightwards
     *  	y-axis = index finger ... facing upwards
     *  	z-axis = middle finger ... facing forwards
     *
     * 	Here we define (Lat,Lon) as a series of rotations applied to reference unit vector (0,0,-1).
     *
     *  	`lat` is the the first rotation applied in sequence:  a clockwise rotation about x.
     *		`lon` is the second rotation applied in sequence:  a clockwise rotation about y.
     *
     *	As a result, each (Lat,Lon) pair corresponds to a unit vector (ux,uy,uz) on the surface of a sphere.
     *
     *		ux	=  cos(Lat)sin(Lon)
     *		uy	=  sin(Lat)
     *		uz	= -cos(Lat)cos(Lon)
     *
     *		lat	= asin(uy)
     *		lon = atan2(ux,-uz)
     *
     * 	Note that when (Lat,Lon) is (0,0), the unit vector is the unrotated reference (0,0,-1); corresponding to an arrow
     * 	which points directly backwards.
     *
     * @return The LatLon.
     */
    public static LatLon fromUnitVector(Point vec)
    {
        //	Ensure unit vector.
        vec = vec.toUnitVector();

        //	Return output.
        LatLon output = new LatLon
        (
                D.DegreesPerRadian * Math.asin(vec.y),
                D.DegreesPerRadian * Math.atan2(vec.x, -vec.z)
        );
        output.normalize();
        return output;
    }
    /**
     * Determines if the coordiante is empty.  It is empty if either Lat or Lon is NaN.
     * @return True if the coordinate is empty, false otherwise.
     */
    public boolean isEmpty() { return Float.isNaN(this.lat) || Float.isNaN(this.lon); }
    /**
     *  normalize the range of the Lat and Lon.
     */
    public void normalize()
    {
        this.lat = F.normalizeDegreesLatitude(this.lat);
        this.lon = F.normalizeDegrees(this.lon);
    }
    /**
     * Expresses this instance as a point:  {X=Lon,Y=Lat}
     * @return The point where {X=Lon,Y=Lat}.
     */
    public com.github.adaviding.numerics.f2.Point toPoint() { return new com.github.adaviding.numerics.f2.Point(this.lon,this.lat); }
    /**
     * Expresses the LatLon as a rotation matrix which can be multiplied by the right-adjacent vector [0, 0, -1], where
     * the multiplication yields a unit vector equivalent to this.ToUnitVector().
     *
     * @return Returns the rotation matrix, which is equal to the transpose of this.RotationMatrixInverse().  Since
     * rotation matrices are orthonormal, the transpose is also equal to the inverse.
     *
     * @url http://en.wikipedia.org/wiki/Rotation_matrix#In_three_dimensions
     */
    public Matrix toRotationMatrix()
    {
        double rlon = this.lon * D.RadiansPerDegree;
        double rlat = this.lat * D.RadiansPerDegree;

        double cosa = Math.cos(rlon);
        double sina = Math.sin(rlon);
        double cosb = Math.cos(rlat);
        double sinb = Math.sin(rlat);

        Matrix output = new Matrix();

        output.m00 = (float)cosa;
        output.m10 = 0.0f;
        output.m20 = (float)sina;

        output.m01 = (float)(-sina*sinb);
        output.m11 = (float)cosb;
        output.m21 = (float)(cosa*sinb);

        output.m02 = (float)(-sina*cosb);
        output.m12 = (float)(-sinb);
        output.m22 = (float)(cosa*cosb);

        return output;
    }
    /**
     * Expresses the LatLon as a rotation matrix which can be multiplied by the right-adjacent vector this.ToUvec(), where
     * the multiplication yields a unit vector [0, 0, -1].
     *
     * @return Returns the rotation matrix, which is equal to the transpose of this.RotationMatrix().  Since rotation matrices
     * are orthonormal, the transpose is also equal to the inverse.
     *
     * @url http://en.wikipedia.org/wiki/Rotation_matrix#In_three_dimensions
     */
    public Matrix toRotationMatrixInverse()
    {
        double rlon = this.lon * D.RadiansPerDegree;
        double rlat = this.lat * D.RadiansPerDegree;

        double cosa = Math.cos(rlon);
        double sina = Math.sin(rlon);
        double cosb = Math.cos(rlat);
        double sinb = Math.sin(rlat);

        Matrix output = new Matrix();

        output.m00 = (float)cosa;
        output.m01 = 0.0f;
        output.m02 = (float)sina;

        output.m10 = (float)(-sina*sinb);
        output.m11 = (float)cosb;
        output.m12 = (float)(cosa*sinb);

        output.m20 = (float)(-sina*cosb);
        output.m21 = (float)(-sinb);
        output.m22 = (float)(cosa*cosb);

        return output;
    }
    /**
     * Represents the LatLon as a unit vector (i.e. a point on the surface of a unit sphere).
     *
     * Here we assume the left-handed coordinate system where...
     *
     * 	x-axis = thumb ... facing rightwards
     * 	y-axis = index finger ... facing upwards
     * 	z-axis = middle finger ... facing forwards
     *
     *  Here we define (Lat,Lon) as a series of rotations applied to reference unit vector (0,0,-1).
     *
     * 	Lat is the the first rotation applied in sequence:  a clockwise rotation about x.
     * 		Lon is the second rotation applied in sequence:  a clockwise rotation about y.
     *
     * 	As a result, each (Lat,Lon) pair corresponds to a unit vector (ux,uy,uz) on the surface of a sphere.
     *
     * 		ux	=  cos(Lat)sin(Lon)
     * 		uy	=  sin(Lat)
     * 		uz	= -cos(Lat)cos(Lon)
     *
     * 		Lat	= asin(uy)
     * 		Lon = atan2(ux,-uz)
     *
     *  	Note that when (Lat,Lon) is (0,0), the unit vector is the unrotated reference (0,0,-1); corresponding to an arrow
     *  	which points directly backwards.
     *
     * @return The unit vector.
     */
    public Point toUnitVector()
    {
        double lon = D.RadiansPerDegree * this.lon;
        double lat = D.RadiansPerDegree * this.lat;

        double cLon = Math.cos(lon);
        double sLon = Math.sin(lon);

        double cLat = Math.cos(lat);
        double sLat = Math.sin(lat);

        Point output = new Point((float)(cLat * sLon), (float)sLat, (float)(-cLat * cLon));

        return output;
    }
}
