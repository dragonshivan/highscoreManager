package highscore.manager.wiring.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

public class FileConfigManager {
	
	public static final String DEFAULT_CONFIG_FILE = "highscoreManager.default.conf";
	public static final String CONFIG_FILE = "highscoreManager.conf";

	public BufferedReader getConfigFile() throws FileNotFoundException {
		File userConfigFile = new File("./" + CONFIG_FILE);
		if(userConfigFile.exists() && userConfigFile.isFile()) {
			System.out.println("Using user-provided config file: " + userConfigFile.getAbsolutePath());
			return new BufferedReader(new FileReader(userConfigFile));
		}
		System.out.println("Using default config file");
		System.out.println("This file can be overriden by creating a new file in the same folder as the jar, called " + CONFIG_FILE);
		return new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)));
	}
}
