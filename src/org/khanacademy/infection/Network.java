package org.khanacademy.infection;

import java.util.HashSet;
import java.util.Set;

public class Network {
	
	private final Set<User> users = new HashSet<>();
	
	public int size() {
		return users.size();
	}

	public void addUser(User user) {
		users.add(user);
		
		Network userNetwork = user.getNetwork();
		if (userNetwork != this) {
			for (User addition : userNetwork.users) {
				addition.setNetwork(this);
				users.add(addition);
			}
		}
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
		
		Network split = new Network();
		split.users.addAll(removed);
		for (User user : removed) {
			user.setNetwork(split);
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
			user.addCondition(condition, false);
		}
	}

	public void removeCondition(String condition) {
		for (User user : users) {
			user.removeCondition(condition, false);
		}
	}
	
	
}
