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

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class provides and displays a timed prompt to users, thereby
 * allowing them to know the program is still running.
 * 
 * @author Bryan R Martinez
 * @version 1.0
 * 
 */
public class Prompt {
	private final int CHECK = 15;
	private Timer timer;
	private int time;
	
	public Prompt(Calculate calc, int seconds) {
		time = 0;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				time++;
				if (!calc.isFinished) {
					if (time % CHECK == 0)
						System.out.println("Running, please be patient...");
				}
				else {
					System.exit(0);
				}
			}
		}, seconds * 1000);
	}
}
