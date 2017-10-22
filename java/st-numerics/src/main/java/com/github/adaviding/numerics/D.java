package com.github.adaviding.numerics;

/**
 * Constants and methods related to 64-bit floating point arithmetic.
 */
public class D {
    /**
     * The number of degrees per radian, 180 / PI.
     */
    public static final double DegreesPerRadian = 57.2957795131;
    /**
     * The radius of Earth in kilometers.
     */
    public static final double EarthRadiusKilometers = 6371;
    /**
     * The radius of Earth in miles.
     */
    public static final double EarthRadiusMiles = 3959;
    /**
     * The mathematical constant PI.
     */
    public static final double Pi = 3.14159265359;
    /**
     * The number of radians per degree, PI / 180.
     */
    public static final double RadiansPerDegree = 0.0174532925199;
    /**
     * The value of 2 * PI
     */
    public static final double TwoPi = 6.28318530718;
    /**
     * Normalizes the range of the input to (-Pi,+Pi].
     * @param r  The argument in radians.
     * @return The argument shifted to its equivalent in the range (-Pi,+Pi].
     */
    public static double normalizeRadians(double r)
    {
        if(r<=-Pi)
            return (-r-Pi)%TwoPi-Pi;
        if(r>Pi)
            return (r+Pi)%TwoPi-Pi;
        return r;
    }
}