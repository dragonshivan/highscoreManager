package highscore.manager.http.server.controller.impl;

import java.net.URI;
import java.nio.charset.Charset;

import highscore.manager.http.server.controller.AbstractController;
import highscore.manager.service.SessionManagementService;

public class LoginControllerImpl extends AbstractController {
	
	private static final String USER_ID_ATTR = LoginControllerImpl.class.getSimpleName() + "_" + "USER_ID_ATTR";
	
	private final SessionManagementService sessionManagementService;
	
	public LoginControllerImpl(Charset responseEncoding, 
			HttpMethod matchingHttpMethod, String matchingRequestURIRegEx,
			SessionManagementService sessionManagementService) {
		super(responseEncoding, matchingHttpMethod, matchingRequestURIRegEx);
		this.sessionManagementService = sessionManagementService;
	}

	@Override
	public HttpResponseCode getResponseCode(HttpRequest request) {		
		long rawUserId = extractUserId(request.getRequestURI());
		if(!validate(rawUserId)) {
			return HttpResponseCode.BAD_REQUEST;
		}
		request.setAttribute(USER_ID_ATTR, (int)rawUserId);
		return HttpResponseCode.OK;
	}

	@Override
	public String getResponseBody(HttpRequest request) {
		Integer userId = request.getAttribute(USER_ID_ATTR);
		return new String(sessionManagementService.getNewSessionId(userId));
	}
	
	private boolean validate(long userId) {
		return userId == (int)userId && userId >= 0;
	}
	
	private long extractUserId(URI requestURI) {
		StringBuilder sb = new StringBuilder(requestURI.toString());
		int lastIndex = sb.lastIndexOf("/");
		String userIdString = sb.substring(sb.lastIndexOf("/", lastIndex - 1) + 1, lastIndex);
		return Long.parseLong(userIdString);
	}
}
