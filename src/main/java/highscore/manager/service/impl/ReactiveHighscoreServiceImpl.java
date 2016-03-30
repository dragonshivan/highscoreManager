package highscore.manager.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import highscore.manager.service.HighscoreService;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl.ScoreEntry;

public class ReactiveHighscoreServiceImpl implements HighscoreService<Stream<ScoreEntry>> {
	
	private final Map<Integer, List<ScoreEntry>> scores;
	private final byte maxHighscoresPerLevel;
	
	
	public ReactiveHighscoreServiceImpl(byte maxHighscoresPerLevel) {
		scores = new ConcurrentHashMap<>();
		this.maxHighscoresPerLevel = maxHighscoresPerLevel;
	}

	@Override
	public void update(int level, int user, int score) {
		scores.compute(level, (existingLevel, scoresOfLevel) -> {
			if(scoresOfLevel == null) {
				return newWithSingleEntry(user, score);
			} else {
				updateExisting(user, score, scoresOfLevel);
				return scoresOfLevel;
			}
		});
	}
	
	private List<ScoreEntry> newWithSingleEntry(int user, int score) {
		List<ScoreEntry> newScoresOfLevel = new CopyOnWriteArrayList<>();
		newScoresOfLevel.add(new ScoreEntry(user, score));
		return newScoresOfLevel;
	}
	
	private void updateExisting(int user, int score, List<ScoreEntry> scoresOfLevel) {
		ScoreEntry newScoreEntry = new ScoreEntry(user, score);
		int oldScoreIndex = scoresOfLevel.indexOf(newScoreEntry);
		if(oldScoreIndex != -1) {
			replaceScore(scoresOfLevel, oldScoreIndex, newScoreEntry);
		} else {
			if(scoresOfLevel.size() < maxHighscoresPerLevel) {
				scoresOfLevel.add(newScoreEntry);
			} else {
				ScoreEntry lowestScoreEntry = getLowest(scoresOfLevel);
				addNewAndRemoveLowest(scoresOfLevel, newScoreEntry, lowestScoreEntry);
			}
		}
	}
	
	private void replaceScore(List<ScoreEntry> scoresOfLevel, int oldScoreIndex, ScoreEntry newScoreEntry) {
		ScoreEntry oldScoreEntry = scoresOfLevel.get(oldScoreIndex);
		if(newScoreEntry.getScore() > oldScoreEntry.getScore()) {
			scoresOfLevel.set(oldScoreIndex, newScoreEntry);
		}
	}
	
	private void addNewAndRemoveLowest(List<ScoreEntry> scoresOfLevel, ScoreEntry newScoreEntry, ScoreEntry lowestScoreEntry) {
		if(newScoreEntry.getScore() > lowestScoreEntry.getScore()) {
			scoresOfLevel.remove(lowestScoreEntry);
			scoresOfLevel.add(newScoreEntry);
		}
	}
 
	@Override
	public Stream<ScoreEntry> getSortedHighscores(int level) {
		List<ScoreEntry> scoresOfLevel = scores.get(level);
		if(scoresOfLevel == null) {
			return Stream.empty();
		}
				
		return scoresOfLevel.stream()
				.sorted((score1, score2) -> score2.getScore() - score1.getScore());
	}

	@Override
	public HighscoreStats getStats() {
		int levelsCount = scores.size();
		long userHighscoreCount = 0;
		for(List<ScoreEntry> scoresOfLevel:scores.values()) {
			userHighscoreCount += scoresOfLevel.size();
		}
		return new HighscoreStats(levelsCount, userHighscoreCount);
	}
	
	private ScoreEntry getLowest(List<ScoreEntry> scores) {
		ScoreEntry lowest = scores.get(0);
		for(int i = 1; i < scores.size(); i++) {
			ScoreEntry current = scores.get(i); 
			if(current.getScore() < lowest.getScore()) {
				lowest = current;
			}
		}
		return lowest;
	}
	
	public static class ScoreEntry {
		private final int user;
		private int score;
		
		public ScoreEntry(int user, int score) {
			this.user = user;
			this.score = score;
		}

		public int getUser() {
			return user;
		}

		public int getScore() {
			return score;
		}
		
		@Override
		public boolean equals(Object other) {
			return other != null && other instanceof ScoreEntry && user == ((ScoreEntry)other).getUser();
		}
		
		@Override
		public int hashCode() {
			return user;
		}
		
		@Override
		public String toString() {
			return user + "=" + score;
		}
	}
}
