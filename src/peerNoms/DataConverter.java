package peerNoms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class does the heavy lifting of taking in a raw input CSV file,
 * extracting the relevant information, and repackaging the data in a
 * clean, synthesized way.
 * 
 * @author Anders Schneider
 *
 */
public class DataConverter {
	
	int numStudents;
	String[] nomColHeads, friendColHeads; // Nomination and friend column headers
	String[] categories; // Nomination categories
	String[][] nomData, friendData; // Nomination and friend data, separated
	Student[] students;
	
	//TODO Add error handling - if something that should be there isn't there, throw (and catch) a detailed error!
	
	/**
	 * Takes in the CSV lines of input, records specifics about the input
	 * data, cleans the data, and stores the simplified data in a pair of
	 * instance variables.
	 * 
	 * @param lines Input lines from a CSV file
	 */
	void parseInput(String[] lines) {
		// This object will store all specifics about the data
		HeaderInformation hi = new HeaderInformation();
		
		// Create more informative column headers from the input header row
		processColHeaders(lines[1], hi);
		
		// Create a new Student object for each data row
		processRowTitles(lines, hi);
		students = createStudentList(lines, hi);
		
		// Clean the data
		nomData = extractData(lines, hi.nomCols, hi.firstNomCol, hi.firstRow);
		friendData = extractData(lines, hi.friendCols, hi.firstFriendCol, hi.firstRow);
		
		//printData(nomData, 10, 100);
	}

	/**
	 * Given the cleaned data, student information, and column headers stored
	 * in instance variables, this method returns the CSV-format output to
	 * be saved to a file.
	 * 
	 * @return An array of CSV strings to be saved to a file
	 */
	String[] generateOutput() {
		// Exract and store all information about all students
		recordFriendsAndCategories();
		
		// Find the maximum number of friends and nominations per student
		int maxFriends = findMaxFriends();
		int[] maxExtras = new int[categories.length];
		
		// Calculate the total number of output columns
		int totalExtraCols = findTotalExtraCols(maxExtras);
		int outCols = 2 + maxFriends + categories.length + totalExtraCols;
		
		String[][] outData = new String[numStudents + 1][outCols];
		fillWithEmptyStrings(outData);
		
		// Fill the output data with the stored information
		setFirstRow(outData, maxFriends, maxExtras);
		fillRows(outData, maxFriends, maxExtras);
		
		return formatResult(outData);
	}
	
	/**
	 * Returns the maximum number of nominations any student submitted for
	 * the input category.
	 * 
	 * @param category A category for which peers nominated each other
	 * @return The maximum number of nominations submitted by any student
	 */
	private int findMax(String category) {
		int result = 0;
		for (Student s : students) {
			if (s.numExtras(category) > result) {
				result = s.numExtras(category);
			}
		}
		return result;
	}
	
	/**
	 * Creates and returns a Student object from the row of data corresponding
	 * to that student's friend choices and peer nominations.
	 * 
	 * @param row An array of cells representing a single data row
	 * @return A Student with all information in the row recorded
	 */
	private static Student createStudent (String[] row, HeaderInformation hi) {
		// If this row contains no first or last name, return a dummy Student
		if (row[1].trim().length() == 0 || row[2].trim().length() == 0) {
			return new Student("", "");
		}
		
		// Otherwise save the Student's name and ID and return it
		String name = row[hi.firstFriendCol - 2].trim() + "; " + row[hi.firstFriendCol - 3].trim();
		String id = row[hi.firstFriendCol - 1].trim();
		
		return new Student(name, id);
	}
	
	/**
	 * Returns a String representing a student's name, extracted from the
	 * input column header from the raw data.
	 * 
	 * @param uglyName The long, raw column header from the input data
	 * @return The name of the student represented by that column header
	 */
	private static String createColHeader(String uglyName) {
		// Search for the last '-' which marks the end of the student's name
		int last = uglyName.length() - 1;
		while (uglyName.charAt(last) != '-') last--;
		
		// Search for the front of the student's name (bookended by ".-")
		int first = last;
		System.out.println(uglyName);
		while (!".-".equals(uglyName.substring(first - 2, first))) first--;
		
		return uglyName.substring(first, last).trim();
	}

	/**
	 * Returns a formatted version of the input row, where all commas that
	 * are preceded by the word "month" are replaced by semicolons (because 
	 * they are part of the phrase "... in the past month, how often..." 
	 * rather than serving as data cell delimeters).
	 * 
	 * @param headerRow A raw data input row
	 * @return The reformatted data row
	 */
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
	
	/**
	 * Returns the index of the first instance of the input String as the
	 * first substring amongst the input column headers. Returns -1 if no
	 * header matches the input String.
	 * 
	 * @param headers A String array of column headers
	 * @param match A String to match to the beginning of each header
	 * @return The index of the first matching column header
	 */
	private static int findFirstInstance(String[] headers, String match) {
		int len = match.length();
		int result = 0;
		
		for (String elem : headers) {
			if (elem.length() < len) continue;
			if (match.equals(elem.substring(0, len))) return result;
			result++;
		}
		return -1;
	}
	
	/**
	 * Extracts all of the relevant information from the row of column headers
	 * and saves it in instance variables.
	 * 
	 * @param headerRow A String representing the row of column headers in the input
	 * @param hi A HeaderInformation object with relevant data specs
	 */
	private void processColHeaders(String headerRow, 
											HeaderInformation hi) {
		// Replace non-delimiting commas and split the data into its cells
		headerRow = replaceNonDelimCommas(headerRow);
		String[] headers = headerRow.split(",");
		
		// Record the relevant column information
		findColMarkers(headers, hi);
		
		hi.friendCols = (hi.lastFriendCol + 1 - hi.firstFriendCol) / 2;
		friendColHeads = generateColHeads(headers, hi.friendCols, hi.firstFriendCol); 
		
		hi.nomCols = (hi.totalCols - hi.firstNomCol) / 2;
		nomColHeads = generateColHeads(headers, hi.nomCols, hi.firstNomCol);
	}
	
	/**
	 * Generate sensible, simplified column headers out of the raw input column
	 * headers.
	 * 
	 * @param headers An array of Strings representing the raw column headers
	 * @param numCols A count of the number of columns to process
	 * @param firstCol The index of the first column to process
	 * @return An array of Strings representing the processed column headers
	 */
	private static String[] generateColHeads(String[] headers, int numCols, int firstCol) {
		String[] result = new String[numCols];
		for (int i = 0; i < numCols; i++) {
			result[i] = createColHeader(headers[firstCol + 2 * i]);
		}
		return result;
	}
	
	/**
	 * Records details about the input column headers in the HeaderInformation
	 * object. These details include the indices of the first column of friend
	 * and peer nomination data, as well as the indices of the last column
	 * of friend and peer nomination data.
	 * 
	 * @param headers An array of Strings representing column headers
	 * @param hi The HeaderInformation object where the header details are recorded
	 */
	private void findColMarkers(String[] headers, HeaderInformation hi) {
		for (int i = 0; i < 10; i++) {
			System.out.println(headers[i]);
		}
		
		hi.totalCols = headers.length;
		
		// First friend column begins after last name, first name, and ID columns
		hi.firstFriendCol = findFirstInstance(headers, "Please") + 3;
		
		System.out.println("First friend column: " + hi.firstFriendCol);
		
		// Last friend column begins just before 'Timing' column
		int firstNomCol = findFirstInstance(headers, "Timing");
		hi.lastFriendCol = firstNomCol - 1;
		
		// First peer nomination column begins after 'Timing' columns
		while ("Timing".equals(headers[firstNomCol].substring(0, 6))) firstNomCol++;
		hi.firstNomCol = firstNomCol;
	}
	
	/**
	 * Stores information regarding the headers of the input data rows in the
	 * input HeaderInformation object. This information includes the index of
	 * the first row and the total number of rows.
	 * 
	 * @param lines An array of Strings representing the input data
	 * @param hi The HeaderInformation object where row specs are recorded
	 */
	private void processRowTitles(String[] lines, HeaderInformation hi) {		
		int firstRow = 2;
		
		hi.firstRow = firstRow;
		hi.totalRows = lines.length;
		
		numStudents = hi.totalRows - hi.firstRow;
	}
	
	/**
	 * Returns a list of Students created from the headers of the input data
	 * rows. Each row corresponds to a single student.
	 * 
	 * @param lines An array of Strings representing the input data
	 * @param hi A HeaderInformation object with information about the data
	 * @return An array of Students created from the input row headers
	 */
	private Student[] createStudentList(String[] lines, HeaderInformation hi) {
		Student[] result = new Student[numStudents];
		for (int i = 0; i < numStudents; i++) {
			String[] row = lines[hi.firstRow + i].split(",", -1);
			result[i] = createStudent(row, hi);
		}
		return result;
	}
	
	/**
	 * Returns a copy of the input data, with all extraneous information
	 * stripped away. In particular, only every other column contains useful
	 * data, so half of the data columns are effectively removed.
	 * 
	 * @param lines The raw input data
	 * @param numCols The number of columns of data to process
	 * @param firstCol The index of the first column of data to process
	 * @param firstRow The index of the first row of data to process
	 * @return A 2D array of strings representing the cleaned data
	 */
	private String[][] extractData(String[] lines, int numCols, int firstCol, int firstRow) {
		String[][] result = new String[numStudents][numCols];
		for (int i = 0; i < numStudents; i++) {
			String[] line = lines[i + firstRow].split(",", -1);
			for (int j = 0; j < numCols; j++) {
				// Skip every other line of the raw data
				result[i][j] = line[firstCol + 2 * j];
			}
		}
		return result;
	}
	
	/**
	 * Prints the input 2D array of Strings to the console. This method is
	 * mainly used for testing.
	 * 
	 * @param data A 2D array of data to be printed
	 * @param cols The number of columns to print
	 * @param rows The number of rows to print
	 */
	private void printData(String[][] data, int cols, int rows) {
		// Print the headers: Col 1   Col2   Col3   ...
		System.out.print("\t");
		for (int j = 0; j < cols; j++) {
			System.out.print("Col " + j + "\t");
		}
		System.out.println();
		
		// Print all data one row at a time
		for (int i = 0; i < rows; i++) {
			System.out.print("Row " + i + "\t");
			for (int j = 0; j < cols; j++) {
				System.out.print(data[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	/**
	 * Parse every line of cleaned data and record information pertaining
	 * to each student in the corresponding Student object.
	 */
	private void recordFriendsAndCategories() {
		int nomCols = nomData[0].length;
		int friendCols = friendData[0].length;
		
		// A map of category keys ("0", e.g.) to character habits ("Grit", e.g.)
		Map<String, String> cats = new HashMap<>();
		
		for (int i = 0; i < numStudents; i++) {
			Student s = students[i];
			processStudentLine(s, friendData[i], nomData[i], friendCols, 
															nomCols, cats);
		}
		
		// If any students are missing peer nominations, fill them in with empty Strings 
		fillInMissingNoms(cats);
		
		// Save the list of categories to a global array
		categories = cats.values().toArray(new String[cats.size()]);
	}
	
	/**
	 * Checks each student to make sure that they have a peer nomination for
	 * every category of character habit. If they are missing a nomination,
	 * an empty string is put in its place.
	 * 
	 * @param categories A map of category keys to character habit categories
	 */
	private void fillInMissingNoms(Map<String, String> categories) {
		for (Student s : students) {
			for (String category : categories.values()) {
				if (!s.noms.containsKey(category)) {
					s.addNom(category, "");
				}
			}
		}
	}
	
	/**
	 * Saves information from the input row of data into the corresponding
	 * Student object.
	 * 
	 * @param s The corresponding Student
	 * @param friendRow The student's row of friend data
	 * @param nomRow The student's row of peer nomination data
	 * @param friendCols The number of friend data columns
	 * @param nomCols The number of peer nomination data columns
	 * @param categories A map of category keys to character habit categories
	 */
	private void processStudentLine(Student s, String[] friendRow, String[] nomRow, 
						int friendCols, int nomCols, Map<String, String> categories) {
		
		addFriends(s, friendRow, friendCols);
		addNoms(s, categories, nomRow, nomCols);
	}
	
	/**
	 * Parses a row of friend data to record a student's friends.
	 * 
	 * @param s The Student in question
	 * @param friendRow The row friend data for this student
	 * @param friendCols The number of columns of friend data
	 */
	private void addFriends(Student s, String[] friendRow, int friendCols) {
		for (int j = 0; j < friendCols; j++) {
			if (!"".equals(friendRow[j])) { // if the cell is not empty
				s.addFriend(friendColHeads[j]); // add the student as a friend
			}
		}
	}
	
	/**
	 * Parses a row of peer nomination data to record a student's peer
	 * nominations for various character habits.
	 * 
	 * @param s The Student in question
	 * @param categories A map of category keys to character habit categories
	 * @param nomRow The row of peer nomination data for this student
	 * @param nomCols The number of columns of peer nomination data
	 */
	private void addNoms(Student s, Map<String, String> categories,
													String[] nomRow, int nomCols) {
		for (int j = 0; j < nomCols; j++) {
			String categoryKey = nomRow[j];
			if (!"".equals(categoryKey)) {
				// Check if this category key is already in the map
				if (!categories.containsKey(categoryKey)) { // If not, add it
					categories.put(categoryKey, "Category: " + categoryKey);
				}
				s.addNom(categories.get(categoryKey), nomColHeads[j]);
			}
		}
	}
	
	/**
	 * Returns the maximum number of friends any student has indicated in the data.
	 * 
	 * @return The maximum number of friends for any single student
	 */
	private int findMaxFriends() {
		int maxFriends = 0;
		for (Student s : students) {
			if (s.friends.size() > maxFriends) maxFriends = s.friends.size();
		}
		return maxFriends;
	}
	
	/**
	 * Returns the number of total extra columns for all peer nomination
	 * categories. (When a student selects two students as having demonstrated
	 * "Self Control", one student must serve as the "Self Control Extra")
	 * 
	 * @param maxExtras An array of the maximum number of extras by category
	 * @return The total number of extra columns for peer nomination categories
	 */
	private int findTotalExtraCols(int[] maxExtras) {
		int totalExtraCols = 0;
		for (int i = 0; i < categories.length; i++) {
			maxExtras[i] = findMax(categories[i]);
			totalExtraCols += maxExtras[i];
		}
		return totalExtraCols;
	}
	
	/**
	 * Fills the input 2D array of Strings with empty strings.
	 * 
	 * @param data 2D array of Strings
	 */
	private static void fillWithEmptyStrings(String[][] data) {
		for (int row = 0; row < data.length; row++) {
			for (int col = 0; col < data[0].length; col++) {
				data[row][col] = "";
			}
		}
	}
	
	/**
	 * Sets the first row of the output data with the appropriate column headers.
	 * First comes the student name, then the student ID. Thereafter come the
	 * indicated best friends, and after that come the peer nominations by category.
	 * Finally, the extra peer nominations by category come last.
	 * 
	 * @param outData The 2D array of Strings representing the output data
	 * @param maxFriends The maximum number of friends indicated by any single student
	 * @param maxExtras The maximum number of extra peer nominations by category
	 */
	private void setFirstRow(String[][] outData, int maxFriends, int[] maxExtras) {
		// Add the student-information column headers
		outData[0][0] = "Last Name; First Name";
		outData[0][1] = "Student ID";
		
		// Add the "Best Friend" column headers
		int j = 2;
		for (int h = 0; h < maxFriends; h++) {
			outData[0][j] = "Best Friend";
			j++;
		}
		
		// Add the specific category column headers
		for (String category : categories) {
			outData[0][j] = category;
			j++;
		}
		
		// Add the column headers for the extra category peer nominations
		for (int k = 0; k < categories.length; k++) {
			for (int l = 0; l < maxExtras[k]; l++) {
				outData[0][j] = categories[k] + " (extra)";
				j++;
			}
		}
	}
	
	/**
	 * Fills the rows of output data using the information stored in the
	 * Students.
	 * 
	 * @param outData The 2D array of Strings representing the data to output
	 * @param maxFriends The maximum number of friends indicated by any single student
	 * @param maxExtras The maximum number of extra peer nominations by category
	 */
	private void fillRows(String[][] outData, int maxFriends, int[] maxExtras) {
		for (int i = 0; i < numStudents; i++) {
			Student s = students[i];
			outData[i + 1][0] = s.name;
			outData[i + 1][1] = s.id;
			int m = 2; // Index placeholder
			m = fillInFriends(outData[i + 1], s, maxFriends, m);
			m = fillInCategories(outData[i + 1], s, maxExtras, m);
		}
	}
	
	/**
	 * Fills in the cells of a given output row with the student's friends.
	 * 
	 * @param outRow The row of output data to fill
	 * @param s The Student in question
	 * @param maxFriends The maximum number of friends indicated by any single student
	 * @param m The index corresponding to the first column of output friend data
	 * @return An index corresponding to the first column after the output friend data
	 */
	private int fillInFriends(String[] outRow, Student s, int maxFriends, int m) {
		// Fill in all the friends this student has
		int friendsLeft = maxFriends;
		for (String friend : s.friends) {
			outRow[m] = friend;
			friendsLeft--;
			m++;
		}
		
		// If the student did not select the maximum possible number of students
		// advance the index
		if (friendsLeft > 0) m += friendsLeft;
		
		return m;
	}
	
	/**
	 * Fills in the cells of a given output row with the student's peer
	 * nominations (primary nominations, as well as extras).
	 * 
	 * @param outRow The row of output data to fill
	 * @param s The Student in question
	 * @param maxExtras The maximum number of extra peer nominations by category
	 * @param m The index corresponding to the first column of output nomination data
	 * @return An index corresponding to the first column after the output nomination data
	 */
	private int fillInCategories(String[] outRow, Student s, int[] maxExtras, int m) {
		// Fill in the student's primary nominations
		for (String category : categories) {
			outRow[m] = s.noms.get(category);
			m++;
		}
		
		// If any, fill in the student's extra peer nominations
		for (int k = 0; k < categories.length; k++) {
			String category = categories[k];
			if (!s.extras.containsKey(category)) { // student has no extras for this category
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
	
	/**
	 * Formats the output data and returns it as an array of CSV Strings.
	 * 
	 * @param outData A 2D array of output data
	 * @return A formatted 1D array of output data
	 */
	private String[] formatResult(String[][] outData) {
		String[] result = new String[outData.length];
		
		// Make each row into one CSV String
		for (int row = 0; row < outData.length; row++) {
			StringBuilder sb = new StringBuilder();
			sb.append(outData[row][0]);
			for (int col = 1; col < outData[row].length; col++) {
				sb.append(",");
				if (!"null".equals(outData[row][col])) { // Output "" instead of "null"
					sb.append(outData[row][col]);
				}
			}
			result[row] = sb.toString();
		}
		return result;
	}
}
