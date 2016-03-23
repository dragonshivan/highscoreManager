package highscore.manager.wiring.impl;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;

import highscore.manager.http.server.HighscoreManagerHttpHandler;
import highscore.manager.http.server.HighscoreManagerHttpServer;
import highscore.manager.http.server.controller.AbstractController.HttpMethod;
import highscore.manager.http.server.controller.Controller;
import highscore.manager.http.server.controller.impl.DefaultController;
import highscore.manager.http.server.controller.impl.HighscoreReportControllerImpl;
import highscore.manager.http.server.controller.impl.HighscoreUpdateControllerImpl;
import highscore.manager.http.server.controller.impl.LoginControllerImpl;
import highscore.manager.http.server.controller.impl.StatsControllerImpl;
import highscore.manager.service.EncodedSessionKeyService;
import highscore.manager.service.HighscoreService;
import highscore.manager.service.HighscoresFormatterService;
import highscore.manager.service.SessionManagementService;
import highscore.manager.service.datastructure.IntHashMapFactory;
import highscore.manager.service.datastructure.impl.CappedIntHashMapFactory;
import highscore.manager.service.datastructure.impl.ExpandableIntHashMapFactory;
import highscore.manager.service.impl.EncodedSessionKeyServiceImpl;
import highscore.manager.service.impl.HighscoreServiceImpl;
import highscore.manager.service.impl.HighscoresFormatterServiceImpl;
import highscore.manager.service.impl.LockStripingHighscoreServiceImpl;
import highscore.manager.service.impl.SessionManagementServiceImpl;
import highscore.manager.wiring.Wirer;

public class DefaultWirer implements Wirer<HighscoreManagerHttpServer> {

	@Override
	public HighscoreManagerHttpServer wire(Map<String, String> configuration) {
		String httpServerHost = configuration.get("http.server.host");
		int httpServerPort = Integer.parseInt(configuration.get("http.server.port"));
		String httpServerUrlRoot = configuration.get("http.server.url.root");
		int httpServerMaxThreadCount = Integer.parseInt(configuration.get("http.server.max.thread.count"));
		if(httpServerMaxThreadCount <= 0) {
			httpServerMaxThreadCount = Runtime.getRuntime().availableProcessors();
			System.out.println("httpServerMaxThreadCount set to " + httpServerMaxThreadCount);
		}
		String httpServerResponseEncoding = configuration.get("http.server.response.encoding");
		int highscoresMaxPerLevel = Integer.parseInt(configuration.get("highscores.max.per.level"));
		boolean highscoresUseLockStriping = Boolean.parseBoolean(configuration.get("highscores.use.lock.striping"));
		int sessionTimeoutMinutes = Integer.parseInt(configuration.get("session.timeout.minutes"));
		
		
		IntHashMapFactory highscoreServiceIntHashMapFactory = new CappedIntHashMapFactory((byte)highscoresMaxPerLevel);

		HighscoreService highscoreService;
		if(highscoresUseLockStriping) {
			highscoreService = new LockStripingHighscoreServiceImpl(highscoreServiceIntHashMapFactory, httpServerMaxThreadCount);
		} else {
			highscoreService = new HighscoreServiceImpl(highscoreServiceIntHashMapFactory);
		}
		System.out.println("HighscoreService implementation is " + highscoreService.getClass().getSimpleName());
		
		IntHashMapFactory encodedSessionKeyServiceIntHashMapFactory = new ExpandableIntHashMapFactory(10, 8);
		EncodedSessionKeyService encodedSessionKeyService = new EncodedSessionKeyServiceImpl();
		SessionManagementService sessionManagementService = new SessionManagementServiceImpl(encodedSessionKeyService,
				sessionTimeoutMinutes, LocalDateTime.now(), encodedSessionKeyServiceIntHashMapFactory);
		
		Charset charset = Charset.forName(httpServerResponseEncoding);
		Controller defaultController = new DefaultController(charset);
		Controller loginController = new LoginControllerImpl(charset, HttpMethod.GET, ".*/[0-9]{0,10}/login", sessionManagementService);
		Controller highscoreUpdateController = new HighscoreUpdateControllerImpl(charset, HttpMethod.POST, ".*/[0-9]{0,10}/score\\?sessionkey=[A-Z0-9]{6}",
				highscoreService, sessionManagementService);
		HighscoresFormatterService highscoresFormatterService = new HighscoresFormatterServiceImpl("=", ",");
		Controller highscoreReportController = new HighscoreReportControllerImpl(charset, HttpMethod.GET, ".*/[0-9]{0,10}/highscorelist", 
				highscoreService, highscoresFormatterService);
		Controller statsController = new StatsControllerImpl(charset, HttpMethod.GET, ".*/stats", 
				sessionManagementService, highscoreService);
		
		HighscoreManagerHttpHandler highscoreManagerHttpHandler = new HighscoreManagerHttpHandler(defaultController, 
				loginController, highscoreUpdateController, highscoreReportController, statsController);
		
		HighscoreManagerHttpServer highscoreManagerHttpServer = new HighscoreManagerHttpServer(httpServerHost, httpServerPort, httpServerUrlRoot, highscoreManagerHttpHandler, 8);
		
		return highscoreManagerHttpServer;
	}

}
