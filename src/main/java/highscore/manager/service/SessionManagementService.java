package highscore.manager.service;

public interface SessionManagementService {
	
	public static final int SESSION_NOT_FOUND = -2;
	public static final int SYNTACTICALLY_INVALID_SESSION_KEY = -3;
	
	char[] getNewSessionId(int userId);
	int findUser(char[] sessionKey);
	SessionStats getStats();
	
	public static class SessionStats {
		private final int generationsCount;
		private final int sessionsCount;
		
		public SessionStats(int generationsCount, int sessionsCount) {
			this.generationsCount = generationsCount;
			this.sessionsCount = sessionsCount;
		}

		public int getGenerationsCount() {
			return generationsCount;
		}

		public int getSessionsCount() {
			return sessionsCount;
		}
		
		@Override
		public String toString() {
			return "Sessions stats: generations=" + generationsCount + " sessions=" + sessionsCount;
		}
	}
}
