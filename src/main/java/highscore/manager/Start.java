package highscore.manager;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import highscore.manager.http.server.HighscoreManagerHttpServer;
import highscore.manager.wiring.Wirer;
import highscore.manager.wiring.impl.DefaultWirer;

public class Start {

	public static void main(String[] args) throws IOException {
		displayVMArgs();
		System.out.println();
		Wirer<HighscoreManagerHttpServer> wirer = new DefaultWirer();
		HighscoreManagerHttpServer highscoreManagerHttpServer = wirer.wire();
		System.out.println();
		highscoreManagerHttpServer.start();
	}
	
	private static void displayVMArgs() {
		System.out.println("VM args:");
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		arguments.forEach(arg -> System.out.println(arg));
	}
}
