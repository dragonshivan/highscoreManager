package highscore.manager.IT.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import highscore.manager.IT.IntegrationTestCategory;
import highscore.manager.IT.TwoActorsStage;
import highscore.manager.IT.TwoActorsStage.ActionResult;
import highscore.manager.IT.TwoActorsStage.ActorAction;
import highscore.manager.IT.mock.AspectableIntHashMap;
import highscore.manager.IT.mock.SlowGetExpandableIntHashMapFactory;
import highscore.manager.IT.mock.SlowWriteExpandableIntHashMapFactory;
import highscore.manager.service.SessionManagementService;
import highscore.manager.service.datastructure.impl.ExpandableIntHashMapFactory;
import highscore.manager.service.impl.EncodedSessionKeyServiceImpl;
import highscore.manager.service.impl.SessionManagementServiceImpl;

@Category(IntegrationTestCategory.class)
public class SessionManagementServiceImplIntegrationTest {

	private SessionManagementServiceImpl sessionManagementServiceImpl;
	
	private TwoActorsStage twoActorsTest;
	
	@Before
	public void init() {
		sessionManagementServiceImpl = new SessionManagementServiceImpl(new EncodedSessionKeyServiceImpl(),
				1, 
				LocalDateTime.now(),
				new ExpandableIntHashMapFactory(10, 8));
		twoActorsTest = new TwoActorsStage();
	}
	
	@Test
	public void WHEN_many_new_keys_are_created_THEN_there_are_very_few_duplicate_keys() throws InterruptedException {
		//when
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		AtomicInteger collisions = new AtomicInteger(0);
		int repetitions = 1_000_000;
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		for(int i = 0; i < repetitions; i++) {
			threadPool.submit(() -> {
				String key = new String(sessionManagementServiceImpl.getNewSessionId(123));
				if(map.put(key, key) != null) {
					collisions.incrementAndGet();
				}
			});
		}
		threadPool.shutdown();
		threadPool.awaitTermination(10, TimeUnit.SECONDS);
		float collisionPercentage = (collisions.get() * 100f) / (float)repetitions;
		
		//then
		assertThat(collisionPercentage, lessThanOrEqualTo(0.035f));
	}
	
	@Test
	public void WHEN_write_is_ongoing_THEN_read_waits_for_it_to_finish() {
		//when
		Supplier<SessionManagementService> context = () -> {
			SessionManagementService service = new SessionManagementServiceImpl(new EncodedSessionKeyServiceImpl(), 
					10, LocalDateTime.now(), new SlowWriteExpandableIntHashMapFactory(10, 8));
			return service;
		};
		ActorAction<SessionManagementService, Integer> read = (sessionManagementService) -> {
			ActorAction.waitForOverlapWithSlowThread();
			return sessionManagementService.findUser("ABCDEF".toCharArray());
		};
		ActorAction<SessionManagementService, char[]> update = (sessionManagementService) -> {
			return sessionManagementService.getNewSessionId(123);
		};
		BiFunction<ActionResult<Integer>, ActionResult<char[]>, Boolean> actionsAssert = (readResult, updateResult) -> {
			return updateResult.hasOverlapped(readResult) && 
					readResult.hasFinishedAfter(updateResult);
		};
		
		//then
		twoActorsTest.act(read, update, 20, actionsAssert, context,
				AspectableIntHashMap.ThreadNames.READ_THREAD.name(), AspectableIntHashMap.ThreadNames.WRITE_THREAD.name());
	}
	
	@Test
	public void WHEN_read_is_ongoing_THEN_update_waits_for_it_to_finish() {
		//when
		Supplier<SessionManagementService> context = () -> {
			SessionManagementService service = new SessionManagementServiceImpl(new EncodedSessionKeyServiceImpl(), 
					10, LocalDateTime.now(), new SlowGetExpandableIntHashMapFactory(10, 8));
			service.getNewSessionId(111);
			service.getNewSessionId(222);
			return service;
		};
		ActorAction<SessionManagementService, Integer> read = (sessionManagementService) -> {
			return sessionManagementService.findUser("ABCDEF".toCharArray());
		};
		ActorAction<SessionManagementService, char[]> update = (sessionManagementService) -> {
			ActorAction.waitForOverlapWithSlowThread();
			return sessionManagementService.getNewSessionId(123);
		};
		BiFunction<ActionResult<Integer>, ActionResult<char[]>, Boolean> actionsAssert = (readResult, updateResult) -> {
			return updateResult.hasOverlapped(readResult) && 
					updateResult.hasFinishedAfter(readResult);
		};
		
		//then
		twoActorsTest.act(read, update, 1, actionsAssert, context,
				AspectableIntHashMap.ThreadNames.READ_THREAD.name(), AspectableIntHashMap.ThreadNames.WRITE_THREAD.name());
	}
}
