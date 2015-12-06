package org.khanacademy.infection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {

	private static int nextUserID = 0;
	private static Set<User> allUsers = new HashSet<>();
	
	private String userName;
	private int subgraphSize = 1;
	private int userID = nextUserID++;
	
	private Infection infection;
	
	// Coaches and pupils are "doubly linked," in the sense that
	// they both contain a reference to each other
	private final Set<User> coaches = new HashSet<>();
	private final Set<User> pupils = new HashSet<>();
	
	private final HashSet<String> conditions = new HashSet<>();
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getSubgraphSize() {
		return subgraphSize;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public Infection getInfection() {
		return infection;
	}
	
	public void setInfection(Infection infection) {
		this.infection = infection;
	}
	
	public User() {
		this("Anonymous");
	}
	
	public User(String userName) {
		this.userName = userName;
		infection = new Infection();
		infection.addUser(this);
		allUsers.add(this);
	}
	
	public static boolean addCoach(User coach, User pupil) {
		if (coach == null || pupil == null) return false;
		if (pupil.coaches.contains(coach)) return false;
		
		pupil.coaches.add(coach);
		coach.pupils.add(pupil);
		
		coach.infection.addUser(pupil);
		return true;
	}
	
	public void delete() {
		infection.removeUser(this);
		for (User pupil : pupils) pupil.coaches.remove(this);
		for (User coach : coaches) coach.pupils.remove(this);
		allUsers.remove(this);
	}
	
	public static boolean removeCoach(User coach, User pupil) {
		if (coach == null || pupil == null) return false;
		if (!pupil.coaches.contains(coach)) return false;
		
		pupil.coaches.remove(coach);
		coach.pupils.remove(pupil);
		
		coach.infection.prune(coach);
		return true;
	}
	
	public Set<User> neighbors() {
		Set<User> neighbors = new HashSet<>();
		neighbors.addAll(coaches);
		neighbors.addAll(pupils);
		return neighbors;
	}
		
	public boolean addCondition(String condition) {
		return conditions.add(condition);
	}
	
	public boolean removeCondition(String condition) {
		return conditions.remove(condition);
	}
	
	public boolean hasCondition(String condition) {
		return conditions.contains(condition);
	}
	
	public static void infect(User user, String condition) {
		user.infection.addCondition(condition);
	}

	private static Set<Infection> getInfections() {
		Set<Infection> infections = new HashSet<>();
		for (User user : allUsers) infections.add(user.infection);
		return infections;
	}
	
	public static int limitedInfection(String condition, int n) {
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
	public static int limitedInfection(String condition, int n, int threshold) {
		
		Set<Infection> infections = getInfections();

		int infected = 0;
		List<Infection> subset = SubsetSum.subsetSumApproximate(infections, n - infected);
		
		while (subset.size() >= 0) {
			infections.removeAll(subset);
			for (Infection infection : subset) {
				infection.addCondition(condition);
				infected += infection.size();
			}
			subset = SubsetSum.subsetSumApproximate(infections, n - infected);
		}
		
		if (infections.size() == 0 || Math.abs(infected - n) <= threshold) {
			return infected;
		}
		
		List<Infection> list = new ArrayList<Infection>(infections);
		Collections.sort(list, SubsetSum.COUNTABLE_COMPARATOR);
		Infection largest = list.get(0);
		
		largest.infectUpTo(condition, n - infected);
		return n;
	}
	
	/**
	 * Infects exactly n Users with the given condition without breaking up
	 * and infection groups, or return false if this is not possible.
	 * @param condition The condition with which to infect users
	 * @param n The number of users to infect
	 * @return Whether or not the operation succeeded.
	 */
	public static boolean limitedInfectionExact(String condition, int n) {
		Set<Infection> infections = getInfections();
		List<Infection> subset = SubsetSum.subsetSum(infections, n, 0);
		if (subset == null) return false;
		for (Infection infection : subset) {
			infection.addCondition(condition);
		}
		return true;
	}
}
