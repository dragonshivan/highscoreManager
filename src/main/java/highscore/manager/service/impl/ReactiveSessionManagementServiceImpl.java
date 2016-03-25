package highscore.manager.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

import highscore.manager.service.EncodedSessionKeyService;
import highscore.manager.service.SessionManagementService;
import highscore.manager.service.datastructure.IntHashMap;
import highscore.manager.service.datastructure.IntHashMapFactory;

public class ReactiveSessionManagementServiceImpl implements SessionManagementService {
	
	private static final byte SECONDS_TO_MINUTES_DIVISOR = 60;
	
	private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

	private final EncodedSessionKeyService encodedSessionKeyService;
	private final int sessionTimeoutMinutes;
	private final int serverStartTimeAsMinutesFromEpoch;
	
	private final Map<Integer, Map<Integer, Integer>> generations;
	
	private int lastSessionCleanUpAsMinutesFromServerStart;

	public ReactiveSessionManagementServiceImpl(EncodedSessionKeyService encodedSessionKeyService, 
			int sessionTimeoutMinutes,
			LocalDateTime serverStartTime,
			IntHashMapFactory intHashMapFactory) {
		this.encodedSessionKeyService = encodedSessionKeyService;
		this.sessionTimeoutMinutes = sessionTimeoutMinutes;
		serverStartTimeAsMinutesFromEpoch = (int) (serverStartTime.toEpochSecond(ZONE_OFFSET) / SECONDS_TO_MINUTES_DIVISOR);
		lastSessionCleanUpAsMinutesFromServerStart = 0;
		generations = new ConcurrentHashMap<>();
	}

	@Override
	public char[] getNewSessionId(int userId) {
		int encodedSessionKey = encodedSessionKeyService.generateEncoded();
		
		generations.compute(getMinutesFromServerStart(), (generationTime, latestGeneration) -> {
			if(latestGeneration == null) {
				Map<Integer, Integer> newGeneration = new ConcurrentHashMap<>();
				newGeneration.put(encodedSessionKey, userId);
				return newGeneration;
			} else {
				latestGeneration.put(encodedSessionKey, userId);
				return latestGeneration;
			}
		});
		
		//TODO remove expired generations
		
		return encodedSessionKeyService.decode(encodedSessionKey);
	}

	@Override
	public int findUser(char[] sessionKey) {
	
		int encodedSessionKey = encodedSessionKeyService.encode(sessionKey);
		if(encodedSessionKey == EncodedSessionKeyService.INVALID_ENCODING) {
			return SessionManagementService.SYNTACTICALLY_INVALID_SESSION_KEY;
		}
		
		int userId = getAssociatedUserIdFromNonexpiredSession(encodedSessionKey);
		
		if(userId < 0) {
			return SessionManagementService.SESSION_NOT_FOUND;
		}
		
		return userId;
	}
	
	@Override
	public SessionStats getStats() {
		int generationsCount = generations.size();
		int sessionsCount = 0;
		for(Map<?, ?> sessoinsGeneration:generations.values()) {
			sessionsCount += sessoinsGeneration.size();
		}
		return new  SessionStats(generationsCount, sessionsCount);
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
		IntHashMap sessionsGeneration = generations.get(minutesSinceServerStart);
		if(sessionsGeneration == null) {
			System.out.println("Creating new generation of sessions for minute : " + minutesSinceServerStart);
			sessionsGeneration = intHashMapFactory.getIntHashMap();
			generations.put(minutesSinceServerStart, sessionsGeneration);
		}
		return sessionsGeneration;
	}
	
	private int getAssociatedUserIdFromNonexpiredSession(int encodedSessionKey) {
		int userId = IntHashMap.NOT_FOUND;
		for(Entry<Integer, IntHashMap> sessionsGenerationEntry:generations.entrySet()) {
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
		Iterator<Map.Entry<Integer, IntHashMap>> it = generations.entrySet().iterator();
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
