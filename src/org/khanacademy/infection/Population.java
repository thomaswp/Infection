package org.khanacademy.infection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Population {

	private int nextUserID = 0;
	private Set<User> allUsers = new HashSet<>();
	
	protected int incrementUserID() {
		return nextUserID++;
	}
	
	public User createUser(String userName) {
		User user = new User(userName, this);
		allUsers.add(user);
		return user;
	}
	
	public boolean removeUser(User user) {
		return allUsers.remove(user);
	}
	
	public Set<Infection> getInfections() {
		Set<Infection> infections = new HashSet<>();
		for (User user : allUsers) infections.add(user.getInfection());
		return infections;
	}
	
	/**
	 * Calls {@link Population#limitedInfection(String, int, int)} with 
	 * threshold = 0.
	 */
	public int limitedInfection(String condition, int n) {
		return limitedInfection(condition, n, 0);
	}
	
	/**
	 * Infects approximately n users with the given condition. 
	 * The algorithm will avoid breaking up any infection groups if it is possible
	 * to do so while still infecting at least (n - threshold) users.
	 * If this is not possible, the algorithm will infect as many whole infection
	 * groups as possible, and then infect only a portion of one additional infection
	 * group such that n total users are infected.
	 * The only circumstance under which fewer than (n - threshold) users will be infected
	 * is if the total users is fewer than n.
	 * 
	 * @param condition The condition with which to infect users
	 * @param n The desired number of users to infect
	 * @param threshold The threshold of users less than n which can be infected
	 * if this will allow all infection groups to maintain the same condition.
	 * @return The total number of users infected.
	 */
	public int limitedInfection(String condition, int n, int threshold) {
		
		Set<Infection> infections = getInfections();

		int infected = 0;
		// We use an approximating version of SubsetSum here, rather than the exact one,
		// because we want the closest possible sum, which means our "threshold"
		// for SubsetSum is infinity, which breaks the dynamic programming solution
		List<Infection> subset = SubsetSum.subsetSumApproximate(infections, n - infected);
		
		// Because this is an approximation, we can actually try to find more
		// infections that might fit in the gaps
		while (subset.size() > 0) {
			// First, infect the subset we've chosen and count the infected
			for (Infection infection : subset) {
				infection.addCondition(condition);
				infected += infection.size();
			}
			// remove the infections we've already selected from the list
			infections.removeAll(subset);
			// see if there's another subset that will fit in the gaps
			subset = SubsetSum.subsetSumApproximate(infections, n - infected);
		}
		
		// If we've reached the threshold, or included all infections, we're done
		if (infections.size() == 0 || Math.abs(infected - n) <= threshold) {
			return infected;
		}
		
		// If not, choose the largest infection and infect the remaining number
		List<Infection> list = new ArrayList<Infection>(infections);
		Collections.sort(list, SubsetSum.COUNTABLE_COMPARATOR);
		Infection largest = list.get(0);
		
		// There must be at least (n - infected) users in this infection
		// So we know we've now infected n users
		largest.infectUpTo(condition, n - infected);
		return n;
	}
	
	/**
	 * If threshold if 0, infects exactly n Users with the given condition without 
	 * breaking up any infection groups, or fails if this is not possible.
	 * If threshold is not 0, the exact number of infected users can be
	 * be m, where (n - threshold <= m <= n + threshold).
	 * We return the actual number of users infected, or -1 for failure.
	 * @param condition The condition with which to infect users
	 * @param n The number of users to infect
	 * @param threshold A margin of error n.
	 * @return The exact number of users infected, or -1 for failure
	 */
	public int limitedInfectionExact(String condition, int n, int threshold) {
		Set<Infection> infections = getInfections();
		List<Infection> subset = SubsetSum.subsetSum(infections, n, 0);
		if (subset == null) return -1;
		int infected = 0;
		for (Infection infection : subset) {
			infected += infection.size();
			infection.addCondition(condition);
		}
		return infected;
	}
	
	public int countUsersWithCondition(String condition) {
		int n = 0;
		for (User user : allUsers) if (user.hasCondition(condition)) n++;
		return n;
	}
	
}
