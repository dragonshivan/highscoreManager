package highscore.manager.IT.http.client;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class HighscoreManagerHttpClient {
	
	private final String serverRootURI;
	private final boolean debugMode;
	private final int longResponseThresholdMs;
	
	public HighscoreManagerHttpClient(String serverRootURI) {
		this(serverRootURI, false, Integer.MAX_VALUE);
	}
	
	public HighscoreManagerHttpClient(String serverRootURI, boolean debugMode, int longResponseThresholdMs) {
		this.serverRootURI = serverRootURI;
		Unirest.setConcurrency(1, 1);
		Unirest.setTimeouts(0, 0);
		this.debugMode = debugMode;
		this.longResponseThresholdMs = longResponseThresholdMs;
	}

	public HttpResponse<String> login(int userId) {
		long st = System.currentTimeMillis();
		HttpResponse<String> response;
		try {
			response =  Unirest.get("http://" + serverRootURI + userId + "/login").asString();
		} catch (UnirestException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		long et = System.currentTimeMillis();
		if(debugMode && (et - st) > longResponseThresholdMs) {
			System.out.println("login(" + userId + ") took " + (et - st) + " ms.");
		}
		return response;
	}
	
	public HttpResponse<String> updateHighscore(int levelId, char[] sessionKey, int score) {
		long st = System.currentTimeMillis();
		HttpResponse<String> response;
		try {
			response =  Unirest.post("http://" + serverRootURI + levelId + "/score?sessionkey=" + new String(sessionKey))
					.body(Integer.toString(score))
					.asString();
		} catch (UnirestException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		long et = System.currentTimeMillis();
		if(debugMode && (et - st) > longResponseThresholdMs) {
			System.out.println("updateHighscore(" + levelId + ", " + new String(sessionKey) + ", " + score + ") took " + (et - st) + " ms.");
		}
		return response;
	}
	
	public HttpResponse<String> getHighscore(int levelId) {
		long st = System.currentTimeMillis();
		HttpResponse<String> response;
		try {
			response = Unirest.get("http://" + serverRootURI + levelId + "/highscorelist")
					.asString();
		} catch (UnirestException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		long et = System.currentTimeMillis();
		if(debugMode && (et - st) > longResponseThresholdMs) {
			System.out.println("getHighscore(" + levelId + ") took " + (et - st) + " ms.");
		}
		return response;
	}
}
