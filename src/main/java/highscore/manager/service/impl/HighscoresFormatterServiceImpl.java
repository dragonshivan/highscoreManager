package highscore.manager.service.impl;

import highscore.manager.service.HighscoresFormatterService;
import highscore.manager.service.datastructure.IntHeap;

public class HighscoresFormatterServiceImpl implements HighscoresFormatterService<IntHeap> {
	
	private final String valuesSeparator;
	private final String linesSeparator;
	
	public HighscoresFormatterServiceImpl(String valuesSeparator, String linesSeparator) {
		this.valuesSeparator = valuesSeparator;
		this.linesSeparator = linesSeparator;
	}

	@Override
	public String format(IntHeap highscores) {
		if(highscores == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		while(!highscores.isEmpty()) {
			int[] highscore = highscores.pop();
			sb.append(highscore[1]).append(valuesSeparator).append(highscore[0]).append(linesSeparator);
		}
		return sb.substring(0, sb.length() - linesSeparator.length());
	}

}
