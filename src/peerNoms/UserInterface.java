package peerNoms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
				return;
			}
		}
		
		dc.parseInput(input);
		
		String[] output = dc.generateOutput();
		
		// And save it to an output file
		while (true) {
			try {
				saveOutput(output);
				break;
			} catch (FileNotFoundException e) {
				displayError(e);
			} catch (CannotProceed e) {
				return;
			}
		}
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
                return (String[]) lines.toArray(new String[lines.size()]);
            } else {
            	throw new IOException("Whoops! Something went wrong trying "
            			+ "to open that file. Check the file and try again!");
            }
        } else {
        	throw new CannotProceed();
        }
	}
	
	/**
	 * Saves the input array of strings as a file, chosen by the user
	 * with a JFileChooser.
	 * 
	 * @param output An array of strings representing lines of the output
	 * @throws FileNotFoundException If no file is found
	 * @throws CannotProceed If the user closes the save file dialog
	 */
	static void saveOutput(String[] output) throws FileNotFoundException, CannotProceed {
		JFileChooser chooser = new JFileChooser();
		
		int response = chooser.showSaveDialog(null);
		if (response == JFileChooser.APPROVE_OPTION) {
			PrintWriter stream = new PrintWriter(chooser.getSelectedFile());
			for (String line : output) {
				stream.println(line);
			}
			stream.close();
		} else {
			throw new CannotProceed();
		}
	}
}
