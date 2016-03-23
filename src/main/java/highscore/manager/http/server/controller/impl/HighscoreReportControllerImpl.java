package highscore.manager.http.server.controller.impl;

import java.net.URI;
import java.nio.charset.Charset;

import highscore.manager.http.server.controller.AbstractController;
import highscore.manager.service.HighscoreService;
import highscore.manager.service.HighscoresFormatterService;
import highscore.manager.service.datastructure.IntHeap;

public class HighscoreReportControllerImpl extends AbstractController {
	
	private static final String LEVEL_ID_ATTR = HighscoreReportControllerImpl.class.getSimpleName() + "_" + "LEVEL_ID_ATTR";
	
	private final HighscoreService highscoreService;
	private final HighscoresFormatterService highscoresFormatterService;

	public HighscoreReportControllerImpl(Charset responseEncoding,
			HttpMethod matchingHttpMethod, String matchingRequestURIRegEx,
			HighscoreService highscoreService,
			HighscoresFormatterService highscoresFormatterService) {
		super(responseEncoding, matchingHttpMethod, matchingRequestURIRegEx);
		this.highscoreService = highscoreService;
		this.highscoresFormatterService = highscoresFormatterService;
	}

	@Override
	public HttpResponseCode getResponseCode(HttpRequest request) {		
		long rawLevelId = extractLevelId(request.getRequestURI());
		if(!validate(rawLevelId)) {
			return HttpResponseCode.BAD_REQUEST;
		}
		request.setAttribute(LEVEL_ID_ATTR, (int)rawLevelId);
		return HttpResponseCode.OK;
	}

	@Override
	public String getResponseBody(HttpRequest request) {
		Integer levelId = request.getAttribute(LEVEL_ID_ATTR);
		IntHeap highscores = highscoreService.getSortedHighscores(levelId);
		return highscoresFormatterService.format(highscores);
	}
	
	private boolean validate(long rawLevelId) {
		return rawLevelId == (int)rawLevelId && rawLevelId >= 0;
	}
	
	private long extractLevelId(URI requestURI) {
		StringBuilder sb = new StringBuilder(requestURI.toString());
		int lastIndex = sb.lastIndexOf("/");
		String userIdString = sb.substring(sb.lastIndexOf("/", lastIndex - 1) + 1, lastIndex);
		return Long.parseLong(userIdString);
	}
}
