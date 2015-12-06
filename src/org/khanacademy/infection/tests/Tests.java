package org.khanacademy.infection.tests;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;
import org.khanacademy.infection.SubsetSum;

public class Tests {

	// Use a fixed seed for reproducability
	private final static Random rand = new Random(1234);
	
	@Test
	public void testSubsetSum() {
		for (int i = 0; i < 100; i++) {
			int[] array = randomArray(-100, 100, 20);
			
			int threshold = rand.nextInt(20) + 1;
			int offset = rand.nextInt(threshold) * (rand.nextInt(2) * 2 - 1);
			
			double p = rand.nextDouble();
			int realSum = 0;
			for (int j = 0; j < array.length; j++) {
				if (rand.nextDouble() < p) realSum += array[j]; 
			}
			verifySubsetSum(array, realSum, 0);
			verifySubsetSum(array, realSum + offset, threshold);
			
			int fakeSum = rand.nextInt(301) + - 150;
			verifySubsetSum(array, fakeSum, 0);
			verifySubsetSum(array, fakeSum + offset, threshold);
			
		}
	}
	
	private void verifySubsetSum(int[] array, int realSum, int threshold) {
		int[] subset = SubsetSum.subsetSum(array, realSum, threshold);
		if (subset == null) return;
		
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
	
}
