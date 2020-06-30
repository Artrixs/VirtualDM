package virtualdm;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * Mainly static methods to handle the Game Clock
 * @author Arturo Misino
 *
 */
public class Clock {

	public static enum Day {MONDAY, TUESDAY, WEDNESNDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
		private static Day[] values = values();
		public Day next() {
			return values[(this.ordinal() + 1) % values.length];
		}
	}
	
	private static Day day;
	private static LocalTime time;
	
	private static int timeCompression = 1;
	private static final int MAX_TIMECOMPRESSION = 120;
	
	private static long delta;
	private static long lastTickTime;
	private static boolean running;
		
	
	public static void set(Day dd, int hh, int mm, int ss) {
		time = LocalTime.of(hh, mm, ss);
		day = Day.MONDAY;
		running = false;
		day = dd;
	}
	
	public static void set(int hh, int mm, int ss) {
		set(Day.MONDAY,hh,mm,ss);
	}
	
	public static void start() {
		lastTickTime = getCurrentTime();
		running = true;
	}
	
	public static void stop() {
		running = false;
		
	}
	
	public static boolean isRunning() { return running; }
	
	public static void setTimeCompression( int time ) {
		if( time > MAX_TIMECOMPRESSION)
			time = MAX_TIMECOMPRESSION;
		
		if( time < 1 )
			time = 1;
		
		timeCompression = time;
	}
		
	public static void tick() {
		if( !running ) {
			return;
		}
		
		long currentTickTime = getCurrentTime();	
		delta = currentTickTime - lastTickTime;
		
		lastTickTime = currentTickTime;
		delta = delta * timeCompression;
		var oldTime = time;
		time = time.plusNanos(delta * 1000000);
		if( oldTime.compareTo(time) > 0) {
			day = day.next();
		}
	}
	
	public static double getDelta() {
		if ( !running ) {
			return 0;
		}
		return delta / 1000.0;
	}
	
	public static String string() {
		return day + " " + time.format( DateTimeFormatter.ISO_LOCAL_TIME );
	}
	
	private static long getCurrentTime() {
		 return System.currentTimeMillis();
	}
	
	
	/* Old stuff just for reference
	 * 
	 * //private static final int MAX_TPS = 60;
	//private static int throttleTPS = MAX_TPS;
	private static void increaseThrottleTPS() {
		if(throttleTPS < MAX_TPS) {
			throttleTPS++;
		}
	}
	
	private static void decreaseThrottleTPS() {
		if(throttleTPS > 0) {
			throttleTPS--;
			//Force time compression to respect a throttle to avoid a tick with a delta > 1s
			timeCompression = timeCompression < throttleTPS ? timeCompression : throttleTPS;
		}
	}
	
	
	
	//FIXME: int he future oldtick should be trashed
	public static void oldtick() {
		if( !running ) {
			return;
		}
		
		long currentTickTime = getCurrentTime();
		delta = currentTickTime - lastTickTime;
		long millisecondPerTick = 1000/throttleTPS;
		
		if( delta < millisecondPerTick ) {
			try {
				Thread.sleep(millisecondPerTick - delta);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentTickTime = getCurrentTime();
			increaseThrottleTPS();
		} else {
			decreaseThrottleTPS();
		}
		
		delta = currentTickTime - lastTickTime;
		lastTickTime = currentTickTime;
		delta = delta * timeCompression;
		var oldTime = time;
		time = time.plusNanos(delta * 1000000);
		if( oldTime.compareTo(time) > 0) {
			day = day.next();
		}
	}*/
	
}



