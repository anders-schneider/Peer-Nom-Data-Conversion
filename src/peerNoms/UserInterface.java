package peerNoms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;

public class UserInterface {

	public static void main(String[] args) {
		DataConverter dc = new DataConverter();
		
		String[] input;
		
		while (true) {
			try {
				input = load();
				break;
			} catch (IOException e) {
				displayError(e);
			} catch (CannotProceed e) {
				displayError(e);
			}
		}
		
		dc.parseInput(input);
		
		dc.generateOutput();
	}
	
	private static void displayError(Exception e) {
		JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
				JOptionPane.ERROR_MESSAGE);
	}

	private static String[] load() throws IOException, CannotProceed {
		ArrayList<String> lines = new ArrayList<String>(600);
		
        BufferedReader reader;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Please select the data file");
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {	
                String fileName = file.getCanonicalPath();
                
                // Check to make sure the file is a CSV file
                int len = fileName.length();	                
                if (!".csv".equals(fileName.substring(len - 4, len))) {
                	// Otherwise throw an exception
                	throw new IOException("The selected file is not a .csv file. Try again!");
                }
                
                reader = new BufferedReader(new FileReader(fileName));
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
                return (String[]) lines.toArray();
            } else {
            	throw new IOException("Whoops! Something went wrong trying "
            			+ "to open that file. Check the file and try again!");
            }
        } else {
        	throw new CannotProceed();
        }
	}
}
