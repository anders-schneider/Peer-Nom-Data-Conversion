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

	//TODO Comment everything
	//TODO Test everything!
	
	public static void main(String[] args) {		
		JOptionPane.showMessageDialog(null, "This program converts survey response "
				+ "data from students into a condensed, human-readable format.\n\n"
				+ "You'll first be prompted to select the data file.\n\n"
				+ "Note that it must be a .csv file!",
				"Welcome!",
				JOptionPane.DEFAULT_OPTION);
		
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
		
		DataConverter dc = new DataConverter();
		dc.parseInput(input);
		
		String[] output = dc.generateOutput();
		
		JOptionPane.showMessageDialog(null, "Awesome - now you just need to select a "
				+ "file location to save the output to and you'll be all done!");
		
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
	
	/**
	 * Displays an error message to the user based on the input Exception.
	 * 
	 * @param e An exception to display to the user
	 */
	private static void displayError(Exception e) {
		JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Returns an array of String representing the lines of input from a
	 * file specified by the user. A file chooser opens, and the user must
	 * select a valid CSV file. If any other file type is chosen, an
	 * IOException is thrown.
	 * 
	 * @return An array of strings representing the lines of input
	 * @throws IOException If there is an error accessing a specific file
	 * @throws CannotProceed If the user quits from the load dialog
	 */
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
