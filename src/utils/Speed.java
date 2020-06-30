package utils;

public class Speed {

	public static double distanceToAchieveSpeed(double speed1, double speed2, double acceleration) {
		acceleration = Math.abs(acceleration);
		double time = Math.abs(speed2-speed1)/acceleration;
		if( speed1 < speed2 )
			return distanceMRUA(0, speed1, acceleration, time);
		else 
			return distanceMRUA(0, speed1, - acceleration, time);
	}
	
	public static double distanceMRUA(double startPosition, double startSpeed, double startAcceleration, double time) {
		return startPosition + startSpeed*time + (1/2)*startAcceleration*time*time;
	}
	
}
