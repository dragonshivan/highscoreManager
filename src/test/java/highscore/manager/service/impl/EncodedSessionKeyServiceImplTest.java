package highscore.manager.service.impl;

import static org.junit.Assert.*;

import java.util.TreeSet;
import java.util.function.Supplier;

import org.junit.Test;

import highscore.manager.service.EncodedSessionKeyService;

public class EncodedSessionKeyServiceImplTest {
	
	private Supplier<EncodedSessionKeyService> testInstanceSupplier = EncodedSessionKeyServiceImpl::new;

	@Test
	public void testCollisions() {
		int repetitions = 1_000_000;
		TreeSet<Integer> collisionsChecker = new TreeSet<>();
		int collisionsCount = 0;
		EncodedSessionKeyService generator = testInstanceSupplier.get();
		for(int i = 0; i < repetitions; i++) {
			int encodedSessionKey = generator.generateEncoded();
			if(!collisionsChecker.add(encodedSessionKey)) {
				collisionsCount++;
			}
		}
		float collisionPercentage = (collisionsCount * 100f) / (float)repetitions;
		assertTrue(collisionPercentage < 0.035);
	}
	
	@Test
	public void testDecode() {
		EncodedSessionKeyService generator = testInstanceSupplier.get();
		for(int i = 0; i < 10_000; i++) {
			int encodedKey = generator.generateEncoded();
			String decoded0 = new String(generator.decode(encodedKey));
			for(int j = 0; j < 1_000; j++) {
				if(!decoded0.equals(new String(generator.decode(encodedKey)))) {
					throw new RuntimeException("Decoder error");
				}
			}
		}
	}
	
	@Test
	public void testEncode() {
		EncodedSessionKeyService generator = testInstanceSupplier.get();
		
		for(int i = 0; i < 10_000; i++) {
			int encodedKey0 = generator.generateEncoded();
			char[] decoded = generator.decode(encodedKey0);
			for(int j = 0; j < 1_000; j++) {
				int encodedKey = generator.encode(decoded);
				if(encodedKey0 != encodedKey) {
					assertTrue("Key: " + new String(decoded) + " Original encoded: " + Integer.toBinaryString(encodedKey0) + " != " + " subsequent encoding: " + Integer.toBinaryString(encodedKey) , false);
					throw new RuntimeException("Encoder error");
				}
			}
		}
	}
	
	@Test
	public void testKeyHasNumbersAndCapitalLettersOnly() {
		EncodedSessionKeyService generator = testInstanceSupplier.get();
		for(int i = 0; i < 10_000; i++) {
			int encodedKey = generator.generateEncoded();
			String decoded = new String(generator.decode(encodedKey));
			for(char c : decoded.toCharArray()) {
				assertTrue((c >= 48 && c <= 57) || (c >= 65 && c <= 90));
			}
		}
	}
	
	@Test
	public void testKeyLength() {
		EncodedSessionKeyService generator = testInstanceSupplier.get();
		for(int i = 0; i < 10_000; i++) {
			int encodedKey = generator.generateEncoded();
			String decoded = new String(generator.decode(encodedKey));
			assertTrue(EncodedSessionKeyService.CHARACTER_COUNT == decoded.length());
		}
	}
	
	@Test
	public void testIsDecodable() {
		EncodedSessionKeyService generator = testInstanceSupplier.get();
		String keyTooLong = "1234567890";
		assertEquals(EncodedSessionKeyService.INVALID_ENCODING, generator.encode(keyTooLong.toCharArray()));
		String keyTooShort = "1234";
		assertEquals(EncodedSessionKeyService.INVALID_ENCODING, generator.encode(keyTooShort.toCharArray()));
		String keyInvalidChar = "123456$";
		assertEquals(EncodedSessionKeyService.INVALID_ENCODING, generator.encode(keyInvalidChar.toCharArray()));
		String keyValidLowerCase = "12345a";
		assertFalse(EncodedSessionKeyService.INVALID_ENCODING == generator.encode(keyValidLowerCase.toCharArray()));
		String keyValid = "1234A0";
		assertFalse(EncodedSessionKeyService.INVALID_ENCODING == generator.encode(keyValid.toCharArray()));
	}
}
