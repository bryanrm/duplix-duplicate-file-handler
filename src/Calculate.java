/* Copyright (C) 2016  Bryan R Martinez (https://github.com/martineb1)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

/**
 * This class performs all the SHA-256 hash calculations in its
 * own thread (separate from the main thread). It also provides
 * additional functionality regarding output of duplicate files.
 * 
 * @author Bryan R Martinez
 * @version 1.0
 *
 */
public class Calculate extends Thread {
	public boolean isFinished;
	
	private TreeMap<String, LinkedList<String>> map;
	private int dupeCounter;
	private int delCounter;
	private int moveCounter;
	private MessageDigest md;
	private ArgsParser p;
	private Path root;
	private StringBuilder output;
	
	/**
	 * Default and sole constructor initializes variables.
	 * 
	 * @param parser instance of ArgsParser
	 */
	public Calculate(ArgsParser parser) {
		p = parser;
		dupeCounter = 0;
		delCounter = 0;
		moveCounter = 0;
		isFinished = false;
		map = new TreeMap<>();
		output = new StringBuilder();
		root = IO.getFile(p.getSrcDir());
		
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
	}
	
	/**
	 * This method initiates the scan process and produces
	 * results in accordance with the user-entered command-line
	 * arguments. 
	 */
	@Override
	public void run() {
		System.out.println("Beginning scan...");
		if (p.recursiveMode())
			calcRec(root);
		else
			calc(root);
		
		ArrayList<LinkedList<String>> list = getListOfDupes();
		
		if (p.moveMode()) {
			for (LinkedList<String> subList : list) {
				for (String item : subList) {
					IO.appendNum = 0;
					if (IO.moveFile(item, p.getDestDir()))
						moveCounter++;
				}
			}
			outputDupes(list);
		}
		else if (p.deleteMode()) {
			for (LinkedList<String> subList : list) {
				for (int i = 0; i < subList.size()-1; i++) {
					if (IO.deleteFile(subList.get(i)))
						delCounter++;
				}
			}
			outputDupesMarked(list);
		}
		else {
			outputDupes(list);
		}
		
		isFinished = true;
		
		if (dupeCounter == 0) {
			System.out.println("No duplicate files found!");
		}
		else {		
			output.append("Final stats: " + dupeCounter + 
					" sets of duplicate files found." + System.lineSeparator());
			
			if (p.moveMode()) {
				output.append(moveCounter + " files moved to " + 
					p.getDestDir() + System.lineSeparator());
			}
			else if (p.deleteMode()) {
				output.append(delCounter + " files deleted." +
					System.lineSeparator());
			}
			
			System.out.println(output.toString());
			
			if (p.saveMode())
				if (IO.saveFile(p.getExpFile(), output.toString()))
					System.out.println("File saved to " + p.getExpFile());
				else
					System.out.println("Unable to save to " + p.getExpFile());
		}
		
	}

	/**
	 * This method searches a given root directory to maintain
	 * a map of files discovered and their hash. Non-recursive.
	 * 
	 * @param path base directory to search
	 */
	private void calc(Path path) {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				for (Path file : stream) {
					if (!Files.isDirectory(file))
						try {
							md.update(Files.readAllBytes(file));
							byte[] digest = md.digest();
							
							String digestInHex = DatatypeConverter.printHexBinary(digest).toUpperCase();
							
							if (map.containsKey(digestInHex)) {
								map.get(digestInHex).add(file.toString());
							}
							else {
								LinkedList<String> list = new LinkedList<>();
								list.add(file.toString());
								map.put(digestInHex, list);
							}
						} catch (Exception e) { System.err.println(e.getMessage()); }
				}
			} catch (IOException | DirectoryIteratorException e) { System.err.println(e.getMessage()); }
		}
	}
	
	/**
	 * This method recurses through a given root directory to maintain
	 * a map of files discovered and their hash.
	 * 
	 * @param path base directory to search
	 */
	private void calcRec(Path path) {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				for (Path file : stream)
					calcRec(file);
			} catch (IOException | DirectoryIteratorException e) { }
		}
		else {
			try {
				md.update(Files.readAllBytes(path));
				byte[] digest = md.digest();
				
				String digestInHex = DatatypeConverter.printHexBinary(digest).toUpperCase();
				
				if (map.containsKey(digestInHex)) {
					map.get(digestInHex).add(path.toString());
				}
				else {
					LinkedList<String> list = new LinkedList<>();
					list.add(path.toString());
					map.put(digestInHex, list);
				}
			} catch (Exception e) { System.err.println(e.getMessage()); }
		}
	}
	
	/**
	 * This method determines which files in the map have duplicates 
	 * and returns a list of all lists of duplicate files.
	 * 
	 * @return lists of duplicates in String form
	 */
	private ArrayList<LinkedList<String>> getListOfDupes() {
		ArrayList<LinkedList<String>> list = new ArrayList<LinkedList<String>>();
		for (String key : map.keySet()) {
			if (map.get(key).size() > 1) {
				dupeCounter++;
				list.add(map.get(key));
			}
		}
		
		return list;
	}
	
	/**
	 * This method builds a structured String based on a given list
	 * of duplicate files found.
	 * 
	 * @param list list of duplicate files
	 */
	private void outputDupes(ArrayList<LinkedList<String>> list) {
		for (LinkedList<String> subList : list) {
			int count = 1;
			for (String item : subList) {
				output.append(count++ + ".\t" + item + 
					System.lineSeparator());
			}
			output.append("===========" + System.lineSeparator());
		}
	}
	
	/**
	 * This method builds a structured String based on a given list
	 * of duplicate files found. Additionally, as part of the formatting
	 * process, deleted files are marked with an asterisk.
	 * 
	 * @param list list of duplicate files
	 */
	private void outputDupesMarked(ArrayList<LinkedList<String>> list) {
		for (LinkedList<String> subList : list) {
			int count = 1;
			for (int i = 0; i < subList.size(); i++) {
				if (i != subList.size()-1)
					output.append(count++ + ".\t*" + subList.get(i) +
							System.lineSeparator());
				else
					output.append(count++ + ".\t" + subList.get(i) + 
							System.lineSeparator());
			}
			output.append("===========" + System.lineSeparator());
		}
	}
}
