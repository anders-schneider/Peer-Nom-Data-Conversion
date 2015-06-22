package peerNoms;

import java.util.ArrayList;

public class DataConverter {
	
	int dataRows, dataCols;
	String[] colHeads, rowHeads;
	String[][] cleanData;
	
	String[][] outData;
	
	Student[] students;
	
	void parseInput(String[] lines) {
		String[] secondRow = lines[1].split(",");
		int totalCols = secondRow.length;
		int totalRows = lines.length;
		
		int firstCol = 0;
		for (String elem : secondRow) {
			if (elem.length() < 6) continue;
			if ("Timing".equals(elem.substring(0, 6))) break;
			firstCol++;
		}
		
		while ("Timing".equals(secondRow[firstCol].substring(0, 6))) firstCol++;
		
		int firstRow = 0;
		while (!"ResponseID".equals(lines[firstRow].substring(0, 10))) firstRow++;
		
		dataRows = totalRows - firstRow;
		dataCols = (totalCols - firstCol) / 2;
		
		rowHeads = new String[dataRows];
		for (int i = 0; i < dataRows; i++) {
			rowHeads[i] = createRowHeader(lines[firstRow + i].split(","));
		}
		
		colHeads = new String[dataCols];
		for (int i = 0; i < dataCols; i++) {
			colHeads[i] = createColHeader(secondRow[firstCol + 2 * i]);
		}
		
		cleanData = new String[dataRows][dataCols];
		for (int i = 0; i < dataRows; i++) {
			String[] line = lines[i + firstRow].split(",");
			for (int j = 0; j < dataCols; j++) {
				cleanData[i][j] = line[j + firstCol];
			}
		}
	}
	
	void generateOutput() {
		for (int i = 0; i < dataRows; i++) {
			Student s = new Student(rowHeads[i]);
		}
	}
	
	private String createRowHeader(String[] row) {
		return row[2].trim() + ", " + row[1].trim() + " (" + row[3].trim() + ")";
	}
	
	private String createColHeader(String uglyName) {
		int last = uglyName.length() - 1;
		while (uglyName.charAt(last) != '-') last--;
		
		int first = last;
		while (!".-".equals(uglyName.substring(first - 2, first))) first--;
		
		return uglyName.substring(first, last).trim();
	}
}
