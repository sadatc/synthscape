package com.synthverse.util;

import org.jfugue.player.Player;
import org.jfugue.rhythm.Rhythm;

public class SoundUtils {

	static Player player = new Player();
	static Rhythm click1 = new Rhythm().addLayer("S");
	static Rhythm click2 = new Rhythm().addLayer("``");
	static Rhythm click3 = new Rhythm().addLayer("SS");

	public static void playSendSignal() {
		player.play(click1.getPattern());
	}

	public static void playReceiveSignal() {
		player.play(click3.getPattern());
	}

	public static void playCollectedResource() {
		//player.play("V0 E5s D#5s | E5s D#5s E5s B4s D5s C5s");
		player.play("I[Rock_Organ] D");
		//player.play("T[Vivace] I[Rock_Organ] Db4minH C5majW C4maj^^");
	}

	public static void playTest() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			playCollectedResource();
			playSendSignal();
			//playReceiveSignal();
			
			Thread.sleep(100);

		}
		System.exit(1);
	}
	public static void dummy() {
		/*
		 * Rhythm rhythm = new
		 * Rhythm().addLayer("++++++++O..oO...O..oOO..").addLayer(
		 * "..S...S...S...S.")
		 * .addLayer("````````````````").addLayer("...............+");
		 */

		/*
		 * Rhythm rhythm = new Rhythm().addLayer("OOOO....oooo````++++SSSS");
		 * player.play(rhythm.getPattern());
		 * 
		 * new Player().play(new ChordProgression("I IV vi V").eachChordAs(
		 * "$_i $_i Ri $_i"), new Rhythm().addLayer("..X...X...X...XO"));
		 */

		/*
		 * ChordProgression cp = new ChordProgression("I IV V");
		 * player.play(cp.eachChordAs("$0q $1q $2q Rq"));
		 * 
		 * player.play(cp.allChordsAs("$0q $0q $0q $0q $1q $1q $2q $0q"));
		 * 
		 * player.play(cp.allChordsAs("$0 $0 $0 $0 $1 $1 $2 $0").eachChordAs(
		 * "V0 $0s $1s $2s Rs V1 $_q"));
		 */

		Rhythm sendingTrailSound = new Rhythm().addLayer("OoOSSS````XXXX");
		player.play(sendingTrailSound.getPattern());

		/*
		 * new Player().play(new ChordProgression("I IV vi V").eachChordAs(
		 * "$_i $_i Ri $_i"), new Rhythm().addLayer("..X...X...X...XO"));
		 * 
		 * 
		 * player.play("C D E");
		 */
	}
}
