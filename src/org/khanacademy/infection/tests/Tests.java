package org.khanacademy.infection.tests;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;

import org.junit.Test;
import org.khanacademy.infection.SubsetSum;

public class Tests {

	@Test
	public void testSubsetSum() {
		int[] items = new int[] {
			7, 0, 4, 1, 0, 1	
		};
		int[] subset = SubsetSum.subsetSum(items, 12);
		System.out.println(Arrays.toString(subset));
		assertArrayEquals(subset, new int[] {0, 2, 3});
	}
	
}
