package highscore.manager.service.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import highscore.manager.service.HighscoresFormatterService;
import highscore.manager.service.datastructure.IntHeap;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl.ScoreEntry;

public class HighscoresFormatterServiceImpl implements HighscoresFormatterService {
	
	private final String valuesSeparator;
	private final String linesSeparator;
	
	public HighscoresFormatterServiceImpl(String valuesSeparator, String linesSeparator) {
		this.valuesSeparator = valuesSeparator;
		this.linesSeparator = linesSeparator;
	}

	//TODO
	@SuppressWarnings("unchecked")
	@Override
	public String format(Object highscores) {
		if(highscores == null) {
			return null;
		}
		
		if(highscores instanceof IntHeap) {
			StringBuilder sb = new StringBuilder();
			while(!((IntHeap) highscores).isEmpty()) {
				int[] highscore = ((IntHeap) highscores).pop();
				sb.append(highscore[1]).append(valuesSeparator).append(highscore[0]).append(linesSeparator);
			}
			return sb.substring(0, sb.length() - linesSeparator.length());
		} else if (highscores instanceof Stream) {
			return ((Stream<ScoreEntry>) highscores).map(scoreEntry -> scoreEntry.getUser() + valuesSeparator + scoreEntry.getScore()).collect(Collectors.joining(linesSeparator));
		}
		throw new IllegalStateException("Unknow formatting for " + highscores.getClass().getName());
	}
}
