package mc.Mitchellbrine.traitorsAndMiners;

import java.util.Random;

@SuppressWarnings("unused")
public class MathHelper {

	public static Random random(long seed) {
		return new Random(seed);
	}
	
	public static int nextInt(long seed, int maxNum) {
		Random random = new Random(seed);
		return random.nextInt(maxNum);
	}
	
	public static int nextInt(long seed) {
		Random random = new Random(seed);
		return random.nextInt();
	}
	
	public static double nextDouble(long seed) {
		Random random = new Random(seed);
		return random.nextDouble();
	}
	
	public static long nextLong(long seed) {
		Random random = new Random(seed);
		return random.nextLong();
	}
	
}
