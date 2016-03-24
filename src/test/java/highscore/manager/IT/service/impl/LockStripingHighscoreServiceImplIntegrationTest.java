package highscore.manager.IT.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
import highscore.manager.IT.mock.SlowStreamCappedIntHashMapFactory;
import highscore.manager.IT.mock.SlowWriteCappedIntHashMapFactory;
import highscore.manager.service.HighscoreService;
import highscore.manager.service.datastructure.IntHeap;
import highscore.manager.service.datastructure.impl.CappedIntHashMapFactory;
import highscore.manager.service.impl.LockStripingHighscoreServiceImpl;

@Category(IntegrationTestCategory.class)
public class LockStripingHighscoreServiceImplIntegrationTest {
	
	private TwoActorsStage twoActorsTest;
	
	@Before
	public void init() {
		twoActorsTest = new TwoActorsStage();
	}

	@Test
	public void WHEN_multiple_threads_read_and_update_random_highscores_on_same_level_THEN_no_error() {
		//when
		Supplier<HighscoreService<IntHeap>> context = () -> {
			return new LockStripingHighscoreServiceImpl(new CappedIntHashMapFactory((byte)15), 10);
		};
		ActorAction<HighscoreService<IntHeap>, Void> read = (highscoreService) -> {
			for(int i = 0; i < 100_000; i++) {
				highscoreService.getSortedHighscores(1);
			}
			return null;
		};
		
		ActorAction<HighscoreService<IntHeap>, Void> update = (highscoreService) -> {
			for(int i = 0; i < 100_000; i++) {
				highscoreService.update(1, ThreadLocalRandom.current().nextInt(5000), ThreadLocalRandom.current().nextInt(5000));
			}
			return null;
		};
		BiFunction<ActionResult<Void>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> true;
		
		//then
		twoActorsTest.act(read, update, 100, actionsAssert, context);
	}
	
	@Test
	public void WHEN_multiple_threads_update_certain_highscores_on_same_level_THEN_only_those_values_are_read() {
		//when
		Supplier<HighscoreService<IntHeap>> context = () -> {
			return new LockStripingHighscoreServiceImpl(new CappedIntHashMapFactory((byte)15), 10);
		};
		ActorAction<HighscoreService<IntHeap>, IntHeap> read = (highscoreService) -> {
			return highscoreService.getSortedHighscores(1);
		};
		
		ActorAction<HighscoreService<IntHeap>, Void> update = (highscoreService) -> {
			highscoreService.update(1, 1, 11);
			highscoreService.update(1, 1, 12);
			highscoreService.update(1, 2, 21);
			highscoreService.update(1, 2, 22);
			return null;
		};
		BiFunction<ActionResult<IntHeap>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> {
			List<int[]> highscoresList = toList(readResult.getReturnedValue());
			if(highscoresList == null) {
				return true;
			}
			if(highscoresList.size() == 1) {
				return (highscoresList.get(0)[0] == 22 && highscoresList.get(0)[1] == 2) ||
						(highscoresList.get(0)[0] == 21 && highscoresList.get(0)[1] == 2) ||
						(highscoresList.get(0)[0] == 12 && highscoresList.get(0)[1] == 1) ||
						(highscoresList.get(0)[0] == 11 && highscoresList.get(0)[1] == 1);
			}
			if(highscoresList.size() == 2) {
				return (highscoresList.get(0)[1] != highscoresList.get(1)[1]) && 
						(highscoresList.get(0)[0] != highscoresList.get(1)[0]);
			}
			return false;
		};
		
		//then
		twoActorsTest.act(read, update, 1_000, actionsAssert, context);
	}
	
	@Test
	public void WHEN_update_is_ongoing_THEN_read_on_same_level_waits_for_it_to_finish() {
		//when
		Supplier<HighscoreService<IntHeap>> context = () -> {
			HighscoreService<IntHeap> highscoreService = new LockStripingHighscoreServiceImpl(new SlowWriteCappedIntHashMapFactory((byte)15), 10);
			highscoreService.update(1, 11, 111);
			return highscoreService;
		};
		ActorAction<HighscoreService<IntHeap>, IntHeap> read = (highscoreService) -> {
			ActorAction.waitForOverlapWithSlowThread();
			IntHeap highscores = highscoreService.getSortedHighscores(1);
			highscores = highscoreService.getSortedHighscores(1);
			return highscores;
		};
		ActorAction<HighscoreService<IntHeap>, Void> update = (highscoreService) -> {
			highscoreService.update(1, 11, 333);
			return null;
		};
		BiFunction<ActionResult<IntHeap>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> {
			return updateResult.hasOverlapped(readResult) && 
					readResult.hasFinishedAfter(updateResult);
		};
		
		//then
		twoActorsTest.act(read, update, 20, actionsAssert, context,
				AspectableIntHashMap.ThreadNames.READ_THREAD.name(), AspectableIntHashMap.ThreadNames.WRITE_THREAD.name());
	}
	
	@Test
	public void WHEN_update_is_ongoing_THEN_read_on_different_level_waits_for_it_to_finish() {
		//when
		Supplier<HighscoreService<IntHeap>> context = () -> {
			HighscoreService<IntHeap> highscoreService = new LockStripingHighscoreServiceImpl(new SlowWriteCappedIntHashMapFactory((byte)15), 10);
			highscoreService.update(1, 11, 111);
			return highscoreService;
		};
		ActorAction<HighscoreService<IntHeap>, IntHeap> read = (highscoreService) -> {
			ActorAction.waitForOverlapWithSlowThread();
			IntHeap highscores = highscoreService.getSortedHighscores(2);
			highscores = highscoreService.getSortedHighscores(2);
			return highscores;
		};
		ActorAction<HighscoreService<IntHeap>, Void> update = (highscoreService) -> {
			highscoreService.update(1, 11, 333);
			return null;
		};
		BiFunction<ActionResult<IntHeap>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> {
			return updateResult.hasOverlapped(readResult) && 
					updateResult.hasFinishedAfter(readResult);
		};
		
		//then
		twoActorsTest.act(read, update, 20, actionsAssert, context,
				AspectableIntHashMap.ThreadNames.READ_THREAD.name(), AspectableIntHashMap.ThreadNames.WRITE_THREAD.name());
	}
	
	@Test
	public void WHEN_read_is_ongoing_THEN_update_on_same_level_waits_for_it_to_finish() {
		//when
		Supplier<HighscoreService<IntHeap>> context = () -> {
			HighscoreService<IntHeap> highscoreService = new LockStripingHighscoreServiceImpl(new SlowStreamCappedIntHashMapFactory((byte)15), 10);
			highscoreService.update(1, 11, 111);
			return highscoreService;
		};
		ActorAction<HighscoreService<IntHeap>, IntHeap> read = (highscoreService) -> {
			IntHeap highscores = highscoreService.getSortedHighscores(1);
			return highscores;
		};
		ActorAction<HighscoreService<IntHeap>, Void> update = (highscoreService) -> {
			ActorAction.waitForOverlapWithSlowThread();
			highscoreService.update(1, 11, 333);
			return null;
		};
		BiFunction<ActionResult<IntHeap>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> {
			return updateResult.hasOverlapped(readResult) && 
					updateResult.hasFinishedAfter(readResult);
		};
		
		//then
		twoActorsTest.act(read, update, 20, actionsAssert, context,
				AspectableIntHashMap.ThreadNames.READ_THREAD.name(), AspectableIntHashMap.ThreadNames.WRITE_THREAD.name());
	}
	
	@Test
	public void WHEN_read_is_ongoing_THEN_update_on_different_level_doesnt_wait_for_it_to_finish() {
		//when
		Supplier<HighscoreService<IntHeap>> context = () -> {
			HighscoreService<IntHeap> highscoreService = new LockStripingHighscoreServiceImpl(new SlowStreamCappedIntHashMapFactory((byte)15), 10);
			highscoreService.update(1, 11, 111);
			return highscoreService;
		};
		ActorAction<HighscoreService<IntHeap>, IntHeap> read = (highscoreService) -> {
			IntHeap highscores = highscoreService.getSortedHighscores(1);
			return highscores;
		};
		ActorAction<HighscoreService<IntHeap>, Void> update = (highscoreService) -> {
			ActorAction.waitForOverlapWithSlowThread();
			highscoreService.update(2, 11, 333);
			highscoreService.update(3, 22, 222);
			return null;
		};
		BiFunction<ActionResult<IntHeap>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> {
			return updateResult.hasOverlapped(readResult) && 
					readResult.hasFinishedAfter(updateResult);
		};
		
		//then
		twoActorsTest.act(read, update, 20, actionsAssert, context,
				AspectableIntHashMap.ThreadNames.READ_THREAD.name(), AspectableIntHashMap.ThreadNames.WRITE_THREAD.name());
	}
	
	private List<int[]> toList(IntHeap heap) {
		if(heap == null) {
			return null;
		}
		List<int[]> list = new ArrayList<>();
		while(!heap.isEmpty()) {
			list.add(heap.pop());
		}
		return list;
	}
}
