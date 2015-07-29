package com.synthverse.util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class SoundConstants {
	static final String CAPTURE_SOUND = "sounds/Hero.aiff";
	static final String SEND_SOUND = "sounds/Pop.aiff";
	static final String RECEIVE_SOUND = "sounds/Frog.aiff";
}

public enum SoundEffect {

	CAPTURE(SoundConstants.CAPTURE_SOUND), SEND(SoundConstants.SEND_SOUND), RECEIVE(SoundConstants.RECEIVE_SOUND);

	// Nested class for specifying volume
	public static enum Volume {
		MUTE, LOW, MEDIUM, HIGH
	}

	public static Volume volume = Volume.MEDIUM;

	// Each sound effect has its own clip, loaded with its own sound file.
	private Clip clip;

	// Constructor to construct each element of the enum with its own sound
	// file.
	SoundEffect(String soundFileName) {
		try {

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFileName));
			// Get a clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioInputStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	// Play or Re-play the sound effect from the beginning, by rewinding.
	public void play() {
		try {
			if (volume != Volume.MUTE) {
				if (clip.isRunning())
					clip.stop(); // Stop the player if it is still running
				clip.setFramePosition(0); // rewind to the beginning
				clip.start(); // Start playing
				while (!clip.isRunning())
					Thread.sleep(1);
				while (clip.isRunning())
					Thread.sleep(1);
			}
		} catch (Exception e) {

		}
	}

	// Optional static method to pre-load all the sound files.
	public static void init() {
		values(); // calls the constructor for all the elements
	}

	public static void testSound() {
		init();
		for (int i = 0; i < 3; i++) {
			CAPTURE.play();
			SEND.play();
			RECEIVE.play();
		}
		System.exit(1);
	}
}
