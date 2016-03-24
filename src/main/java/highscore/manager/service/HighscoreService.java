package highscore.manager.service;

public interface HighscoreService<R> {

	void update(int level, int user, int score);
	R getSortedHighscores(int level);
	HighscoreStats getStats();
	
	public static class HighscoreStats {
		private final int levelsCount;
		private final long userHighscoreCount;
		
		public HighscoreStats(int levelsCount, long userHighscoreCount) {
			this.levelsCount = levelsCount;
			this.userHighscoreCount = userHighscoreCount;
		}
		
		public int getLevelsCount() {
			return levelsCount;
		}
		
		public long getUserHighscoreCount() {
			return userHighscoreCount;
		}
		
		@Override
		public String toString() {
			return "Highscore stats: levels=" + levelsCount + " user/highscore tuples=" + userHighscoreCount;
		}
	}
}
