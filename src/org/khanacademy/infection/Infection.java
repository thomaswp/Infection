package org.khanacademy.infection;

import java.util.HashSet;
import java.util.Set;

import org.khanacademy.infection.SubsetSum.ICountable;

public class Infection implements ICountable {
	
	private final Set<User> users = new HashSet<>();
	
	public int size() {
		return users.size();
	}

	public void addUser(User user) {
		users.add(user);
		
		Infection userInfection = user.getInfection();
		if (userInfection != this) {
			for (User addition : userInfection.users) {
				addition.setInfection(this);
				users.add(addition);
			}
		}
	}
	
	public void removeUser(User user) {
		users.remove(user);
	}
	
	public void prune(User root) {
		Set<User> included = new HashSet<>();
		addSubgraph(root, included);
		
		Set<User> removed = new HashSet<>();
		for (User user : users) {
			if (!included.contains(user)) {
				removed.add(user);
			}
		}
		
		Infection split = new Infection();
		split.users.addAll(removed);
		for (User user : removed) {
			user.setInfection(split);
		}
	}

	// O(n + e) isn't great, but presumably removing relationships will
	// be a rare operation
	private static void addSubgraph(User root, Set<User> included) {
		if (included.contains(root)) return;
		
		included.add(root);
		for (User neighbor : root.neighbors()) {
			addSubgraph(neighbor, included);
		}
	}

	public void addCondition(String condition) {
		for (User user : users) {
			user.addCondition(condition);
		}
	}

	public void removeCondition(String condition) {
		for (User user : users) {
			user.removeCondition(condition);
		}
	}

	public void infectUpTo(String condition, int n) {
		for (User user : users) {
			if (n <= 0) break;
			user.addCondition(condition);
			n--;
		}
	}
	
	
}
