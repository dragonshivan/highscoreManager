package highscore.manager.http.server.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public abstract class AbstractController implements Controller {
	
	protected static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	
	private final ThreadLocal<Matcher> threadLocalMatcher;
	
	protected final Charset responseEncoding;
	private final HttpMethod matchingHttpMethod;
	private final String matchingRequestURIRegEx;
	
	public AbstractController(Charset responseEncoding, HttpMethod matchingHttpMethod, String matchingRequestURIRegEx) {
		this.responseEncoding = responseEncoding;
		this.matchingHttpMethod = matchingHttpMethod;
		this.matchingRequestURIRegEx = matchingRequestURIRegEx;
		threadLocalMatcher = new ThreadLocal<>();
	}

	@Override
	public void respond(HttpExchange httpExchange) throws IOException {
		HttpRequest request = new HttpRequest(httpExchange);
		
		Headers responseHeaders = getResponseHeaders(request);
		if(responseHeaders != null) {
			httpExchange.getResponseHeaders().putAll(responseHeaders);
		}
		
		HttpResponseCode responseCode = getResponseCode(request);
		byte[] responseBodyBytes = null;
		if(responseCode == HttpResponseCode.OK) {
			String responseBodyString = getResponseBody(request);
			responseBodyBytes = responseBodyString == null || responseBodyString.isEmpty() ? null : responseBodyString.getBytes(responseEncoding);
		} else {
			responseBodyBytes = responseCode.getMessage().getBytes(responseEncoding);
		}
		
		httpExchange.sendResponseHeaders(responseCode.getCode(), responseBodyBytes != null && responseBodyBytes.length > 0 ? responseBodyBytes.length : -1 );
		
		if(responseBodyBytes != null && responseBodyBytes.length > 0) {
			httpExchange.getResponseBody().write(responseBodyBytes);
		}
		
		httpExchange.getResponseBody().close();		
	}
	
	protected Headers getResponseHeaders(HttpRequest request) {
		Headers headers = new Headers();
		headers.add(RequestHeader.CONTENT_TYPE.getKey(), CONTENT_TYPE_TEXT_PLAIN);
		return headers;
	}
	protected abstract HttpResponseCode getResponseCode(HttpRequest request);
	protected abstract String getResponseBody(HttpRequest request);
	
	@Override
	public boolean matches(HttpExchange httpExchange) {
		if(matchingHttpMethod == null ||
				matchingHttpMethod.name().equals(httpExchange.getRequestMethod())) {
			Matcher matcher = getMatcher();
			if(matcher == null) {
				return true;
			}
			matcher.reset(httpExchange.getRequestURI().toString());
			return matcher.matches();
		}
		return false;
	}
	
	public static class HttpRequest {
		private final HttpExchange httpExchange;
		
		private HttpRequest(HttpExchange httpExchange) {
			this.httpExchange = httpExchange;
		}
		
		public Headers getRequestHeaders() {
			return httpExchange.getRequestHeaders();
		}
		
		public InputStream getRequestBody() {
			return httpExchange.getRequestBody();
		}
		
		public URI getRequestURI() {
			return httpExchange.getRequestURI();
		}
		
		public void setAttribute(String name, Object value) {
			httpExchange.setAttribute(name, value);
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getAttribute(String name) {
			return (T) httpExchange.getAttribute(name);
		}
	}
	
	protected boolean isHttpGet(HttpExchange exchange) {
		return HttpMethod.GET.name().equals(exchange.getRequestMethod());
	}
	
	private Matcher getMatcher() {
		if(matchingRequestURIRegEx != null && threadLocalMatcher.get() == null) {
			threadLocalMatcher.set(Pattern.compile(matchingRequestURIRegEx).matcher(""));
		}
		return threadLocalMatcher.get();
	}
	
	public enum HttpMethod {
		GET,POST
	}
	
	public enum HttpResponseCode {
		OK((short)200, null),
		BAD_REQUEST((short)400, "Bad request"),
		FORBIDDEN((short)403, "Forbidden"),
		NOT_FOUND((short)404, "Not found");
		
		private final short code;
		private final String message;
		
		private HttpResponseCode(short code, String message) {
			this.code = code;
			this.message = message;
		}

		public short getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}
	}
	
	public enum RequestHeader {
		
		CONTENT_TYPE("Content-Type"),
		CONTENT_LENGTH("Content-Length");
		
		private final String key;
		
		private RequestHeader(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
	}
}
