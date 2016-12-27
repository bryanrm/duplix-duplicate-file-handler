/* Copyright (C) 2016  Bryan R. Martinez (https://github.com/bryanrm)
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class provides a set of static input/output helper
 * functions in an effort to centralize all input/output methods.
 * 
 * @author Bryan R Martinez
 * @version 1.0
 *
 */
public class IO {
	public static int appendNum = 0;
	
	/**
	 * This method returns a value indicating whether a given 
	 * location exits or not.
	 * 
	 * @param loc String representation of a file path
	 * @return boolean value indicating if location exists
	 */
	public static boolean exists(String loc) {
		Path path = Paths.get(loc);
		if (Files.exists(path))
			return true;
		else return false;
	}
	
	/**
	 * This method returns a value indicating whether a given
	 * directory location exists or not. If it does not exist, an
	 * attempt is made to create the directory. 
	 * 
	 * @param loc String representation of a directory path
	 * @return boolean value indicating if location exists
	 */
	public static boolean dirExists(String loc) {
		Path path = Paths.get(loc);
		if (Files.exists(path) && Files.isDirectory(path))
			return true;
		else {
			try {
				Files.createDirectories(path);
				return true;
			} catch (IOException e) { 
				System.err.println("Error " + e.getMessage());
				return false;
			}
		}
	}
	
	/**
	 * This method returns a String representation of the default
	 * directory. If the directory does not exist, an attempt is
	 * made to create it. 
	 * 
	 * @return String representation of default directory
	 */
	public static String mkDefaultDir() {
		String path = System.getProperty("user.dir") + File.separator 
					+ "MovedFiles" + File.separator;
		if (dirExists(path))
			return path;
		try {
			Files.createDirectory(Paths.get(path));
		}  catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(0);
		}
		return path;
	}
	
	/**
	 * This method returns the Path of a file when provided
	 * the String representation of its location. 
	 * 
	 * @param loc String representation of a file path
	 * @return Path representation of given file
	 */
	public static Path getFile(String loc) {
		Path path = Paths.get(loc);
		return path;
	}
	
	public static String getFilePath(String fileName) {
		fileName = fileName.replace("\\", "/");
		String[] seg = fileName.split("/");
		StringBuilder path = new StringBuilder();
		for (int i = 0; i < seg.length; i++) {
			if (i == seg.length-1)
				break;
			path.append(seg[i] + File.separator);
		}
		return path.toString();
	}
	
	/**
	 * This method returns file name and the file's extension
	 * when provided with a complete location of the file.
	 * 
	 * @param fileName String representation of file path
	 * @return String representation of file name and extension
	 */
	public static String getFileName(String fileName) {
		fileName = fileName.replace("\\", "/");
		String[] seg = fileName.split("/");
		String name = seg[seg.length-1];
		return name;
	}
	
	/**
	 * This method returns file name only when provided with a
	 * location of the file. 
	 * 
	 * @param fileName String representation of file path
	 * @return String representation of file name
	 */
	public static String getFileNameNoExtension(String fileName) {
		fileName = fileName.replace("\\", "/");
		String[] seg = fileName.split("/");
		String name = seg[seg.length-1];
		String[] nameExt = name.split("\\.");
		return nameExt[0];
	}
	
	/**
	 * This method returns file extension only when provided
	 * with a location of the file. 
	 * 
	 * @param fileName String representation of file path
	 * @return String representation of file extension
	 */
	public static String getFileExtension(String fileName) {
		String[] seg = fileName.split("\\.");
		return seg[seg.length-1];
	}
	
	/**
	 * This method creates a file to a given location and writes 
	 * to it a given message.
	 * 
	 * @param loc String representation of output file location
	 * @param msg message to output to file
	 * @return value indicating if write operation was successful
	 */
	public static boolean saveFile(String loc, String msg) {
		Path path = Paths.get(loc);
		try {
			Files.write(path, msg.getBytes());
			return true;
		} catch (Exception e) { return false; }
	}
	
	/**
	 * This method moves a file, given its original location, to
	 * a given destination. 
	 * 
	 * @param path String representation of file path
	 * @param dest String representation of output file path
	 * @return value indicating if move operation was successful
	 */
	public static boolean moveFile(String path, String dest) {
		try {
			String newDest = dest + File.separator + getFileName(path);
			if (!Files.exists(Paths.get(newDest)))
				Files.move(getFile(path), Paths.get(newDest));
			else {
				String name = getFileNameNoExtension(path);
				name += "-(" + appendNum++ + ")." + getFileExtension(path);
				dest += name;
				Files.move(getFile(path), Paths.get(dest));
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * This method attempts to delete a file, given only the
	 * String representation of the file path.
	 * 
	 * @param path String representation of file path
	 * @return value indicating if delete operation was successful
	 */
	public static boolean deleteFile(String path) {
		try {
			Files.deleteIfExists(getFile(path));
			return true;
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
}
