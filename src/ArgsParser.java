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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * This class parses user-entered command-line arguments.
 * 
 * @author Bryan R Martinez
 * @version 1.0
 *
 */
public class ArgsParser {
	private int pointer;
	private String prevArg;
	private boolean destDirDecl;
	private boolean expFileDecl;
	
	private String srcDir;
	private String destDir;
	private String expFile;
	
	private boolean recMode;
	private boolean delMode;
	private boolean moveMode;
	private boolean saveMode;
	
	public boolean parseOK;
	
	/**
	 * Initializes variables and ultimately decides whether or 
	 * not the command-line arguments led to a successful parse.
	 * 
	 * @param args user-entered command-line arguments
	 */
	public ArgsParser(String[] args) {
		srcDir = "";
		destDir = "";
		expFile = "";
		destDirDecl = false;
		expFileDecl = false;
		
		recMode = true;
		delMode = false;
		moveMode = false;
		saveMode = false;
		
		if (args.length > 0) {
			pointer = 0;
			srcDir = args[pointer];
			advPointer();
			prevArg = srcDir;
			if (IO.exists(srcDir)) {
				parseOK = true;
				if (args.length > 1) {
					recArgs(Arrays.copyOfRange(args, pointer, args.length));
					if (moveMode) {
						if (delMode)
							delMode = false;
						if (!destDirDecl)
							destDir = IO.mkDefaultDir();
					}
				}
			} else parseOK = false;
		} else parseOK = false;
	}
	
	/**
	 * This method simply advances the pointer variable.
	 */
	private void advPointer() {
		pointer++;
	}
	
	/**
	 * This method recurses through the user-entered command-line 
	 * arguments to decide which modes should be turned on. 
	 * 
	 * @param args user-entered command-line arguments
	 */
	private void recArgs(String[] args) {
		if (args.length == 0)
			return;
		
		pointer = 0;
		switch (args[pointer]) {
			case "-m":
				moveMode = true;
				prevArg = "-m";
				break;
			case "-d":
				delMode = true;
				prevArg = "-d";
				break;
			case "-s":
				saveMode = true;
				prevArg = "-s";
				break;
			case "-r":
				recMode = false;
				prevArg = "-r";
				break;
			default:
				switch (prevArg) {
					case "-m":
						destDir = args[pointer];
						prevArg = destDir;
						destDirDecl = true;
						break;
					case "-s":
						expFile = args[pointer];
						prevArg = expFile;
						expFileDecl = true;
						break;
					default:
						parseOK = false;
						break;
				}
				break;
		}
		advPointer();
		
		if (pointer < args.length)
			recArgs(Arrays.copyOfRange(args, pointer, args.length));
	}
	
	/**
	 * This method returns the path of the user-entered source directory.
	 * 
	 * @return String representation of source directory
	 */
	public String getSrcDir() {
		return srcDir;
	}
	
	/**
	 * This method returns the path of the destination directory.
	 * 
	 * @return String representation of destination directory
	 */
	public String getDestDir() {
		if (IO.dirExists(destDir))
			return destDir;
		return "";
	}
	
	/**
	 * This method returns the path of the export file.
	 * 
	 * @return String representation of export file
	 */
	public String getExpFile() {
		if (saveMode) {
			if (expFileDecl) {
				IO.dirExists(IO.getFilePath(expFile));
				return expFile;
			}
			else {
				String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				return srcDir + File.separator + "duplix-" + date.toString() + ".txt";
			}
		}
		return "";
	}
	/**
	 * This method returns a boolean value indicating whether recursive
	 * file-search mode is on or off. True for on, false for off. 
	 * 
	 * @return boolean value indicating if recursion mode is on or off
	 */
	public boolean recursiveMode() {
		return recMode;
	}
	
	/**
	 * This method returns a boolean value indicating whether 
	 * export-results-to-file (save) mode is on or off.
	 * True indicates on, false indicates off. 
	 * 
	 * @return boolean value indicating if save mode is on or off
	 */
	public boolean saveMode() {
		return saveMode;
	}
	
	/**
	 * This method returns a boolean value indicating whether
	 * file-move mode is on or off. True is on, false is off.
	 * 
	 * @return boolean value indicating if move mode is on or off
	 */
	public boolean moveMode() {
		return moveMode;
	}
	
	/**
	 * This method returns a boolean value indicating whether
	 * file-deletion mode is on or off. True is on, false is off.
	 * 
	 * @return boolean value indicating if delete mode is on or off
	 */
	public boolean deleteMode() {
		return delMode;
	}
}
