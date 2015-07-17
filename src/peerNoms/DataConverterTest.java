package peerNoms;

import static org.junit.Assert.*;

import java.lang.reflect.*;

import org.junit.Before;
import org.junit.Test;

public class DataConverterTest {

	DataConverter dc;
	
	@Before
	public void setUp() throws Exception {
		dc = new DataConverter();
	}

	@Test
	public void testParseInput() {
		//TODO Test
		fail("Not yet implemented");
	}

	@Test
	public void testGenerateOutput() {
		//TODO Test
		fail("Not yet implemented");
	}

	@Test
	public void testCreateColHeader() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class[] cArg = new Class[1];
		cArg[0] = String.class;
		Method createColHeader = dc.getClass().getDeclaredMethod("createColHeader", cArg);
		createColHeader.setAccessible(true);
		
		String s1 = "\"Team: 1776ers / In the past month, who did you spend time with from your team (this / could be time s...- Andrew Arcuicci-Group\"";
		String result1 = (String) createColHeader.invoke(dc, s1);
		
		assertEquals("Andrew Arcuicci", result1);
		
		String s2 = "\"Team: Dragons / In the past month, who did you spend time with from your team (this / could be time s...- Anam  Josan-Group\"";
		String result2 = (String) createColHeader.invoke(dc, s2);
		
		assertEquals("Anam  Josan", result2);
		
		String s3 = "\"Team: Dragons / In the past month, who did you spend time with from your team (this / could be time s...- Anam Middle Josan-Smith-Group\"";
		String result3 = (String) createColHeader.invoke(dc, s3);
		
		assertEquals("Anam Middle Josan-Smith", result3);
	}
	
	@Test
	public void testCreateStudent() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class[] cArg = new Class[1];
		cArg[0] = String.class;
		Method createStudent = dc.getClass().getDeclaredMethod("createStudent", cArg);
		createStudent.setAccessible(true);
		
		// FIGURE OUT WHAT IS GOING THAT MAKES THIS THROW AN ERROR
		
		HeaderInformation hi = new HeaderInformation();
		hi.firstFriendCol = 4;
		
		String[] row1 = {"R_beo98pS2bzrDT2h", "Anders", "Schneider", "60201", "", "", "1", "0", "", "", "", "", "3.66", "4.567", "", "", "2", "0"};
		Student expected1 = new Student("Anders Schneider", "60201");
		Student actual1 = (Student) createStudent.invoke(dc, row1, hi);
		
		assertTrue(expected1.equals(actual1));
		
		String[] row2 = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
		Student expected2 = new Student("", "");
		Student actual2 = (Student) createStudent.invoke(dc, row2, hi);
		
		assertTrue(expected2.equals(actual2));
	}
	
	@Test
	public void testFillWithEmptyStrings() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFindFirstInstance() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testGenerateColHeads() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testReplaceNonDelimCommas() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testAddFriends() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testAddNoms() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testCreateStudentList() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testExtractData() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFillInCategories() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFillInFriends() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFillInMissingCategories() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFillRows() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFindColMarkers() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFindMax() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFindMaxFriends() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFindTotalExtraCols() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testFormatResult() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testProcessColHeaders() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testProcessRowTitles() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testRecordFriendsAndCategories() {
		//TODO Test
		fail("Not yet implemented");
	}
	
	@Test
	public void testSetFirstRow() {
		//TODO Test
		fail("Not yet implemented");
	}
}
