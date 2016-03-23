package highscore.manager.wiring;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import highscore.manager.wiring.impl.FileConfigManager;

public interface Wirer<T> {
	
	public static final String DEFAULT_CONFIG_FILE = "highscoreManager.default.conf";
	public static final String CONFIG_FILE = "highscoreManager.conf";
	
	T wire(Map<String, String> configuration);
	
	default T wire() {
		Map<String, String> confMap = new LinkedHashMap<>();
		try(BufferedReader in = new FileConfigManager().getConfigFile()) {
			in.lines().forEach(line -> {
				if(!line.startsWith("#")) {
					String[] elems = line.split("=");
					if(elems.length >= 1 && elems[0].trim().length() > 0) {
						confMap.put(elems[0].trim(), elems.length == 2 ? elems[1].trim() : null);
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		confMap.forEach((k, v) -> System.out.println("\t" +k + " = " + v));
		return wire(confMap);
	}	
	
}
