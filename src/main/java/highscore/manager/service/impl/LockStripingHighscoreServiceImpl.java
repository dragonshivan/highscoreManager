package highscore.manager.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

import highscore.manager.service.HighscoreService;
import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;
import highscore.manager.service.datastructure.IntHeap;
import highscore.manager.service.datastructure.impl.MaxIntHeap;

public class LockStripingHighscoreServiceImpl implements HighscoreService<IntHeap> {
	private Map<Integer, IntHashMap> highscoresPerLevel;
	
	private final IntHashMapFactory intHashMapFactory;
	
	private final byte maxHighscoresCount;
	
	private final StampedLock globalLock;
	
	private final StampedLock[] locksPerLevelPool;
	
	public LockStripingHighscoreServiceImpl(IntHashMapFactory intHashMapFactory, int lockPoolMaxSize) {
		highscoresPerLevel = new HashMap<>();
		this.intHashMapFactory = intHashMapFactory;
		this.maxHighscoresCount = (byte) intHashMapFactory.getCapacity();
		globalLock = new StampedLock();
		locksPerLevelPool = new StampedLock[lockPoolMaxSize];
		for(int i = 0; i < lockPoolMaxSize; i++) {
			locksPerLevelPool[i] = new StampedLock();
		}
	}

	@Override
	public void update(int level, int user, int score) {
		IntHashMap highscoresForLevel;
		Long levelLockStamp;
		long globalLockStamp = globalLock.writeLock();
		try {
			highscoresForLevel = highscoresPerLevel.get(level);
			if(highscoresForLevel == null) {
				highscoresForLevel = intHashMapFactory.getIntHashMap();
				highscoresPerLevel.put(level, highscoresForLevel);
			}
			levelLockStamp = writeLock(level);
		} finally {
			globalLock.unlockWrite(globalLockStamp);
		}
		
		try {
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
			writeUnlock(level, levelLockStamp);
		}
	}

	@Override
	public IntHeap getSortedHighscores(int level) {
		long globalLockStamp = globalLock.readLock();
		IntHashMap highscoresForLevel;
		Long levelLockStamp = null;
		try {
			highscoresForLevel = highscoresPerLevel.get(level);
			if(highscoresForLevel != null) {
				levelLockStamp = readLock(level);
			}
		} finally {
			globalLock.unlockRead(globalLockStamp);
		}
		
		try {
			if(highscoresForLevel != null) {
				MaxIntHeap heap = new MaxIntHeap((byte) highscoresForLevel.getSize());
				highscoresForLevel.stream((u, s) -> heap.push(s, u));
				return heap;
			}
			return null;
		} finally {
			if(levelLockStamp != null) {
				readUnlock(level, levelLockStamp);
			}
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
	
	private long readLock(int level) {
		return getLock(level).readLock();
	}
	
	private long writeLock(int level) {
		return getLock(level).writeLock();
	}
	
	private void readUnlock(int level, long stamp) {
		getLock(level).unlockRead(stamp);
	}
	
	private void writeUnlock(int level, long stamp) {
		getLock(level).unlockWrite(stamp);
	}
	
	private StampedLock getLock(int level) {
		return locksPerLevelPool[level % locksPerLevelPool.length];
	}
}
