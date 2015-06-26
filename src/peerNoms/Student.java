package peerNoms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Student {
	String name;
	HashMap<String, String> noms;
	HashMap<String, ArrayList<String>> extras;
	List<String> bffs;
	
	public Student(String name) {
		this.name = name;
		noms = new HashMap<String, String>();
		extras = new HashMap<String, ArrayList<String>>();
		bffs = new ArrayList<String>();
	}
	
	public void addBFF(String bff) {
		if (!bffs.contains(bff)) bffs.add(bff);
	}
	
	public void add(String category, String name) {
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
