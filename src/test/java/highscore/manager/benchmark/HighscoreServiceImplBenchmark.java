package highscore.manager.benchmark;

import static highscore.manager.benchmark.BenchmarkUtils.getUsedHeapMb;
import static highscore.manager.benchmark.BenchmarkUtils.randomConcurrentInt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import highscore.manager.service.datastructure.impl.CappedIntHashMapFactory;
import highscore.manager.service.impl.HighscoreServiceImpl;

public class HighscoreServiceImplBenchmark {

	public static void main(String[] args) throws InterruptedException {
		//-Xmx2g -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops
		//16 threads
		System.out.println("HighscoreServiceImplBenchmark");
		
		//270	3.3
		readAndUpdateBenchmark(1_000_000);
		
		//345	2.9
		readReadAndUpdateBenchmark(1_000_000);
	}
	
	private static void readAndUpdateBenchmark(int repetitions) throws InterruptedException {
		System.gc();
		HighscoreServiceImpl service = new HighscoreServiceImpl(new CappedIntHashMapFactory((byte)15));
		ExecutorService executorService = Executors.newFixedThreadPool(16);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			executorService.execute(() -> service.getSortedHighscores(randomConcurrentInt()));
			executorService.execute(() -> service.update(randomConcurrentInt(), randomConcurrentInt(), randomConcurrentInt()));
		}
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.MINUTES);
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Read and update " + repetitions + " : " + (et - st) + " ms");
	}
	
	private static void readReadAndUpdateBenchmark(int repetitions) throws InterruptedException {
		System.gc();
		HighscoreServiceImpl service = new HighscoreServiceImpl(new CappedIntHashMapFactory((byte)15));
		ExecutorService executorService = Executors.newFixedThreadPool(16);
		long st = System.currentTimeMillis();
		for(int i = 0; i < repetitions; i++) {
			executorService.execute(() -> service.getSortedHighscores(randomConcurrentInt()));
			executorService.execute(() -> service.getSortedHighscores(randomConcurrentInt()));
			executorService.execute(() -> service.update(randomConcurrentInt(), randomConcurrentInt(), randomConcurrentInt()));
		}
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.MINUTES);
		long et = System.currentTimeMillis();
		System.out.println("Used heap : " + getUsedHeapMb() + " Mb");
		System.gc();
		System.out.println("Read Read and update " + repetitions + " : " + (et - st) + " ms");
	}
}
