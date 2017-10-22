package com.github.adaviding.numerics;

/**
 * Constants and methods for 32-bit floating point numbers.
 */
public class F {
    /**
     * The number of degrees per radian, 180 / PI.
     */
    public static final float DegreesPerRadian = 57.2957795131f;
    /**
     * The radius of Earth in kilometers.
     */
    public static final float EarthRadiusKilometers = 6371f;
    /**
     * The radius of Earth in miles.
     */
    public static final float EarthRadiusMiles = 3959f;
    /**
     * The mathematical constant PI.
     */
    public static final float Pi = 3.14159265359f;
    /**
     * The number of radians per degree, PI / 180.
     */
    public static final float RadiansPerDegree = 0.0174532925199f;
    /**
     * The value of 2 * PI
     */
    public static final float TwoPi = 6.28318530718f;
    /**
     * Normalizes the range of the input to (-180,+180].
     * @param degrees The argument shifted to its equivalent in the range (-180,+180].
     * @return The argument in degrees.
     */
    public static float normalizeDegrees(float degrees)
    {
        if (degrees <= -180f)
            return (-degrees - 180f) % 360f - 180f;
        if (degrees > 180)
            return (degrees + 180f) % 360f - 180f;
        return degrees;
    }
    /**
     * Normalizes the range of the input to [-90,+90].
     * @param degrees The argument in degrees latitude.
     * @return The argument shifted to its equivalent in the range [-90,+90].
     */
    public static float normalizeDegreesLatitude(float degrees)
    {
        degrees = F.normalizeDegrees(degrees);
        if (degrees <= -90f)
            return -180f - degrees;
        if (degrees > 90f)
            return 180f - degrees;
        return degrees;
    }
    /**
     * Normalizes the range of the input to [0,360).
     * @param degrees The argument shifted to its equivalent in the range [0,360).
     * @return The argument in degrees.
     */
    public static float normalizeDegreesPos(float degrees)
    {
        if (degrees < 0)
            return 360 - ((-degrees) % 360);
        if (degrees >= 360)
            return degrees % 360;
        return degrees;
    }
    /**
     * Normalizes the range of the input to (-Pi,+Pi].
     * @param r The argument in radians.
     * @return The argument shifted to its equivalent in the range (-Pi,+Pi].
     */
    public static float normalizeRadians(float r)
    {
        if (r <= -Pi)
            return (-r - Pi) % TwoPi - Pi;
        if (r > Pi)
            return (r + Pi) % TwoPi - Pi;
        return r;
    }
    /**
     * Normalizes the range of the input to [0,2*Pi).
     * @param r The argument in radians.
     * @return The argument shifted to its equivalent in the range [0,2*Pi).
     */
    public static float normalizeRadiansPos(float r)
    {
        if (r < 0)
            return TwoPi - ((-r) % TwoPi);
        if (r >= TwoPi)
            return r % TwoPi;
        return r;
    }
    public static int sign(float x) {
        if (x<0f)
            return -1;
        if (x>0f)
            return 1;
        return 0;
    }
    /**
     * Tests to see whether the two values are equal within the tolerance specified.
     * @param a  The first value.
     * @param b  The second value.
     * @param tol  The tolerance.  Typically a tiny, positive number.
     * @return  True if the values are equal or nearly equal within the specified tolerance, false otherwise.
     */
    public static boolean testEqual(float a, float b, float tol)
    {
        if(a==b)
            return true;
        return Math.abs(a-b)<tol;
    }
    /**
     * Parses a floating point number from a string.  Returns NaN if the parse failed.
     * @param s The string to be parsed.
     */
    public static float tryParse(String s)
    {
        try { return Float.parseFloat(s); }
        catch(Exception x) {}
        return Float.NaN;
    }
}
