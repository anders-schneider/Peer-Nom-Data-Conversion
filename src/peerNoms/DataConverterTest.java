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
	public void testCreateStudent() {
		//TODO Test
		fail("Not yet implemented");
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
	public void testProcessStudentLine() {
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
