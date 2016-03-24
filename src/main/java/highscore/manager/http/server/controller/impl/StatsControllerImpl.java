package highscore.manager.http.server.controller.impl;

import java.nio.charset.Charset;

import highscore.manager.http.server.controller.AbstractController;
import highscore.manager.service.HighscoreService;
import highscore.manager.service.SessionManagementService;

public class StatsControllerImpl extends AbstractController {
	
	private final SessionManagementService sessionManagementService;
	private final HighscoreService<?> highscoreService;
	
	public StatsControllerImpl(Charset responseEncoding, 
			HttpMethod matchingHttpMethod, String matchingRequestURIRegEx,
			SessionManagementService sessionManagementService,
			HighscoreService<?> highscoreService) {
		super(responseEncoding, matchingHttpMethod, matchingRequestURIRegEx);
		this.sessionManagementService = sessionManagementService;
		this.highscoreService = highscoreService;
	}

	@Override
	public HttpResponseCode getResponseCode(HttpRequest request) {		
		return HttpResponseCode.OK;
	}

	@Override
	public String getResponseBody(HttpRequest request) {
		return highscoreService.getStats().toString() + " | " + sessionManagementService.getStats().toString();
	}
}
