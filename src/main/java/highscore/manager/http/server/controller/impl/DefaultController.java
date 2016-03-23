package highscore.manager.http.server.controller.impl;

import java.nio.charset.Charset;

import com.sun.net.httpserver.Headers;

import highscore.manager.http.server.controller.AbstractController;

public class DefaultController extends AbstractController {
	
	private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

	public DefaultController(Charset responseEncoding) {
		super(responseEncoding, null, null);
	}

	@Override
	public Headers getResponseHeaders(HttpRequest request) {
		Headers headers = new Headers();
		headers.add(RequestHeader.CONTENT_TYPE.getKey(), CONTENT_TYPE_TEXT_PLAIN);
		return headers;
	}

	@Override
	public HttpResponseCode getResponseCode(HttpRequest request) {
		return HttpResponseCode.NOT_FOUND;
	}

	@Override
	public String getResponseBody(HttpRequest request) {
		return null;
	}

}
