package org.khanacademy.infection;

import java.util.ArrayList;
import java.util.Arrays;
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

	public static <T extends ICountable> List<T> subsetSum(Collection<T> set, int n) {
		
		int length = set.size(); 
				
		ArrayList<T> list = new ArrayList<>(set);
		if (set.size() == 0) return list;
		
		int[] sizes = new int[length];
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = list.get(i).size();
		}
		
		int[] subsetSum = subsetSum(sizes, n);
		if (subsetSum == null) return null;
		
		List<T> subset = new ArrayList<>();
		for (int i : subsetSum) subset.add(list.get(i));
		
		return subset;
	}
	
	public static int[] subsetSum(int[] sizes, int n) {
		
		int length = sizes.length;
		
		int sumPositive = 0;
		int sumNegative = 0;
		for (int i = 0; i < length; i++) {
			int v = sizes[i];
			if (v > 0) sumPositive += v;
			else sumNegative += v;
		}
		if (sumPositive > n) sumPositive = n;
		if (sumPositive < n) return null;
		
		int width = sumPositive - sumNegative + 1;
		
		boolean[][] sumArray = new boolean[length][width]; 
		for (int i = 0; i < width; i++) {
			sumArray[0][i] = sizes[0] == sumNegative + i;
		}
		for (int i = 1; i < length; i++) {
			for (int j = 0; j < width; j++) {
				if (sumArray[i-1][j] || sizes[i] == sumNegative + j) {
					sumArray[i][j] = true;
				} else {
					int x = j - sizes[i];
					if (x >= 0 && x < width && sumArray[i-1][x]) {
						sumArray[i][j] = true;
					}
				}
			}
		}
		
		for (int i = 0; i < length; i++) {
			System.out.println(Arrays.toString(sumArray[i]));
		}
		
		int r = length - 1, c = width - 1;
		if (!sumArray[r][c]) return null;

		List<Integer> indices = new ArrayList<>();
		while (c >= 0 && r > 0) {
			if (!sumArray[r][c]) break;
			while (r > 0 && sumArray[r-1][c]) r--;
			if (sizes[r] == 0) break;
			indices.add(r);
			c -= sizes[r];
		}
		
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) indicesArray[indicesArray.length - 1 - i] = indices.get(i);
		return indicesArray;
	}
	
}
