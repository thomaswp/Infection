package org.khanacademy.infection;

import java.util.HashSet;
import java.util.Set;

public class User {
	
	private String userName;
	private int subgraphSize = 1;
	private int userID;
	
	private Infection infection;
	
	// Coaches and pupils are "doubly linked," in the sense that
	// they both contain a reference to each other
	private final Set<User> coaches = new HashSet<>();
	private final Set<User> pupils = new HashSet<>();
	
	private final HashSet<String> conditions = new HashSet<>();
	
	private final Population population;
	
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
	
	protected User(String userName, Population population) {
		this.userName = userName;
		this.population = population;
		infection = new Infection();
		infection.addUser(this);
		userID = population.incrementUserID();
	}
	
	public static boolean addCoach(User coach, User pupil) {
		if (coach == null || pupil == null) return false;
		if (coach == pupil) return false;
		if (pupil.coaches.contains(coach)) return false;
		
		pupil.coaches.add(coach);
		coach.pupils.add(pupil);
		
		coach.infection.addUser(pupil);
		return true;
	}
	
	public void delete() {
		infection.deleteUser(this);
		for (User pupil : pupils) pupil.coaches.remove(this);
		for (User coach : coaches) coach.pupils.remove(this);
		population.removeUser(this);
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
	
	/**
	 * Infects this User with the given condition, which
	 * spreads to all connected users through this User's
	 * {@link Infection}.
	 * @param condition The condition with which to infect users
	 */
	public void infect(String condition) {
		infection.addCondition(condition);
	}
}
