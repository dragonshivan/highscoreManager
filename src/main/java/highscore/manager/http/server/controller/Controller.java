package highscore.manager.http.server.controller;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public interface Controller {

	default boolean matches(HttpExchange httpExchange) {
		return false;
	}
	void respond(HttpExchange exchange) throws IOException;
	
}
