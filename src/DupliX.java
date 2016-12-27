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

/**
 * DupliX - Duplicate File Handler (Detect/Relocate/Delete)
 * <p>
 * This program detects duplicate files in a given directory. It decides
 * a file is a duplicate of another when the SHA-256 hash of each is equal.
 * By default, the program searches for files recursively (includes files
 * within inner folders).
 * <p>
 * Once all duplicates have been found, the appropriate action is taken.
 * That action depends on the user-entered command-line arguments. 
 * 
 * @author Bryan R Martinez
 * @version 1.0
 *
 */
public class DupliX {
	public static final int PROMPT_TIME = 1;
	public static void main(String[] args) {
		ArgsParser parser = new ArgsParser(args);
		if (parser.parseOK) {
			Calculate calc = new Calculate(parser);
			calc.start();
			new Prompt(calc, PROMPT_TIME);
		}
		else
			System.out.println("Invalid parse");
	}
}
