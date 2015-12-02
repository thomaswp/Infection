package org.khanacademy.infection;

import java.util.HashSet;
import java.util.Set;

public class User {

	private static int nextUserID = 0;
	
	private String userName;
	private int subgraphSize = 1;
	private int userID = nextUserID++;
	
	private Network network;
	
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
	
	public Network getNetwork() {
		return network;
	}
	
	public void setNetwork(Network network) {
		this.network = network;
	}
	
	public User() {
		this("Anonymous");
	}
	
	public User(String userName) {
		this.userName = userName;
		network = new Network();
		network.addUser(this);
	}
	
	public static boolean addCoach(User coach, User pupil) {
		if (coach == null || pupil == null) return false;
		if (pupil.coaches.contains(coach)) return false;
		
		pupil.coaches.add(coach);
		coach.pupils.add(pupil);
		
		coach.network.addUser(pupil);
		return true;
	}
	
	public static boolean removeCoach(User coach, User pupil) {
		if (coach == null || pupil == null) return false;
		if (!pupil.coaches.contains(coach)) return false;
		
		pupil.coaches.remove(coach);
		coach.pupils.remove(pupil);
		
		coach.network.prune(coach);
		return true;
	}
	
	public Set<User> neighbors() {
		Set<User> neighbors = new HashSet<>();
		neighbors.addAll(coaches);
		neighbors.addAll(pupils);
		return neighbors;
	}
		
	public void addCondition(String condition, boolean infect) {
		if (infect) network.addCondition(condition);
		else conditions.add(condition);
	}
	
	public void removeCondition(String condition, boolean infect) {
		if (infect) network.removeCondition(condition);
		else conditions.remove(condition);
	}
	
	public boolean hasCondition(String condition) {
		return conditions.contains(condition);
	}
	
}
