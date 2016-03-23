package highscore.manager.service.impl;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.MatcherAssert.*;

import highscore.manager.service.EncodedSessionDataService;

public class EncodedSessionDataServiceImplTest {

	private EncodedSessionDataService service;
	
	@Before
	public void init() {
		service = new EncodedSessionDataServiceImpl();
	}
	
	@Test
	public void WHEN_data_has_fringe_values_THEN_decoding_returns_them() {
		//when
		long encodedValue = service.encode(Integer.MAX_VALUE, Integer.MAX_VALUE);
		//then
		assertThat(service.decodeUserId(encodedValue), equalTo(Integer.MAX_VALUE));
		assertThat(service.decodeSessionStartTimeMinutesFromServerStart(encodedValue), equalTo(Integer.MAX_VALUE));
		
		//when
		encodedValue = service.encode(Integer.MAX_VALUE, 0);
		//then
		assertThat(service.decodeUserId(encodedValue), equalTo(Integer.MAX_VALUE));
		assertThat(service.decodeSessionStartTimeMinutesFromServerStart(encodedValue), equalTo(0));
		
		//when
		encodedValue = service.encode(0, Integer.MAX_VALUE);
		//then
		assertThat(service.decodeUserId(encodedValue), equalTo(0));
		assertThat(service.decodeSessionStartTimeMinutesFromServerStart(encodedValue), equalTo(Integer.MAX_VALUE));		
	}
	
	@Test
	public void WHEN_data_is_encoded_THEN_decoding_returns_same_data() {
		//when
		long encodedValue = service.encode(123, 2);
		//then
		assertThat(service.decodeUserId(encodedValue), equalTo(123));
		assertThat(service.decodeSessionStartTimeMinutesFromServerStart(encodedValue), equalTo(2));
	}
}
