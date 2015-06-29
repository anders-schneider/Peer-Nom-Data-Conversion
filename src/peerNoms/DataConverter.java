package peerNoms;

import java.util.ArrayList;
import java.util.Arrays;

public class DataConverter {
	
	int dataRows, dataCols, bffCols;
	String[] colHeads, sNames, sIds;
	String[][] cleanData, bffData;
	
	Student[] students;
	
	String[][] outData;
	
	void parseInput(String[] lines) {
		String rowTwo = lines[1];
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < rowTwo.length(); i++) {
			if (rowTwo.charAt(i) == ',' && "month".equals(rowTwo.substring(i - 5, i))) {
				sb.append(";");
			} else {
				sb.append(rowTwo.charAt(i));
			}
		}
		
		String[] secondRow = sb.toString().split(",");
		
		int totalCols = secondRow.length;
		int totalRows = lines.length;
		
		int firstBFFCol = 0;
		for (String elem : secondRow) {
			if (elem.length() < 5) continue;
			if ("\"Team".equals(elem.substring(0, 5))) break;
			firstBFFCol++;
		}
		
		int firstCol = 0;
		for (String elem : secondRow) {
			if (elem.length() < 6) continue;
			if ("Timing".equals(elem.substring(0, 6))) break;
			firstCol++;
		}
		
		int lastBFFCol = firstCol - 1;
		
		while ("Timing".equals(secondRow[firstCol].substring(0, 6))) firstCol++;
		
		int firstRow = 0;
		while (!"ResponseID".equals(lines[firstRow].substring(0, 10))) firstRow++;
		firstRow++;
		
		dataRows = totalRows - firstRow;
		dataCols = (totalCols - firstCol) / 2;
		bffCols = (lastBFFCol + 1 - firstBFFCol) / 2;
		
		sNames = new String[dataRows];
		sIds = new String[dataRows];
		for (int i = 0; i < dataRows; i++) {
			String[] row = lines[firstRow + i].split(",");
			if (row.length < 4) continue;
			saveNameId(row, i);
		}
		
		colHeads = new String[dataCols];
		for (int i = 0; i < dataCols; i++) {
			colHeads[i] = createColHeader(secondRow[firstCol + 2 * i]);
		}
		
		cleanData = new String[dataRows][dataCols];
		for (int i = 0; i < dataRows; i++) {
			String[] line = lines[i + firstRow].split(",", -1);
			for (int j = 0; j < dataCols; j++) {
				cleanData[i][j] = line[firstCol + 2 * j];
			}
		}
		
		bffData = new String[dataRows][bffCols];
		for (int i = 0; i < dataRows; i++) {
			String[] line = lines[i + firstRow].split(",", -1);
			for (int j = 0; j < bffCols; j++) {
				bffData[i][j] = line[firstBFFCol + 2 * j];
			}
		}
		
//		int rows = 100;
//		int cols = 10;
//		
//		System.out.print("\t");
//		for (int j = 0; j < cols; j++) {
//			System.out.print("Col " + j + "\t");
//		}
//		System.out.println();
//		
//		for (int i = 0; i < rows; i++) {
//			System.out.print("Row " + i + "\t");
//			for (int j = 0; j < cols; j++) {
//				System.out.print(bffData[i][j] + "\t");
//			}
//			System.out.println();
//		}
	}
	
	String[] generateOutput() {
		students = new Student[dataRows];
		
		ArrayList<String> categories = new ArrayList<String>();
		
		for (int i = 0; i < dataRows; i++) {
			Student s = new Student(sNames[i], sIds[i]);
			for (int j = 0; j < dataCols; j++) {
				String category = cleanData[i][j];
				if (!"".equals(category)) {
					s.add(category, colHeads[j]);
					if (!categories.contains(category)) categories.add(category);
				}
			}
			for (int j = 0; j < bffCols; j++) {
				String bffIndicator = bffData[i][j];
				if (!"".equals(bffIndicator)) {
					s.addBFF(colHeads[j]);
				}
			}
			students[i] = s;
		}
		
		for (Student s : students) {
			for (String category : categories) {
				if (!s.noms.containsKey(category)) {
					s.add(category, "");
				}
			}
		}
		
		String[] cats = categories.toArray(new String[categories.size()]);
		
		int maxBFFs = 0;
		for (Student s : students) {
			if (s.bffs.size() > maxBFFs) maxBFFs = s.bffs.size();
		}
		
		int totalExtraCols = 0;
		int[] maxExtras = new int[cats.length];
		for (int i = 0; i < cats.length; i++) {
			maxExtras[i] = findMax(cats[i]);
			totalExtraCols += maxExtras[i];
		}
		
		int outCols = 2 + maxBFFs + cats.length + totalExtraCols;
		
		outData = new String[dataRows + 1][outCols];
		
		for (int row = 0; row < outData.length; row++) {
			for (int col = 0; col < outData[0].length; col++) {
				outData[row][col] = "";
			}
		}
		
		outData[0][0] = "Student: Last Name; First Name";
		outData[0][1] = "Student ID";
		
		int j = 2;
		for (int h = 0; h < maxBFFs; h++) {
			outData[0][j] = "Best Friend";
			j++;
		}
		for (String category : cats) {
			outData[0][j] = "Category: " + category;
			j++;
		}
		for (int k = 0; k < cats.length; k++) {
			for (int l = 0; l < maxExtras[k]; l++) {
				outData[0][j] = "Category: " + cats[k] + " (extra)";
				j++;
			}
		}
		
		for (int i = 0; i < dataRows; i++) {
			Student s = students[i];
			outData[i + 1][0] = s.name;
			outData[i + 1][1] = s.id;
			int m = 2;
			int bffsLeft = maxBFFs;
			for (String bff : s.bffs) {
				outData[i + 1][m] = bff;
				bffsLeft--;
				m++;
			}
			if (bffsLeft > 0) m += bffsLeft;
			for (String category : cats) {
				outData[i + 1][m] = s.noms.get(category);
				m++;
			}
			for (int k = 0; k < cats.length; k++) {
				String category = cats[k];
				if (!s.extras.containsKey(category)) {
					m += maxExtras[k];
				} else {
					int spacesLeft = maxExtras[k];
					ArrayList<String> extras = s.extras.get(category);
					for (String extra : extras) {
						outData[i + 1][m] = extra;
						m++;
						spacesLeft--;
					}
					m += spacesLeft;
				}
			}
		}
		
//		for (int row = 0; row < dataRows + 1; row++) {
//			for (int col = 0; col < outData[0].length; col++) {
//				System.out.print(outData[row][col] + "\t");
//			}
//			System.out.println();
//		}
		
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
	
	private int findMax(String category) {
		int result = 0;
		for (Student s : students) {
			if (s.numExtras(category) > result) {
				result = s.numExtras(category);
			}
		}
		return result;
	}
	
	private void saveNameId (String[] row, int index) {
		sNames[index] = row[2].trim() + "; " + row[1].trim();
		sIds[index] = row[3].trim();
	}
	
	private String createColHeader(String uglyName) {
		int last = uglyName.length() - 1;
		while (uglyName.charAt(last) != '-') last--;
		
		int first = last;
		while (!".-".equals(uglyName.substring(first - 2, first))) first--;
		
		return uglyName.substring(first, last).trim();
	}
}
