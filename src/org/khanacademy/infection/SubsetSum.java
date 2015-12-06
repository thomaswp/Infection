package org.khanacademy.infection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SubsetSum {

	public final static Comparator<ICountable> COUNTABLE_COMPARATOR = 
			new Comparator<ICountable>() {
		@Override
		public int compare(ICountable o1, ICountable o2) {
			return -Integer.compare(o1.size(), o2.size());
		}
	};
	
	
	public static <T extends ICountable> List<T> subsetSumApproximate(Collection<T> set, int targetSum) {
		List<T> subset = new ArrayList<>();
		
		ArrayList<T> list = new ArrayList<>(set);
		Collections.sort(list, COUNTABLE_COMPARATOR);
		
		int sum = 0;
		for (T t : list) {
			int size = t.size();
			if (sum + size <= targetSum) {
				subset.add(t);
				sum += size;
			}
		}
		
		return subset;
	}
	
	public interface ICountable {
		int size();
	}

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
	
	public static int[] subsetSum(int[] items, int n, int threshold) {
		
		int length = items.length;
		if (length == 0) {
			return Math.abs(n) <= threshold ? new int[0] : null;
		}
		
		int sumPositive = 0;
		int sumNegative = 0;
		for (int i = 0; i < length; i++) {
			int v = items[i];
			if (v > 0) sumPositive += v;
			else sumNegative += v;
		}
		if (sumPositive > n + threshold) sumPositive = n;
		if (sumPositive < n - threshold) return null;
		
		int width = sumPositive - sumNegative + 1;
		if (width <= 0) return null;
		
		boolean[][] sumArray = new boolean[length][width]; 
		for (int i = 0; i < width; i++) {
			sumArray[0][i] = items[0] == i + sumNegative;
		}
		for (int i = 1; i < length; i++) {
			for (int j = 0; j < width; j++) {
				if (sumArray[i-1][j] || items[i] == j + sumNegative) {
					sumArray[i][j] = true;
				} else {
					int x = j - items[i];
					if (x >= 0 && x < width && sumArray[i-1][x]) {
						sumArray[i][j] = true;
					}
				}
			}
		}
		
//		for (int i = 0; i < width; i++) {
//			for (int j = 0; j < length; j++) {
//				System.out.print(sumArray[j][i] ? "1 " : "0 ");
//			}
//			System.out.println();
//		}
		
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

		List<Integer> indices = new ArrayList<>();
		while (r >= 0 && c < width && sumArray[r][c]) {
			while (r > 0 && sumArray[r-1][c]) r--;
			if (items[r] == 0) break;
			indices.add(r);
			c -= items[r];
			r--;
		}
		
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) indicesArray[indicesArray.length - 1 - i] = indices.get(i);
		return indicesArray;
	}
	
}
