package highscore.manager.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.StampedLock;

import highscore.manager.service.EncodedSessionKeyService;
import highscore.manager.service.SessionManagementService;
import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;

//TODO this might not work right for session expiration
public class SessionManagementServiceImpl implements SessionManagementService {
	
	private static final byte SECONDS_TO_MINUTES_DIVISOR = 60;
	
	private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

	private final EncodedSessionKeyService encodedSessionKeyService;
	private final int sessionTimeoutMinutes;
	private final int serverStartTimeAsMinutesFromEpoch;
	private final IntHashMapFactory intHashMapFactory;
	
	private final Map<Integer, IntHashMap> sessionsPerStartTimeAsMinutesFromServerStart;
	
	private final StampedLock globalLock;
	
	private int lastSessionCleanUpAsMinutesFromServerStart;

	public SessionManagementServiceImpl(EncodedSessionKeyService encodedSessionKeyService, 
			int sessionTimeoutMinutes,
			LocalDateTime serverStartTime,
			IntHashMapFactory intHashMapFactory) {
		this.encodedSessionKeyService = encodedSessionKeyService;
		this.sessionTimeoutMinutes = sessionTimeoutMinutes;
		serverStartTimeAsMinutesFromEpoch = (int) (serverStartTime.toEpochSecond(ZONE_OFFSET) / SECONDS_TO_MINUTES_DIVISOR);
		lastSessionCleanUpAsMinutesFromServerStart = 0;
		this.intHashMapFactory = intHashMapFactory;
		sessionsPerStartTimeAsMinutesFromServerStart = new LinkedHashMap<>();
		globalLock = new StampedLock();
	}

	@Override
	public char[] getNewSessionId(int userId) {
		
		int encodedSessionKey = encodedSessionKeyService.generateEncoded();
		IntHashMap sessionsGeneration;
		
		long globalLockStamp = globalLock.writeLock();		
		try {			
			removeExpiredSessionsIfAny();
			sessionsGeneration = getOrCreateCurrentSessionGeneration();
			sessionsGeneration.put(encodedSessionKey, userId);
		} finally {
			globalLock.unlockWrite(globalLockStamp);
		}
		
		return encodedSessionKeyService.decode(encodedSessionKey);
	}

	@Override
	public int findUser(char[] sessionKey) {
	
		int encodedSessionKey = encodedSessionKeyService.encode(sessionKey);
		if(encodedSessionKey == EncodedSessionKeyService.INVALID_ENCODING) {
			return SessionManagementService.SYNTACTICALLY_INVALID_SESSION_KEY;
		}
		
		int userId;
				
		long globalLockStamp = globalLock.readLock();
		try {
			userId = getAssociatedUserIdFromNonexpiredSession(encodedSessionKey);
		} finally {
			globalLock.unlockRead(globalLockStamp);
		}
		
		if(userId < 0) {
			return SessionManagementService.SESSION_NOT_FOUND;
		}
		
		return userId;
	}
	
	@Override
	public SessionStats getStats() {
		long lockStamp = globalLock.readLock();
		try {
			int generationsCount = sessionsPerStartTimeAsMinutesFromServerStart.size();
			int sessionsCount = 0;
			for(IntHashMap sessoinsGeneration:sessionsPerStartTimeAsMinutesFromServerStart.values()) {
				sessionsCount += sessoinsGeneration.getSize();
			}
			return new  SessionStats(generationsCount, sessionsCount);
		} finally {
			globalLock.unlockRead(lockStamp);
		}
		
	}
	
	protected int getMinutesFromServerStart() {
		int minutesFromEpoch = (int) (LocalDateTime.now().toEpochSecond(ZONE_OFFSET) / SECONDS_TO_MINUTES_DIVISOR);
		return minutesFromEpoch - serverStartTimeAsMinutesFromEpoch;
	}
	
	private int getSessionLifeMinutes(int sessionStartTimeMinutesFromServerStart) {
		return getMinutesFromServerStart() - sessionStartTimeMinutesFromServerStart;
	}
	
	private IntHashMap getOrCreateCurrentSessionGeneration() {
		int minutesSinceServerStart = getMinutesFromServerStart();
		IntHashMap sessionsGeneration = sessionsPerStartTimeAsMinutesFromServerStart.get(minutesSinceServerStart);
		if(sessionsGeneration == null) {
			System.out.println("Creating new generation of sessions for minute : " + minutesSinceServerStart);
			sessionsGeneration = intHashMapFactory.getIntHashMap();
			sessionsPerStartTimeAsMinutesFromServerStart.put(minutesSinceServerStart, sessionsGeneration);
		}
		return sessionsGeneration;
	}
	
	private int getAssociatedUserIdFromNonexpiredSession(int encodedSessionKey) {
		int userId = IntHashMap.NOT_FOUND;
		for(Entry<Integer, IntHashMap> sessionsGenerationEntry:sessionsPerStartTimeAsMinutesFromServerStart.entrySet()) {
			int sessionStartTimeMinutesFromServerStart = sessionsGenerationEntry.getKey();
			if(getSessionLifeMinutes(sessionStartTimeMinutesFromServerStart) > sessionTimeoutMinutes) {
				continue;
			}
			IntHashMap sessionsGeneration = sessionsGenerationEntry.getValue();
			userId = sessionsGeneration.get(encodedSessionKey);
			if(userId >= 0) {
				break;
			}
		}
		return userId;
	}
	
	private void removeExpiredSessionsIfAny() {
		if(getMinutesFromServerStart() - lastSessionCleanUpAsMinutesFromServerStart < 1) {
			return;
		}
		Iterator<Map.Entry<Integer, IntHashMap>> it = sessionsPerStartTimeAsMinutesFromServerStart.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, IntHashMap> entry = it.next();
		    int sessionStartTimeMinutesFromServerStart = entry.getKey();
		    if(getSessionLifeMinutes(sessionStartTimeMinutesFromServerStart) >= sessionTimeoutMinutes) {
		    	System.out.println("Removing " + entry.getValue().getSize() + " session(s) created " + 
		    			getSessionLifeMinutes(sessionStartTimeMinutesFromServerStart) + " minute(s) ago.");
		        it.remove();
		    }
		}
		lastSessionCleanUpAsMinutesFromServerStart = getMinutesFromServerStart();
	}
}
