package highscore.manager.service;

public interface EncodedSessionKeyService {
	
	static final byte CHARACTER_COUNT = 6;
	
	static final byte INVALID_ENCODING = -1;
	
	int generateEncoded();
	int encode(char[] sessionKey);
	char[] decode(int encodedSessionKey);
}
