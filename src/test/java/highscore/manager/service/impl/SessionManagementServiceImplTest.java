package highscore.manager.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import highscore.manager.IT.mock.TimeTravelingSessionManagementServiceImpl;
import highscore.manager.service.EncodedSessionDataService;
import highscore.manager.service.EncodedSessionKeyService;
import highscore.manager.service.SessionManagementService;
import highscore.manager.service.datastructure.impl.ExpandableIntHashMapFactory;

public class SessionManagementServiceImplTest {

	private TimeTravelingSessionManagementServiceImpl sessionManagementService;
	
	@Before
	public void init() {
		EncodedSessionKeyService encodedSessionKeyService = new EncodedSessionKeyServiceImpl();
		EncodedSessionDataService encodedSessionDataService = new EncodedSessionDataServiceImpl();
		sessionManagementService = 
				new TimeTravelingSessionManagementServiceImpl(encodedSessionKeyService, 
						encodedSessionDataService, 10, LocalDateTime.now(), new ExpandableIntHashMapFactory(10, 8));
	}
	
	@Test
	public void WHEN_less_than_10_minutes_pass_THEN_session_is_valid() {
		char[] sessionKey = sessionManagementService.getNewSessionId(1);
		int user = sessionManagementService.findUser(sessionKey);
		assertThat(user, equalTo(1));
	}
	
	@Test
	public void WHEN_5_minutes_pass_THEN_session_is_still_valid() {
		char[] sessionKey = sessionManagementService.getNewSessionId(123);
		sessionManagementService.setTimeAdvancement(5);
		int user = sessionManagementService.findUser(sessionKey);
		assertThat(user, equalTo(123));
	}
	
	@Test
	public void WHEN_1_minute_passes_THEN_new_generation_of_sessions_is_created() {
		sessionManagementService.getNewSessionId(123);
		assertThat(sessionManagementService.getStats().getGenerationsCount(), equalTo(1));
		sessionManagementService.setTimeAdvancement(1);
		sessionManagementService.getNewSessionId(456);
		assertThat(sessionManagementService.getStats().getGenerationsCount(), equalTo(2));
	}
	
	@Test
	public void WHEN_11_minutes_pass_THEN_session_is_expired() {
		char[] sessionKey = sessionManagementService.getNewSessionId(123);
		int user = sessionManagementService.findUser(sessionKey);
		assertThat(user, equalTo(123));
		sessionManagementService.setTimeAdvancement(11);
		int user2 = sessionManagementService.findUser(sessionKey);
		assertThat(user2, equalTo(SessionManagementService.SESSION_NOT_FOUND));
	}
	
	@Test
	public void WHEN_valid_session_key_is_processed_THEN_corresponing_user_is_found() {
		char[] sessionKey = sessionManagementService.getNewSessionId(123);
		int user = sessionManagementService.findUser(sessionKey);
		assertThat(user, equalTo(123));
	}
	
	@Test
	public void WHEN_non_existing_session_key_is_processed_THEN_session_not_found_is_returned() {
		int user = sessionManagementService.findUser("abcdef".toCharArray());
		assertThat(user, equalTo(SessionManagementService.SESSION_NOT_FOUND));
	}
	
	@Test
	public void WHEN_syntactically_invalid_session_key_is_processed_THEN_syntactically_invalid_is_returned() {
		int user = sessionManagementService.findUser("$!%$!%".toCharArray());
		assertThat(user, equalTo(SessionManagementService.SYNTACTICALLY_INVALID_SESSION_KEY));
	}
	
}
