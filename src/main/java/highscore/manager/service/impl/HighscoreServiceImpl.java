package highscore.manager.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

import highscore.manager.service.HighscoreService;
import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;
import highscore.manager.service.datastructure.IntHeap;
import highscore.manager.service.datastructure.impl.MaxIntHeap;

public class HighscoreServiceImpl implements HighscoreService {
	private Map<Integer, IntHashMap> highscoresPerLevel;
	
	private final IntHashMapFactory intHashMapFactory;
	
	private final byte maxHighscoresCount;
	
	private final StampedLock globalLock;
	
	public HighscoreServiceImpl(IntHashMapFactory intHashMapFactory) {
		highscoresPerLevel = new HashMap<>();
		this.intHashMapFactory = intHashMapFactory;
		this.maxHighscoresCount = (byte) intHashMapFactory.getCapacity();
		globalLock = new StampedLock();
	}

	@Override
	public void update(int level, int user, int score) {
		IntHashMap highscoresForLevel;
		long globalLockStamp = globalLock.writeLock();
		try {
			highscoresForLevel = highscoresPerLevel.get(level);
			if(highscoresForLevel == null) {
				highscoresForLevel = intHashMapFactory.getIntHashMap();
				highscoresPerLevel.put(level, highscoresForLevel);
			}
			if(highscoresForLevel.get(user) != IntHashMap.EMPTY_LOCATION) {
				if(score > highscoresForLevel.get(user)) {
					highscoresForLevel.put(user, score);
				} 
			} else if(highscoresForLevel.getSize() < maxHighscoresCount) {
				highscoresForLevel.put(user, score);
			} else {
				int[] lowestUserScore = getLowestHighscore(highscoresForLevel);
				if(score > lowestUserScore[1]) {
					highscoresForLevel.remove(lowestUserScore[0]);
					highscoresForLevel.put(user, score);
				}
			}
		} finally {
			globalLock.unlockWrite(globalLockStamp);
		}
	}

	@Override
	public IntHeap getSortedHighscores(int level) {
		long globalLockStamp = globalLock.readLock();
		IntHashMap highscoresForLevel;
		try {
			highscoresForLevel = highscoresPerLevel.get(level);
			if(highscoresForLevel != null) {
				highscoresForLevel = highscoresForLevel.deepCopy();
			} else {
				return null;
			}
		} finally {
			globalLock.unlockRead(globalLockStamp);
		}
		if(highscoresForLevel != null) {
			MaxIntHeap heap = new MaxIntHeap((byte) highscoresForLevel.getSize());
			highscoresForLevel.stream((u, s) -> heap.push(s, u));
			return heap;
		} else {
			return null;
		}
	}
	
	@Override
	public HighscoreStats getStats() {
		long lockStamp = globalLock.readLock();
		try {
			int levelsCount = highscoresPerLevel.size();
			long userHighscoreCount = 0;
			for(IntHashMap highscoresForLevel:highscoresPerLevel.values()) {
				userHighscoreCount += highscoresForLevel.getSize();
			}
			return new HighscoreStats(levelsCount, userHighscoreCount);
		} finally {
			globalLock.unlockRead(lockStamp);
		}
	}
	
	private int[] getLowestHighscore(IntHashMap highscoresForLevel) {
		int[] userScore = new int[]{-1, -1};
		highscoresForLevel.stream((u, s) -> {
			if(userScore[1] == -1 || s < userScore[1]) {
				userScore[0]= u;
				userScore[1]= s;
			}
		});
		return userScore;
	}
}
