package highscore.manager.service.impl;

import highscore.manager.service.EncodedSessionDataService;

@Deprecated
public class EncodedSessionDataServiceImpl implements EncodedSessionDataService {

	private static final byte INT_BITS_COUNT = 32;
	
	@Override
	public long encode(int userId, int sessionStartTimeMinutesFromServerStart) {
		long encodedValue = 0;
		encodedValue = (encodedValue | userId) << INT_BITS_COUNT;
		encodedValue = encodedValue | sessionStartTimeMinutesFromServerStart;
		return encodedValue;
	}

	@Override
	public int decodeUserId(long encodedSessionValue) {
		return (int) (encodedSessionValue >> INT_BITS_COUNT);
	}

	@Override
	public int decodeSessionStartTimeMinutesFromServerStart(long encodedSessionValue) {
		return (int) ((encodedSessionValue << INT_BITS_COUNT) >> INT_BITS_COUNT);
	}
}
