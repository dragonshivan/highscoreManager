package highscore.manager.IT;

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
import highscore.manager.service.impl.SessionManagementServiceImpl;
import highscore.manager.wiring.Wirer;

public class IntegrationTestWirer implements Wirer<HighscoreManagerHttpServer> {

	@Override
	public HighscoreManagerHttpServer wire(Map<String, String> configuration) {
		IntHashMapFactory highscoreServiceIntHashMapFactory = new CappedIntHashMapFactory((byte)15);
		
		//TODO lockPoolCount = threadPoolCount
		HighscoreService highscoreService = new HighscoreServiceImpl(highscoreServiceIntHashMapFactory);
		
		IntHashMapFactory encodedSessionKeyServiceIntHashMapFactory = new ExpandableIntHashMapFactory(10, 8);
		EncodedSessionKeyService encodedSessionKeyService = new EncodedSessionKeyServiceImpl();
		SessionManagementService sessionManagementService = new SessionManagementServiceImpl(encodedSessionKeyService,
				10, LocalDateTime.now(), encodedSessionKeyServiceIntHashMapFactory);
		
		Charset charset = Charset.forName("UTF-8");
		Controller defaultController = new DefaultController(charset);
		Controller loginController = new LoginControllerImpl(charset, HttpMethod.GET, ".*/[0-9]{0,10}/login", sessionManagementService);
		Controller highscoreUpdateController = new HighscoreUpdateControllerImpl(charset, HttpMethod.POST, ".*/[0-9]{0,10}/score\\?sessionkey=[A-Z0-9]{6}",
				highscoreService, sessionManagementService);
		HighscoresFormatterService highscoresFormatterService = new HighscoresFormatterServiceImpl("=", ",");
		Controller highscoreReportController = new HighscoreReportControllerImpl(charset, HttpMethod.GET, ".*/[0-9]{0,10}/highscorelist", 
				highscoreService, highscoresFormatterService);
		
		HighscoreManagerHttpHandler highscoreManagerHttpHandler = new HighscoreManagerHttpHandler(defaultController, 
				loginController, highscoreUpdateController, highscoreReportController);
		
		HighscoreManagerHttpServer highscoreManagerHttpServer = new HighscoreManagerHttpServer("localhost", 8888, "/", highscoreManagerHttpHandler);
		
		return highscoreManagerHttpServer;
	}

}
