package highscore.manager.IT.http.server;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.mashape.unirest.http.HttpResponse;

import highscore.manager.IT.IntegrationTestCategory;
import highscore.manager.IT.IntegrationTestWirer;
import highscore.manager.IT.http.client.HighscoreManagerHttpClient;
import highscore.manager.http.server.HighscoreManagerHttpServer;
import highscore.manager.http.server.controller.AbstractController.HttpResponseCode;
import highscore.manager.service.EncodedSessionKeyService;
import highscore.manager.service.datastructure.IntHeap;
import highscore.manager.service.datastructure.impl.MaxIntHeap;
import highscore.manager.service.impl.HighscoresFormatterServiceImpl;
import highscore.manager.wiring.Wirer;

@Category(IntegrationTestCategory.class)
public class HighscoreManagerHttpServerIntegrationTest {
	
	private HighscoreManagerHttpClient httpClient;
	private HighscoreManagerHttpServer httpServer;
	
	@Before
	public void init() throws IOException {
		Wirer<HighscoreManagerHttpServer> wirer = new IntegrationTestWirer();
		httpServer = wirer.wire(new HashMap<>());
		httpServer.start();
		httpClient = new HighscoreManagerHttpClient("localhost:8888/");
	}
	
	@After
	public void after() {
		httpServer.stop();
	}

	@Test
	public void WHEN_fringe_values_for_user_id_received_THEN_session_key_returned() {
		HttpResponse<String> response = httpClient.login(0);
		assertThat((short)response.getStatus(), equalTo(HttpResponseCode.OK.getCode()));
		assertThat((byte)response.getBody().length(), equalTo(EncodedSessionKeyService.CHARACTER_COUNT));
		
		response = httpClient.login(Integer.MAX_VALUE);
		assertThat((short)response.getStatus(), equalTo(HttpResponseCode.OK.getCode()));
		assertThat((byte)response.getBody().length(), equalTo(EncodedSessionKeyService.CHARACTER_COUNT));		
	}
	
	@Test
	public void WHEN_out_of_range_values_for_user_id_received_THEN_http_error_returned() {
		HttpResponse<String> response = httpClient.login(-1);
		assertThat((short)response.getStatus(), equalTo(HttpResponseCode.NOT_FOUND.getCode()));
		
		response = httpClient.login(10 + Integer.MAX_VALUE);
		assertThat((short)response.getStatus(), equalTo(HttpResponseCode.NOT_FOUND.getCode()));
	}
	
	@Test
	public void WHEN_fringe_values_for_highscore_update_received_THEN_no_error() {
		String sessionKey = httpClient.login(0).getBody();
		HttpResponse<String> response = httpClient.updateHighscore(0, sessionKey.toCharArray(), 0);
		assertThat((short)response.getStatus(), equalTo(HttpResponseCode.OK.getCode()));
		assertThat(response.getBody().length(), equalTo(0));
		
		sessionKey = httpClient.login(Integer.MAX_VALUE).getBody();
		response = httpClient.updateHighscore(Integer.MAX_VALUE, sessionKey.toCharArray(), Integer.MAX_VALUE);
		assertThat((short)response.getStatus(), equalTo(HttpResponseCode.OK.getCode()));
		assertThat(response.getBody().length(), equalTo(0));		
	}
	
	@Test
	public void WHEN_update_with_invalid_session_key_THEN_error() {
		HttpResponse<String> response = httpClient.updateHighscore(0, "123456".toCharArray(), 0);
		assertThat((short)response.getStatus(), equalTo(HttpResponseCode.BAD_REQUEST.getCode()));
	}
	
	@Test
	public void WHEN_highscores_for_same_level_and_user_are_updated_THEN_those_highscores_are_returned() {
		IntHeap expectedHighscores = new MaxIntHeap((byte) 15);
		Random random = new Random();
		for(int i = 0; i < 15; i++) {
			String sessionKey = httpClient.login(i).getBody();
			int highscore = random.nextInt(Integer.MAX_VALUE);
			expectedHighscores.push(highscore, i);
			httpClient.updateHighscore(1, sessionKey.toCharArray(), highscore);
		}
		
		String highscoresStr = httpClient.getHighscore(1).getBody();
		String expectedHighscoresStr = new HighscoresFormatterServiceImpl("=", ",").format(expectedHighscores);
		assertThat(highscoresStr, equalTo(expectedHighscoresStr));		
	}
}
