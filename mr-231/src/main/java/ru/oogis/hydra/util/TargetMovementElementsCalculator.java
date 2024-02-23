package ru.oogis.hydra.util;

import static ru.oogis.hydra.util.GeoHelper.KNOT_PMS;
import static ru.oogis.hydra.util.GeoHelper.WGS84_RADIUS;


public class TargetMovementElementsCalculator
{
	/**
	 * Расчет элементов движения цели по двум позициям
	 * @param p_lat1 широта первой позиции цели
	 * @param p_lon1 долгота первой позиции цели
	 * @param p_lat2 широта второй позиции цели
	 * @param p_lon2 долгота второй позиции цели
	 * @param p_deltaTime время в секундах, за которое цель перешла из первой позиции во вторую
	 * @return объект рассчетных элементов движения цели {@link #TargetMovementElements}
	 */
	public static TargetMovementElements calculate(double p_lat1, double p_lon1, double p_lat2, double p_lon2, long p_deltaTime)
	{
		TargetMovementElements a_result = new TargetMovementElements();
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
		a_result.distance = Math.acos(a_sinLat1 * a_sinLat2 + a_cosLat1
				* a_cosLat2 * a_cosLonDelta) * WGS84_RADIUS;
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
		a_result.course = Math.toDegrees(a_angleInRad2);
		a_result.speed = (a_result.distance / p_deltaTime) / KNOT_PMS;
		return a_result;
	}
}
