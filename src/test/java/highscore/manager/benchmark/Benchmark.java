package highscore.manager.benchmark;

import static highscore.manager.benchmark.BenchmarkUtils.getUsedHeapMb;
import static highscore.manager.benchmark.BenchmarkUtils.randomInt;

import java.io.IOException;

import highscore.manager.Start;
import highscore.manager.IT.http.client.HighscoreManagerHttpClient;
import highscore.manager.service.EncodedSessionKeyService;

public class Benchmark {

	public static void main(String[] args) throws InterruptedException, IOException {
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		//8 threads
		System.out.println("Benchmark");
		
//		login10UpdateRead(1_000_000, 20_000, 400);
		
		//5-30 Mb, 30 sec
		login10UpdateRead(1000, 200, 4);
	}
	
	private static void login10UpdateRead(int repetitions, int users, int levels) throws IOException, InterruptedException {
		System.gc();
		Start.main(null);
		HighscoreManagerHttpClient httpClient = new HighscoreManagerHttpClient("localhost:8123/", true, 1000);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			if( i % 10 == 0) {
				System.out.println("Done repetitions: " + i + ". Used heap: " + getUsedHeapMb() + " Mb. Passed " + (System.currentTimeMillis() - st) + " Ms");
			}
			String sessionKey = httpClient.login(randomInt(users)).getBody();
			if(sessionKey.length() != EncodedSessionKeyService.CHARACTER_COUNT) {
				System.out.println("Bad session key :" + sessionKey);
			}
			int levelId = randomInt(levels);
			for(int j = 0; j < 10; j++) {
				httpClient.updateHighscore(levelId, sessionKey.toCharArray(), randomInt());
//				System.out.println(httpClient.getHighscore(levelId).getBody());
				httpClient.getHighscore(levelId).getBody();
			}
		}
		long et = System.currentTimeMillis();
		System.gc();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.out.println("login update read " + repetitions + " : " + (et - st) + " ms");
	}
}
