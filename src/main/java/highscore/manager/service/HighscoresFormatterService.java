package highscore.manager.service;

@FunctionalInterface
public interface HighscoresFormatterService<T> {

	String format(T highscores);
}
