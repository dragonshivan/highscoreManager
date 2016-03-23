package highscore.manager.http.server;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import highscore.manager.http.server.controller.Controller;

public class HighscoreManagerHttpHandler implements HttpHandler {
	
	private final Controller defaultController;
	private final Controller[] controllers;
	
	public HighscoreManagerHttpHandler(Controller defaultController, Controller... controllers) {
		this.defaultController = defaultController;
		this.controllers = controllers;
	}	

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		Controller controller = defaultController;
		for(Controller c:controllers) {
			if(c.matches(httpExchange)) {
				controller = c;
				break;
			}
		}
		controller.respond(httpExchange);
	}

}
