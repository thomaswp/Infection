package org.khanacademy.infection.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;
import org.khanacademy.infection.Infection;
import org.khanacademy.infection.Population;
import org.khanacademy.infection.SubsetSum;
import org.khanacademy.infection.User;

public class Tests {

	// Use a fixed seed for reproducability
	private final static Random rand = new Random(1234);

	@Test
	public void testConnections() {
		Population pop = new Population();

		User a = pop.createUser("A");
		User b = pop.createUser("B");
		User c = pop.createUser("C");
		User d = pop.createUser("D");

		// Test a connection
		assertNotEquals(a.getInfection(), b.getInfection());
		User.addCoach(a, b);
		assertEquals(a.getInfection(), b.getInfection());

		// Test transitivity
		User.addCoach(b, c);
		assertEquals(a.getInfection(), c.getInfection());

		// Test no loops
		assertTrue(!User.addCoach(d, d));

		// Test no duplicates, but do allow cycles
		assertTrue(!User.addCoach(a, b));
		assertTrue(User.addCoach(b, a));

		// Test the other direction
		User.addCoach(d, b);
		assertEquals(a.getInfection(), d.getInfection());

		// Test infection spread
		a.infect("A");
		assertTrue(d.hasCondition("A"));
	}

	@Test
	public void testDisconnections() {
		Population pop = new Population();

		User a = pop.createUser("A");
		User b = pop.createUser("B");
		User c = pop.createUser("C");
		User d = pop.createUser("D");

		User.addCoach(a, b);
		User.addCoach(b, c);
		User.addCoach(b, d);

		// Test that they start the same
		assertEquals(a.getInfection(), d.getInfection());
		a.infect("A");
		assertTrue(d.hasCondition("A"));

		// Test infection split, but that conditions remain
		User.removeCoach(a, b);
		assertNotEquals(a.getInfection(), b.getInfection());
		assertEquals(b.getInfection(), c.getInfection());
		assertEquals(b.getInfection(), d.getInfection());
		assertTrue(d.hasCondition("A"));

		// Make sure infections don't spread from a to d
		a.infect("B");
		assertTrue(!d.hasCondition("B"));

		// But do spread from b to d
		b.infect("C");
		assertTrue(!a.hasCondition("C"));
		assertTrue(d.hasCondition("C"));
	}

	@Test
	public void testSubsetSum() {
		// Randomized trials
		for (int i = 0; i < 100; i++) {
			// Createa a random array
			int[] array = randomArray(-100, 100, 20);

			// Choose a random threshold and an offset where -threshold < offset < threshold
			int threshold = rand.nextInt(20) + 1;
			int offset = rand.nextInt(threshold) * (rand.nextInt(2) * 2 - 1);

			// Choose a random sum we know can be done
			double p = rand.nextDouble();
			int realSum = 0;
			for (int j = 0; j < array.length; j++) {
				if (rand.nextDouble() < p) realSum += array[j]; 
			}
			// And verify that it can
			verifySubsetSum(array, realSum, 0);
			// Optionally with an offset
			verifySubsetSum(array, realSum + offset, threshold);

			// Then choose a random sum and do the same
			int fakeSum = rand.nextInt(301) + - 150;
			verifySubsetSum(array, fakeSum, 0);
			verifySubsetSum(array, fakeSum + offset, threshold);

		}
	}

	private void verifySubsetSum(int[] array, int realSum, int threshold) {
		int[] subset = SubsetSum.subsetSum(array, realSum, threshold);

		// If the subset fails, we can't say for sure anything's wrong
		if (subset == null) return;

		// If it succeeds though, we can ensure it does in fact sum
		int sum = 0;
		for (int i = 0; i < subset.length; i++) {
			sum += array[subset[i]];
		}

		assertTrue(Math.abs(sum - realSum) <= threshold);
	}

	private static int[] randomArray(int min, int max, int n) {
		int[] array = new int[n];
		for (int i = 0; i < n; i++) {
			array[i] = min + rand.nextInt(max - min + 1);
		}
		return array;
	}

	@Test
	public void testLimitedInfection() {
		// Random trials
		for (int r = 0; r < 100; r++) {
			Population pop = new Population();

			// Create some randomly size infection groups 
			double p = rand.nextDouble();
			int targetSum = 0;
			for (int i = 0; i < 20; i++) {
				int size = rand.nextInt(200);
				createInfectionGroup(pop, size);

				// Choose some to calculate a doable sum
				if (rand.nextDouble() < p) targetSum += size;
			}

			// Create a random threshold and offset
			int threshold = rand.nextInt(20) + 1;
			int offset = rand.nextInt(threshold) * (rand.nextInt(2) * 2 - 1);

			// Test the targetSum
			verifyLimitedInfection(pop, targetSum, 0);
			verifyLimitedInfection(pop, targetSum + offset, threshold);

			// Test a fake sum
			int fakeSum = rand.nextInt(500) + 250;
			verifyLimitedInfection(pop, fakeSum, 0);
			verifyLimitedInfection(pop, fakeSum + offset, threshold);
		}
	}

	private static void verifyLimitedInfection(Population pop, int targetSum, int threshold) {
		// Do the limited infection
		String condition = "A" + rand.nextDouble();
		int infected = pop.limitedInfection(condition, targetSum, threshold);

		// It aught to be within the threshold
		assertTrue(Math.abs(pop.countUsersWithCondition(condition) - infected) <= threshold);

		// Count the inconsistent infections (with mixed conditions)
		int inconsistent = 0;
		for (Infection infection : pop.getInfections()) {
			if (!infection.consistent(condition)) inconsistent++;
		}

		// There should be at most 1
		assertTrue(inconsistent <= 1);
		// If our infected number != targetSum, there should be none inconsistent
		assertTrue(inconsistent == 0 || infected == targetSum);
	}

	@Test
	public void testLimitedInfectionExact() {
		// Random trials
		for (int r = 0; r < 100; r++) {
			Population pop = new Population();

			// Create some randomly size infection groups 
			double p = rand.nextDouble();
			int targetSum = 0;
			for (int i = 0; i < 20; i++) {
				int size = rand.nextInt(200);
				createInfectionGroup(pop, size);

				// Choose some to calculate a doable sum
				if (rand.nextDouble() < p) targetSum += size;
			}

			// Create a random threshold and offset
			int threshold = rand.nextInt(20) + 1;
			int offset = rand.nextInt(threshold) * (rand.nextInt(2) * 2 - 1);

			// Test the targetSum
			verifyLimitedInfectionExact(pop, targetSum, 0);
			verifyLimitedInfectionExact(pop, targetSum + offset, threshold);

			// Test a fake sum
			int fakeSum = rand.nextInt(500) + 250;
			verifyLimitedInfectionExact(pop, fakeSum, 0);
			verifyLimitedInfectionExact(pop, fakeSum + offset, threshold);
		}
	}

	private void verifyLimitedInfectionExact(Population pop, int targetSum, int threshold) {
		// Do the limited exact infection
		String condition = "A" + rand.nextDouble();
		int infected = pop.limitedInfectionExact(condition, targetSum, threshold);

		// If it can't be done, we can't really verify this
		if (infected == -1) return;
		
		// It aught to be within the threshold
		assertTrue(Math.abs(pop.countUsersWithCondition(condition) - infected) <= threshold);

		// Count the inconsistent infections (with mixed conditions)
		int inconsistent = 0;
		for (Infection infection : pop.getInfections()) {
			if (!infection.consistent(condition)) inconsistent++;
		}

		// There should no inconsistencies
		assertTrue(inconsistent == 0);
	}

	private static void createInfectionGroup(Population pop, int size) {
		// Create a chain of users
		User lastUser = null;
		for (int i = 0; i < size; i++) {
			User user = pop.createUser("");
			if (lastUser != null) User.addCoach(lastUser, user);
			lastUser = user;
		}
	}

}
