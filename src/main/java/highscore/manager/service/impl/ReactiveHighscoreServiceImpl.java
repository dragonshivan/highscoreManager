package highscore.manager.service.impl;

import java.util.concurrent.CopyOnWriteArrayList;

import highscore.manager.service.HighscoreService;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl.Score;
import rx.Observable;
import rx.functions.Func1;

public class ReactiveHighscoreServiceImpl implements HighscoreService<Observable<Score>> {
	
	private final CopyOnWriteArrayList<Score> scores;
	private final byte maxHighscoresPerLevel;
	
	
	public ReactiveHighscoreServiceImpl(byte maxHighscoresPerLevel) {
		scores = new CopyOnWriteArrayList<>();
		this.maxHighscoresPerLevel = maxHighscoresPerLevel;
	}

	@Override
	public void update(int level, int user, int score) {
		scores.add(new Score(level, user, score));
	}

	@Override
	public Observable<Score> getSortedHighscores(int level) {
		//TODO
		return null;
	}

	@Override
	public HighscoreStats getStats() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static class Score {
		private final int level;
		private final int user;
		private final int score;
		
		public Score(int level, int user, int score) {
			this.level = level;
			this.user = user;
			this.score = score;
		}

		public int getLevel() {
			return level;
		}

		public int getUser() {
			return user;
		}

		public int getScore() {
			return score;
		}
		
		@Override
		public String toString() {
			return user + "=" + score;
		}
	}
}
