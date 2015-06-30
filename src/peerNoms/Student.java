package peerNoms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO Comment all of Student class

public class Student {
	String name, id;
	Map<String, String> noms;
	Map<String, ArrayList<String>> extras;
	List<String> friends;
	
	public Student(String name, String id) {
		this.name = name;
		this.id = id;
		noms = new HashMap<String, String>();
		extras = new HashMap<String, ArrayList<String>>();
		friends = new ArrayList<String>();
	}
	
	public void addFriend(String friend) {
		if (!friends.contains(friend)) friends.add(friend);
	}
	
	public void addNom(String category, String name) {
		if (!noms.containsKey(category)) {
			noms.put(category, name);
		} else if (extras.containsKey(category)) {
			extras.get(category).add(name);
		} else {
			ArrayList<String> extraList = new ArrayList<String>();
			extraList.add(name);
			extras.put(category, extraList);
		}
	}
	
	public int numExtras(String category) {
		if (!extras.containsKey(category)) return 0;
		return extras.get(category).size();
	}
}
