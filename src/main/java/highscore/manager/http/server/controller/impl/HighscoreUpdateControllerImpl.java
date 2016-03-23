package highscore.manager.http.server.controller.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import highscore.manager.http.server.controller.AbstractController;
import highscore.manager.service.EncodedSessionKeyService;
import highscore.manager.service.HighscoreService;
import highscore.manager.service.SessionManagementService;

public class HighscoreUpdateControllerImpl extends AbstractController {
	
	private static final String LEVEL_ID_ATTR = HighscoreUpdateControllerImpl.class.getSimpleName() + "_" + "LEVEL_ID_ATTR";
	private static final String USER_ID_ATTR = HighscoreUpdateControllerImpl.class.getSimpleName() + "_" + "USER_ID_ATTR";
	private static final String SCORE_ATTR = HighscoreUpdateControllerImpl.class.getSimpleName() + "_" + "SCORE_ATTR";
	
	private final HighscoreService highscoreService;
	private final SessionManagementService sessionManagementService;

	public HighscoreUpdateControllerImpl(Charset responseEncoding, HttpMethod matchingHttpMethod,
			String matchingRequestURIRegEx,
			HighscoreService highscoreService,
			SessionManagementService sessionManagementService) {
		super(responseEncoding, matchingHttpMethod, matchingRequestURIRegEx);
		this.highscoreService = highscoreService;
		this.sessionManagementService = sessionManagementService;
	}

	@Override
	public HttpResponseCode getResponseCode(HttpRequest request) {
		long levelId = (int) extractLevelId(request.getRequestURI());
		char[] sessionKey = extractsessionKey(request.getRequestURI());
		String score = extractScore(request.getRequestBody());
		
		if(!validate(levelId, sessionKey, score)) {
			return HttpResponseCode.BAD_REQUEST;
		} else {
			int userId = sessionManagementService.findUser(sessionKey);
			if(userId < 0) {
				return HttpResponseCode.BAD_REQUEST;
			}
			request.setAttribute(LEVEL_ID_ATTR, (int)levelId);
			request.setAttribute(USER_ID_ATTR, userId);
			request.setAttribute(SCORE_ATTR, Integer.parseInt(score));
			return HttpResponseCode.OK;
		}
	}
	
	@Override
	public String getResponseBody(HttpRequest request) {
		int levelId = request.getAttribute(LEVEL_ID_ATTR);
		int userId = request.getAttribute(USER_ID_ATTR);
		int score = request.getAttribute(SCORE_ATTR);
		highscoreService.update(levelId, userId, score);
		return null;
	}
	
	private boolean validate(long levelId, char[] sessionKey, String score) {
		return levelId == (int)levelId && levelId >= 0 &&
				score != null && !score.isEmpty() &&
				Long.parseLong(score) == (int)Long.parseLong(score) &&
				sessionKey != null && sessionKey.length == EncodedSessionKeyService.CHARACTER_COUNT;
	}
	
	private long extractLevelId(URI requestURI) {
		StringBuilder sb = new StringBuilder(requestURI.toString());
		int lastIndex = sb.lastIndexOf("/");
		String userIdString = sb.substring(sb.lastIndexOf("/", lastIndex - 1) + 1, lastIndex);
		return Long.parseLong(userIdString);
	}
	
	private char[] extractsessionKey(URI requestURI) {
		StringBuilder sb = new StringBuilder(requestURI.toString());
		int lastIndex = sb.lastIndexOf("?sessionkey=") + "?sessionkey=".length();
		String userIdString = sb.substring(lastIndex);
		return userIdString.toCharArray();
	}
	
	private String extractScore(InputStream requestBody) {
		try(ByteArrayOutputStream result = new ByteArrayOutputStream();) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = requestBody.read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
			return result.toString(responseEncoding.name());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}