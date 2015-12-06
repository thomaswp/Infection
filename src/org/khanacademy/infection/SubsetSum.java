package org.khanacademy.infection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for solving and approximating the Subset Sum problem
 */
public class SubsetSum {

	public final static Comparator<ICountable> COUNTABLE_COMPARATOR = 
			new Comparator<ICountable>() {
		@Override
		public int compare(ICountable o1, ICountable o2) {
			return -Integer.compare(o1.size(), o2.size());
		}
	};
	
	/**
	 * Approximates the {@link SubsetSum#subsetSum(Collection, int, int)} algorithm with threshold
	 * equal to infinity. This function will always return a subset with a sum <= targetSum. 
	 * <b>Note</b>: This is an approximation, and may not return the true subset
	 * with the closest sum. However, it runs in polynomial time.
	 * @param set The set from which to draw the subset
	 * @param targetSum The target sum of the subet
	 * @return The subet
	 */
	public static <T extends ICountable> List<T> subsetSumApproximate(Collection<T> set, int targetSum) {
		List<T> subset = new ArrayList<>();
		
		// Sort from larget to smallest
		ArrayList<T> list = new ArrayList<>(set);
		Collections.sort(list, COUNTABLE_COMPARATOR);
		
		// Include all items that fit in our remaining space
		int sum = 0;
		for (T t : list) {
			int size = t.size();
			if (sum + size <= targetSum) {
				subset.add(t);
				sum += size;
			}
		}
		
		// Return the subset
		return subset;
	}
	
	/**
	 * Interface for items with a size, which can be subset to to sum to a specific
	 * total size.
	 */
	public interface ICountable {
		int size();
	}

	/**
	 * Runs the {@link SubsetSum#subsetSum(int[], int, int)} algorithm using the sizes of the
	 * given {@link ICountable} items and returns the subset calculated, or null upon failure.
	 */
	public static <T extends ICountable> List<T> subsetSum(Collection<T> set, int n, int threshold) {
		
		int length = set.size(); 
				
		ArrayList<T> list = new ArrayList<>(set);
		if (set.size() == 0) return list;
		
		int[] sizes = new int[length];
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = list.get(i).size();
		}
		
		int[] subsetSum = subsetSum(sizes, n, threshold);
		if (subsetSum == null) return null;
		
		List<T> subset = new ArrayList<>();
		for (int i : subsetSum) subset.add(list.get(i));
		
		return subset;
	}
	
	/**
	 * Calculates a subset of the provided integers which sum to n,
	 * or as close to n as possible while remaining within threshold of n,
	 * or returns null if this is not possible. The returned array is
	 * guaranteed to sum to m, where (n - threshold <= m <= n + threshold).
	 * 
	 * This algorithm uses a dynamic programming solution which runs in
	 * polynomial time in the <i>range</i> of items (i.e. max(items) - min(items)), 
	 * but not polynomial in the number of items.
	 * 
	 * Adapted from https://en.wikipedia.org/wiki/Subset_sum_problem#Pseudo-polynomial_time_dynamic_programming_solution
	 * 
	 * @param items The items to subset
	 * @param n The target sum of the subset
	 * @param threshold A margin of error for n
	 * @return The <b>indices</b> of the subset
	 */
	public static int[] subsetSum(int[] items, int n, int threshold) {
		
		int length = items.length;
		if (length == 0) {
			return Math.abs(n) <= threshold ? new int[0] : null;
		}
		
		// We're going to create an array of solutions to the question
		// Can the first x items of the array sum to y? and store them.
		
		// Calculate the sum of all positive and negative items in the array
		// to determine the bounds of our DP array
		int sumPositive = 0;
		int sumNegative = 0;
		for (int i = 0; i < length; i++) {
			int v = items[i];
			if (v > 0) sumPositive += v;
			else sumNegative += v;
		}
		
		// We don't need to know about sums greater than n + threshold
		if (sumPositive > n + threshold) sumPositive = n;
		// And if we can't make a sum greater than n - threshold, we can stop
		if (sumPositive < n - threshold) return null;
		
		// Our array will contains all solutions from sumNegative to sumPositive
		int width = sumPositive - sumNegative + 1;
		if (width <= 0) return null;
		
		// Make the array and initialize it
		boolean[][] sumArray = new boolean[length][width]; 
		for (int j = 0; j < width; j++) {
			// For x == 0, a(x,y) is true if y == items[0]
			// Here y = j + sumNegative, since j is a 0-based array index
			sumArray[0][j] = items[0] == j + sumNegative;
		}
		
		for (int i = 1; i < length; i++) {
			for (int j = 0; j < width; j++) {
				// Again, a(x,y) corresponds to sumArray[i, j + sumNegative]
				// There are three ways a(x,y) can be true:
				if (sumArray[i-1][j] || items[i] == j + sumNegative) {
					// If a(x-1,y) is true, or items[x] == y
					sumArray[i][j] = true;
				} else {
					// Or if a(x-1, y-items[x]) is true
					int x = j - items[i];
					if (x >= 0 && x < width && sumArray[i-1][x]) {
						sumArray[i][j] = true;
					}
				}
			}
		}
		
		// Optionally print the array for debugging
//		for (int i = 0; i < width; i++) {
//			for (int j = 0; j < length; j++) {
//				System.out.print(sumArray[j][i] ? "1 " : "0 ");
//			}
//			System.out.println();
//		}
		
		// Now we trace back through to find the solution
		
		// First find the closest sum to n, within threshold,
		// that we were able to achieve
		int r = length - 1, c = n - sumNegative;
		boolean found = false;
		for (int i = 0; i <= threshold; i++) {
			c = n - sumNegative + i;
			if (c < width && sumArray[r][c]) {
				found = true;
				break;
			}
			c = n - sumNegative - i;
			if (c >= 0 && c < width && sumArray[r][c]) {
				found = true;
				break;
			}
		}
		if (!found) return null;

		// Then work back through the array to find the subset
		// that created that sum
		List<Integer> indices = new ArrayList<>();
		while (r >= 0 && c < width && sumArray[r][c]) {
			while (r > 0 && sumArray[r-1][c]) r--;
			if (items[r] == 0) break;
			indices.add(r);
			c -= items[r];
			r--;
		}
		
		// Convert it to indices (guaranteeing an actual subset)
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) indicesArray[indicesArray.length - 1 - i] = indices.get(i);
		return indicesArray;
	}
	
}
