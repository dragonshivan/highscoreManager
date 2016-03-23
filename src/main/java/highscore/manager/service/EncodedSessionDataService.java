package highscore.manager.service;

public interface EncodedSessionDataService {

	public long encode(int userId, int sessionStartTimeMinutesFromServerStart);
	public int decodeUserId(long encodedSessionValue);
	public int decodeSessionStartTimeMinutesFromServerStart(long encodedSessionValue);
}
