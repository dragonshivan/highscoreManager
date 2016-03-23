package highscore.manager.service.impl;

import java.util.concurrent.ThreadLocalRandom;

import highscore.manager.service.EncodedSessionKeyService;

public class EncodedSessionKeyServiceImpl implements EncodedSessionKeyService {

	private static final byte RADIX = Character.MAX_RADIX;
	
	private static final String PAD_TEMPLATE = "000000";

	@Override
	public int generateEncoded() {
		return ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
	}

	@Override
	public int encode(char[] sessionKey) {
		if(sessionKey == null || sessionKey.length != CHARACTER_COUNT) {
			return INVALID_ENCODING;
		}
		int encodedKey;
		try {
			encodedKey = Integer.parseInt(new String(sessionKey).toUpperCase(), RADIX);
		} catch(NumberFormatException nfe) {
			return INVALID_ENCODING;
		}
		return encodedKey;
	}

	@Override
	public char[] decode(int encodedSessionKey) {
		String unpadded = Integer.toString(encodedSessionKey, RADIX);
		return (PAD_TEMPLATE.substring(unpadded.length()) + unpadded).toUpperCase().toCharArray();
	}
	
}