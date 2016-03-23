package highscore.manager.http.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HighscoreManagerHttpServer {
	
	private final String host;
	private final int port;
	private final String rootURL;
	private final HttpHandler httpHandler;
	private HttpServer server;
	
	public HighscoreManagerHttpServer(String host, int port, String rootURL, HttpHandler httpHandler) {
		this.host = host;
		this.port = port;
		this.rootURL = rootURL;
		this.httpHandler = httpHandler;
	}
	
	public void start() throws IOException {
		InetSocketAddress address = new InetSocketAddress(host, port);
		server = HttpServer.create(address, 0);
	    server.setExecutor(Executors.newCachedThreadPool());
	    server.createContext(rootURL, httpHandler);	    
	    server.start();
	    System.out.println("started on " + address + rootURL);
	}
	
	public void stop() {
		server.stop(1);
		System.out.println("stopped");
	}
	
}
