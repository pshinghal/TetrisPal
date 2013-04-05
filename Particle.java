import java.util.Random;

public class Particle {
	// Number of runs averaged to get the fitness
	private static final int NUM_RUNS = 16;
	private static final int INIT_FITNESS = 0;

	public static final int DIMENSIONS = 6;

	public static double[] MAX;
	public static double[] MIN;
	public static double INERTIA_WEIGHT;
	public static double COGNITIVE_WEIGHT;
	public static double SOCIAL_WEIGHT;
	public static double[] gBest;
	public static int gBestFitness;

	private double[] position;
	private double[] velocity;
	private int fitness;
	private Random random;

	private double[] pBest;
	private int pBestFitness;

	private double getRandomInRange(double min, double max) {
		return min + (random.nextDouble() * (max - min));
	}

	private int getOneFitness() {
		PlayerSkeleton p = new PlayerSkeleton(position);
		return p.getRowsCleared();
	}

	// TODO: Parallelise
	// TODO: Something other than average?
	private void updateFitness() {
		int sum = 0;
		for (int i = 0; i < NUM_RUNS; i++) {
			sum += getOneFitness();
		}
		fitness = sum / NUM_RUNS;
	}

	private void updatePBest() {
		if (fitness > pBestFitness) {
			System.arraycopy(position, 0, pBest, 0, position.length);
			pBestFitness = fitness;
		}
	}

	private void updateGBest() {
		if (fitness > gBestFitness) {
			System.arraycopy(position, 0, gBest, 0, position.length);
			gBestFitness = fitness;
		}
	}

	private void updateVelocity() {
		double rand1 = random.nextDouble();
		double rand2 = random.nextDouble();
		double[] first = VectorMath.scale(velocity, INERTIA_WEIGHT);
		double[] second = VectorMath.scale(VectorMath.subtract(pBest, position), rand1 * COGNITIVE_WEIGHT);
		double[] third = VectorMath.scale(VectorMath.subtract(gBest, position), rand2 * SOCIAL_WEIGHT);
		velocity = VectorMath.add(VectorMath.add(first, second), third);
	}

	private void updatePosition() {
		position = VectorMath.add(position, velocity);
		//TODO: Truncate at MAX and MIN?
	}

	private void initializePosition() {
		position = new double[DIMENSIONS];
		for (int i = 0; i < DIMENSIONS; i++) {
			position[i] = getRandomInRange(MIN[i], MAX[i]);
		}
	}

	private void initializePBest() {
		pBest = new double[DIMENSIONS];
		for (int i = 0; i < DIMENSIONS; i++) {
			pBest[i] = 0;
		}
	}

	private void initializePosition(double[] initPos) {
		position = new double[DIMENSIONS];
		System.arraycopy(initPos, 0, position, 0, initPos.length);
	}

	private void initializeVelocity() {
		velocity = new double[DIMENSIONS];
	}

	private void initializeFitness() {
		fitness = 0;
	}

	public Particle() {
		random = new Random();
		initializePosition();
		initializeVelocity();
		initializeFitness();
		initializePBest();
	}

	public Particle(double[] initPos) {
		random = new Random();
		initializePosition(initPos);
		initializeVelocity();
		initializeFitness();
		initializePBest();
	}

	public void update() {
		updateFitness();
		updatePBest();
		updateGBest();
		updatePosition();
		updateVelocity();
	}

	public double[] getPosition() {
		return position;
	}

	public double[] getVelocity() {
		return velocity;
	}

	public int getFitness() {
		return fitness;
	}

	public double[] getPBest() {
		return pBest;
	}

	public int getPBestFitness() {
		return pBestFitness;
	}
}
