package peerNoms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataConverter {
	
	int numStudents;
	String[] nomColHeads;
	String[] categories;
	String[][] nomData, friendData;
	Student[] students;
	
	DataConverter() {
	}
	
	void parseInput(String[] lines) {
		HeaderInformation hi = new HeaderInformation();
		
		processColHeaders(lines[1], hi);
		
		int numStudents = lines.length;		
		
		processRowTitles(lines, hi);
		
		students = createStudentList(lines, hi);
		
		nomData = extractData(lines, hi.nomCols, hi.firstNomCol, hi.firstRow);
		friendData = extractData(lines, hi.friendCols, hi.firstFriendCol, hi.firstRow);
		
		//printData(nomData, 10, 100);
	}

	String[] generateOutput() {
		recordFriendsAndCategories();
		
		int maxFriends = findMaxFriends();
		
		int[] maxExtras = new int[categories.length];
		int totalExtraCols = findTotalExtraCols(maxExtras);
		
		int outCols = 2 + maxFriends + categories.length + totalExtraCols;
		
		String[][] outData = new String[numStudents + 1][outCols];
		
		fillWithEmptyStrings(outData);
		
		setFirstRow(outData, maxFriends, maxExtras);
		
		fillRows(outData, maxFriends, maxExtras);
		
		return formatResult(outData);
	}
	
	private int findMax(String category) {
		int result = 0;
		for (Student s : students) {
			if (s.numExtras(category) > result) {
				result = s.numExtras(category);
			}
		}
		return result;
	}
	
	private static Student createStudent (String[] row) {
		if (row[1].trim().length() == 0 || row[2].trim().length() == 0) {
			return new Student("", "");
		}
		String name = row[2].trim() + "; " + row[1].trim();
		String id = row[3].trim();
		
		return new Student(name, id);
	}
	
	private static String createColHeader(String uglyName) {
		int last = uglyName.length() - 1;
		while (uglyName.charAt(last) != '-') last--;
		
		int first = last;
		while (!".-".equals(uglyName.substring(first - 2, first))) first--;
		
		return uglyName.substring(first, last).trim();
	}

	private static String replaceNonDelimCommas(String headerRow) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < headerRow.length(); i++) {
			if (headerRow.charAt(i) == ',' 
					&& "month".equals(headerRow.substring(i - 5, i))) {
				sb.append(";");
			} else {
				sb.append(headerRow.charAt(i));
			}
		}
		
		return sb.toString();
	}
	
	private static int findFirstInstance(String[] headers, String match) {
		int len = match.length();
		int result = 0;
		
		for (String elem : headers) {
			if (elem.length() < len) continue;
			if (match.equals(elem.substring(0, len))) break;
			result++;
		}
		
		return result;
	}
	
	private void processColHeaders(String headerRow, 
											HeaderInformation hi) {
		headerRow = replaceNonDelimCommas(headerRow);
		String[] headers = headerRow.split(",");
		findColMarkers(headers, hi);
		
		hi.nomCols = (hi.totalCols - hi.firstNomCol) / 2;
		
		generateNomColHeads(headers, hi);

		hi.friendCols = (hi.lastFriendCol + 1 - hi.firstFriendCol) / 2;
	}
	
	private void generateNomColHeads(String[] headers, 
												HeaderInformation hi) {
		nomColHeads = new String[hi.nomCols];
		for (int i = 0; i < hi.nomCols; i++) {
			nomColHeads[i] = createColHeader(headers[hi.firstNomCol + 2 * i]);
		}
	}
	
	private void findColMarkers(String[] headers, HeaderInformation hi) {
		hi.totalCols = headers.length;
		
		hi.firstFriendCol = findFirstInstance(headers, "\"Team");
		int firstNomCol = findFirstInstance(headers, "Timing");
		
		hi.lastFriendCol = firstNomCol - 1;
		while ("Timing".equals(headers[firstNomCol].substring(0, 6))) firstNomCol++;
		hi.firstNomCol = firstNomCol;
	}
	
	private void processRowTitles(String[] lines, HeaderInformation hi) {
		int firstRow = 0;
		while (!"ResponseID".equals(lines[firstRow].substring(0, 10))) firstRow++;
		firstRow++;
		
		hi.firstRow = firstRow;
		hi.totalRows = lines.length;
		
		numStudents = hi.totalRows - hi.firstRow;
	}
	
	private Student[] createStudentList(String[] lines, HeaderInformation hi) {
		Student[] result = new Student[numStudents];
		for (int i = 0; i < numStudents; i++) {
			String[] row = lines[hi.firstRow + i].split(",", -1);
			result[i] = createStudent(row);
		}
		return result;
	}
	
	private String[][] extractData(String[] lines, int numCols, int firstCol, int firstRow) {
		String[][] result = new String[numStudents][numCols];
		for (int i = 0; i < numStudents; i++) {
			String[] line = lines[i + firstRow].split(",", -1);
			for (int j = 0; j < numCols; j++) {
				result[i][j] = line[firstCol + 2 * j];
			}
		}
		return result;
	}
	
	private void printData(String[][] data, int cols, int rows) {
		
		System.out.print("\t");
		for (int j = 0; j < cols; j++) {
			System.out.print("Col " + j + "\t");
		}
		System.out.println();
		
		for (int i = 0; i < rows; i++) {
			System.out.print("Row " + i + "\t");
			for (int j = 0; j < cols; j++) {
				System.out.print(data[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	private void recordFriendsAndCategories() {
		int nomCols = nomData[0].length;
		int friendCols = friendData[0].length;
		
		Map<String, String> cats = new HashMap<>();
		
		for (int i = 0; i < numStudents; i++) {
			Student s = students[i];
			processStudentLine(s, friendData[i], nomData[i], friendCols, 
															nomCols, cats);
		}
		
		fillInMissingCategories(cats);
		
		categories = cats.values().toArray(new String[cats.size()]);
	}
	
	private void fillInMissingCategories(Map<String, String> categories) {
		for (Student s : students) {
			for (String category : categories.values()) {
				if (!s.noms.containsKey(category)) {
					s.addNom(category, "");
				}
			}
		}
	}
	
	private void processStudentLine(Student s, String[] friendRow, String[] nomRow, 
						int friendCols, int nomCols, Map<String, String> categories) {
		
		addFriends(s, friendRow, friendCols);
		addNoms(s, categories, nomRow, nomCols);
	}
	
	private void addFriends(Student s, String[] friendRow, int friendCols) {
		for (int j = 0; j < friendCols; j++) {
			String friendIndicator = friendRow[j];
			if (!"".equals(friendIndicator)) {
				s.addFriend(nomColHeads[j]);
			}
		}
	}
	
	private void addNoms(Student s, Map<String, String> categories,
													String[] nomRow, int nomCols) {
		for (int j = 0; j < nomCols; j++) {
			String categoryKey = nomRow[j];
			if (!"".equals(categoryKey)) {
				if (!categories.containsKey(categoryKey)) {
					categories.put(categoryKey, "Category: " + categoryKey);
				}
				s.addNom(categories.get(categoryKey), nomColHeads[j]);
			}
		}
	}
	
	private int findMaxFriends() {
		int maxFriends = 0;
		for (Student s : students) {
			if (s.friends.size() > maxFriends) maxFriends = s.friends.size();
		}
		return maxFriends;
	}
	
	private int findTotalExtraCols(int[] maxExtras) {
		int totalExtraCols = 0;
		for (int i = 0; i < categories.length; i++) {
			maxExtras[i] = findMax(categories[i]);
			totalExtraCols += maxExtras[i];
		}
		return totalExtraCols;
	}
	
	private static void fillWithEmptyStrings(String[][] data) {
		for (int row = 0; row < data.length; row++) {
			for (int col = 0; col < data[0].length; col++) {
				data[row][col] = "";
			}
		}
	}
	
	private void setFirstRow(String[][] outData, int maxFriends, int[] maxExtras) {
		outData[0][0] = "Last Name; First Name";
		outData[0][1] = "Student ID";
		
		int j = 2;
		for (int h = 0; h < maxFriends; h++) {
			outData[0][j] = "Best Friend";
			j++;
		}
		for (String category : categories) {
			outData[0][j] = category;
			j++;
		}
		for (int k = 0; k < categories.length; k++) {
			for (int l = 0; l < maxExtras[k]; l++) {
				outData[0][j] = categories[k] + " (extra)";
				j++;
			}
		}
	}
	
	private void fillRows(String[][] outData, int maxFriends, int[] maxExtras) {
		for (int i = 0; i < numStudents; i++) {
			Student s = students[i];
			outData[i + 1][0] = s.name;
			outData[i + 1][1] = s.id;
			int m = 2;
			m = fillInFriends(outData[i + 1], s, maxFriends, m);
			m = fillInCategories(outData[i + 1], s, maxExtras, m);
		}
	}
	
	private int fillInFriends(String[] outRow, Student s, int maxFriends, int m) {
		int friendsLeft = maxFriends;
		for (String friend : s.friends) {
			outRow[m] = friend;
			friendsLeft--;
			m++;
		}
		
		if (friendsLeft > 0) m += friendsLeft;
		
		return m;
	}
	
	private int fillInCategories(String[] outRow, Student s, int[] maxExtras, int m) {
		for (String category : categories) {
			outRow[m] = s.noms.get(category);
			m++;
		}
		
		for (int k = 0; k < categories.length; k++) {
			String category = categories[k];
			if (!s.extras.containsKey(category)) {
				m += maxExtras[k];
			} else {
				int spacesLeft = maxExtras[k];
				ArrayList<String> extras = s.extras.get(category);
				for (String extra : extras) {
					outRow[m] = extra;
					m++;
					spacesLeft--;
				}
				m += spacesLeft;
			}
		}
		
		return m;
	}
	
	private String[] formatResult(String[][] outData) {
		String[] result = new String[outData.length];
		for (int row = 0; row < outData.length; row++) {
			StringBuilder sb = new StringBuilder();
			sb.append(outData[row][0]);
			for (int col = 1; col < outData[row].length; col++) {
				sb.append(",");
				if (!"null".equals(outData[row][col])) {
					sb.append(outData[row][col]);
				} else {
					System.out.println("ROW: " + row + ", COL: " + col);
				}
			}
			result[row] = sb.toString();
		}
		return result;
	}
}
