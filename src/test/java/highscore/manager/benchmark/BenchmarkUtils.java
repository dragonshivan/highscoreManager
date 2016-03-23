package highscore.manager.benchmark;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BenchmarkUtils {
	
	private static final Random RANDOM = new Random();
	
	public static int randomInt(int max) {
		return RANDOM.nextInt(max);
	}
	
	public static int randomInt() {
		return RANDOM.nextInt(Integer.MAX_VALUE);
	}
	
	public static long randomLong() {
		return RANDOM.nextLong();
	}
	
	public static int randomInteger() {
		return RANDOM.nextInt(Integer.MAX_VALUE);
	}
	
	public static Long randomLongObject() {
		return RANDOM.nextLong();
	}
	
	public static int randomConcurrentInt() {
		return ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
	}
	
	public static double getUsedHeapMb() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) /(double) (1024 * 1024);
	}
}
