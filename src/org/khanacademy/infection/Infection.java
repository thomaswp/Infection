package org.khanacademy.infection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.khanacademy.infection.SubsetSum.ICountable;

public class Infection implements ICountable {
	
	private final Set<User> users = new HashSet<>();
	
	public int size() {
		return users.size();
	}

	/**
	 * Adds the given user to this infection, combining its current
	 * infection into this one.
	 * @param user
	 */
	public void addUser(User user) {
		users.add(user);
		
		Infection userInfection = user.getInfection();
		if (userInfection != this) {
			// TODO: optionally, we could update those users' conditions
			// to make it homogeneous
			for (User addition : userInfection.users) {
				addition.setInfection(this);
				users.add(addition);
			}
		}
	}
	
	/**
	 * Removes the given user from this infection, and then
	 * optionally splits this infection if it's become broken up.
	 */
	public void deleteUser(User user) {
		users.remove(user);
		Iterator<User> iterator = users.iterator();
		if (iterator.hasNext()) {
			// removing this user may have split up the infection
			prune(iterator.next());
		}
	}
	
	/**
	 * Prunes any users from this infection which are no longer 
	 * connected to the given user.
	 * @param root
	 */
	public void prune(User root) {
		// Calculate the connected subgraph starting at root
		Set<User> included = new HashSet<>();
		addSubgraph(root, included);
		
		// Figure out who didn't make the cut
		Set<User> removed = new HashSet<>();
		for (User user : users) {
			if (!included.contains(user)) {
				removed.add(user);
			}
		}
		
		// Spin these users off into new infections
		while (removed.size() > 0) {
			// Pop a user, create a new infection and find its members
			User pop = users.iterator().next();
			Infection split = new Infection();
			Set<User> connected = new HashSet<>();
			addSubgraph(pop, connected);
			split.users.addAll(connected);
			for (User user : connected) {
				user.setInfection(split);
			}
			
			// Then remove these users and keep going
			removed.removeAll(connected);
		}
	}

	// Finds the connected subgraph starting at root
	// O(n + e) isn't great, but presumably removing relationships and users 
	// will be a rare operation
	private static void addSubgraph(User root, Set<User> included) {
		if (included.contains(root)) return;
		
		included.add(root);
		for (User neighbor : root.neighbors()) {
			addSubgraph(neighbor, included);
		}
	}

	/**
	 * Adds the given condition to all users in this infection
	 * @param condition The condition to infect
	 */
	public void addCondition(String condition) {
		for (User user : users) {
			user.addCondition(condition);
		}
	}

	/**
	 * Removes the given condition from all users in this infection
	 * @param condition The condition to remove
	 */
	public void removeCondition(String condition) {
		for (User user : users) {
			user.removeCondition(condition);
		}
	}

	/**
	 * Infects up to n users in this infection with the given condition
	 * @param condition The condition to infect
	 * @param n The number of users to infect
	 */
	public void infectUpTo(String condition, int n) {
		for (User user : users) {
			if (n <= 0) break;
			user.addCondition(condition);
			n--;
		}
	}
	
	
}
