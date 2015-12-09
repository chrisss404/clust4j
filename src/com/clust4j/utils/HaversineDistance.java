package com.clust4j.utils;

import org.apache.commons.math3.util.FastMath;

public class HaversineDistance implements GeometricallySeparable {
	public static final int EARTH_RADIUS_KM = 6371;
	public static final int EARTH_RADIUS_MI = 3959;
	public static enum DistanceUnit { MI, KM }
	private final int radius;
	
	
	
	public HaversineDistance() {
		this(DistanceUnit.MI);
	}
	
	public HaversineDistance(DistanceUnit unit) {
		radius = unit.equals(DistanceUnit.MI) ? EARTH_RADIUS_MI : EARTH_RADIUS_KM;
	}

	
	
	@Override
	public double getDistance(double[] a, double[] b) {
		VecUtils.checkDims(a,b);
		
		final int n = a.length;
		if(n != 2)
			throw new IllegalArgumentException("haversine "
				+ "distance can only take arrays of length 2: [lat, long]");
		
		double dLat = FastMath.toRadians(b[0] - a[0]);
		double dLong= FastMath.toRadians(b[1] - a[1]);
		
		double a0 = FastMath.toRadians(a[0]);
		double b0 = FastMath.toRadians(b[0]);
		
		double aPrime = haversine(dLat) + FastMath.cos(a0) * FastMath.cos(b0) * haversine(dLong);
		double c = 2 * FastMath.atan2(FastMath.sqrt(aPrime), FastMath.sqrt(1 - aPrime));
		
		return c * radius;
	}
	
	
	private static double haversine(double val) {
		return FastMath.pow(FastMath.sin(val / 2d), 2);
	}

	
	@Override
	public String getName() {
		return "Haversine";
	}
}