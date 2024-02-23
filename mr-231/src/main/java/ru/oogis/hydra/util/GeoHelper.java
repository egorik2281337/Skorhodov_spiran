package ru.oogis.hydra.util;

public class GeoHelper
{
	static final int WGS84_RADIUS = 6378137;
	static final double KNOT_PMS = 0.514444444;

	public static double calcCourse(double p_lat1, double p_lon1, double p_lat2,
			double p_lon2)
	{
		double a_lat1 = Math.toRadians(p_lat1);
		double a_lon1 = Math.toRadians(p_lon1);
		double a_lat2 = Math.toRadians(p_lat2);
		double a_lon2 = Math.toRadians(p_lon2);
		double a_cosLat1 = Math.cos(a_lat1);
		double a_cosLat2 = Math.cos(a_lat2);
		double a_sinLat1 = Math.sin(a_lat1);
		double a_sinLat2 = Math.sin(a_lat2);
		double a_lonDelta = a_lon2 - a_lon1;
		double a_cosLonDelta = Math.cos(a_lonDelta);
		double a_sinLonDelta = Math.sin(a_lonDelta);
		double a_x =
				(a_cosLat1 * a_sinLat2) - (a_sinLat1 * a_cosLat2 * a_cosLonDelta);
		double a_y = a_sinLonDelta * a_cosLat2;
		double a_z = Math.toDegrees(Math.atan(-a_y / a_x));
		if (a_x < 0)
		{
			a_z += 180.0;
		}
		double a_z2 = (a_z + 180.0) % 360.0 - 180.0;
		a_z2 = -Math.toRadians(a_z2);
		double a_angleInRad2 =
				a_z2 - ((2 * Math.PI) * Math.floor((a_z2 / (2 * Math.PI))));
		double a_result = Math.toDegrees(a_angleInRad2);
		return a_result;
	}

	public static double calcDistanceInMeters(double p_lat1, double p_lon1,
			double p_lat2, double p_lon2)
	{
		double a_lat1 = Math.toRadians(p_lat1);
		double a_lon1 = Math.toRadians(p_lon1);
		double a_lat2 = Math.toRadians(p_lat2);
		double a_lon2 = Math.toRadians(p_lon2);
		// double a_cosLat1 = Math.cos(a_lat1);
		// double a_cosLat2 = Math.cos(a_lat2);
		// double a_sinLat1 = Math.sin(a_lat1);
		// double a_sinLat2 = Math.sin(a_lat2);
		// double a_lonDelta = a_lon2 - a_lon1;
		// double a_cosLonDelta = Math.cos(a_lonDelta);
		// double a_sinLonDelta = Math.sin(a_lonDelta);
		// double a_y =
		// Math.sqrt(Math.pow(a_cosLat2 * a_sinLonDelta, 2)
		// + Math.pow(a_cosLat1 * a_sinLat2 - a_sinLat1 * a_cosLat2
		// * a_cosLonDelta, 2));
		// double a_x = a_sinLat1 * a_sinLat2 + a_cosLat1 * a_cosLat2 *
		// a_cosLonDelta;
		// double a_distance = Math.atan2(a_y, a_x);
		// double a_result = a_distance * WGS84_RADIUS;
		// return Math.abs(a_result);
		double a_result =
				Math.acos(Math.sin(a_lat1) * Math.sin(a_lat2) + Math.cos(a_lat1)
						* Math.cos(a_lat2) * Math.cos(a_lon2 - a_lon1))
						* WGS84_RADIUS;
		System.out.println(a_result);
		return a_result;
	}

	public static String convertAngleToDMS(double p_value, String p_rumb)
	{
		StringBuilder a_builder = new StringBuilder();
		long a_value = Math.round(p_value * 360000);
		a_builder.append(String.format("%02d", a_value / 360000l));
		a_value -= (a_value / 360000l) * 360000l;
		a_builder.append(String.format("%02d", a_value / 6000l));
		a_value -= (a_value / 6000l) * 6000l;
		a_builder.append(String.format("%02d", a_value / 100l));
		a_value -= (a_value / 100l) * 100l;
		a_builder.append(String.format("%02d", a_value));
		a_builder.append(p_rumb);
		return a_builder.toString();
	}

	public static Double convertDMSToAngle(int p_degree, int p_min, double p_sec)
	{
		return p_degree + p_min / 60.0 + p_sec / 3600.0;
	}

	public static String convertLatitudeToGMS(Double p_value)
	{
		if (p_value != null)
		{
			double a_lat = normalizeLatitude(p_value);
			String a_rumb = a_lat < 0 ? "S" : "N";
			a_lat = Math.abs(a_lat);
			return convertAngleToDMS(a_lat, a_rumb);
		}
		return "";
	}

	public static String convertLongitudeToDMS(Double p_value)
	{
		if (p_value != null)
		{
			double a_lon = normalizeLongitude(p_value);
			String a_rumb = a_lon < 0 ? "W" : "E";
			a_lon = Math.abs(a_lon);
			return convertAngleToDMS(a_lon, a_rumb);
		}
		return "";
	}

	public static double knots2mps(double p_speed)
	{
		return p_speed * KNOT_PMS;
	}

	public static Double normalizeAngle(Double p_value)
	{
		if (p_value != null)
		{
			int a_sign = p_value < 0 ? -1 : 1;
			double a_value = Math.abs(p_value);
			int a_mod = (int) (a_value / 360);
			return a_sign * (a_value - a_mod * 360);
		}
		return p_value;
	}

	public static Double normalizeLatitude(Double p_value)
	{
		if (p_value != null)
		{
			double a_value = Math.abs(normalizeAngle(p_value));
			int a_sign = p_value < 0 ? -1 : 1;
			if (a_value > 90 && a_value <= 270)
			{
				a_value = 180 - a_value;
			}
			if (a_value > 270 && a_value <= 360)
			{
				a_value -= 360;
			}
			return a_sign * a_value;
		}
		return p_value;
	}

	public static Double normalizeLongitude(Double p_value)
	{
		if (p_value != null)
		{
			double a_value = Math.abs(normalizeAngle(p_value));
			int a_sign = p_value < 0 ? -1 : 1;
			if (a_value > 180 && a_value <= 360)
			{
				a_value -= 360;
			}
			return a_sign * a_value;
		}
		return p_value;
	}

	public static double radiansToMetersDistanceWGS84(double p_distanceInRadian)
	{
		return p_distanceInRadian * (Math.PI / 180) * WGS84_RADIUS;
	}
}
