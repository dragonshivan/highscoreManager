package highscore.manager.service;

import highscore.manager.service.datastructure.IntHeap;

public interface HighscoresFormatterService {

	String format(IntHeap highscores);
}
