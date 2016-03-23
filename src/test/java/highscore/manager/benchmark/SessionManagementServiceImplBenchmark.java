package highscore.manager.benchmark;

import static highscore.manager.benchmark.BenchmarkUtils.getUsedHeapMb;
import static highscore.manager.benchmark.BenchmarkUtils.randomConcurrentInt;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import highscore.manager.service.EncodedSessionKeyService;
import highscore.manager.service.SessionManagementService;
import highscore.manager.service.datastructure.impl.ExpandableIntHashMapFactory;
import highscore.manager.service.impl.EncodedSessionKeyServiceImpl;
import highscore.manager.service.impl.SessionManagementServiceImpl;

public class SessionManagementServiceImplBenchmark {

	public static void main(String[] args) throws InterruptedException {
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		//16 threads
		System.out.println("SessionManagementServiceImplBenchmark");
		
		//75 4.1
		generateAndFind(1_000_000);
		
		//170 4
		generateAndFindFind(1_000_000);
	}
	
	private static void generateAndFind(int repetitions) throws InterruptedException {
		System.gc();
		EncodedSessionKeyService encodedSessionKeyService = new EncodedSessionKeyServiceImpl();
		SessionManagementService service = new SessionManagementServiceImpl(encodedSessionKeyService,
				10, LocalDateTime.now(), new ExpandableIntHashMapFactory(10, 8)); 
		ExecutorService executorService = Executors.newFixedThreadPool(16);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			executorService.execute(() -> service.getNewSessionId(randomConcurrentInt()));
			executorService.execute(() -> service.findUser(encodedSessionKeyService.decode(randomConcurrentInt())));
		}
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.MINUTES);
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Generate and Find " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void generateAndFindFind(int repetitions) throws InterruptedException {
		System.gc();
		EncodedSessionKeyService encodedSessionKeyService = new EncodedSessionKeyServiceImpl();
		SessionManagementService service = new SessionManagementServiceImpl(encodedSessionKeyService,
				10, LocalDateTime.now(), new ExpandableIntHashMapFactory(10, 8)); 
		ExecutorService executorService = Executors.newFixedThreadPool(16);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			executorService.execute(() -> service.getNewSessionId(randomConcurrentInt()));
			executorService.execute(() -> service.findUser(encodedSessionKeyService.decode(randomConcurrentInt())));
			executorService.execute(() -> service.findUser(encodedSessionKeyService.decode(randomConcurrentInt())));
		}
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.MINUTES);
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Generate and Find Find " + repetitions + " : " + (et - st) + " ms");
	}
}
