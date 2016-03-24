package highscore.manager.IT.service.impl;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import highscore.manager.IT.IntegrationTestCategory;
import highscore.manager.IT.TwoActorsStage;
import highscore.manager.IT.TwoActorsStage.ActionResult;
import highscore.manager.IT.TwoActorsStage.ActorAction;
import highscore.manager.service.HighscoreService;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl.Score;
import rx.Observable;

@Ignore("reactive implementation not done yet")
@Category(IntegrationTestCategory.class)
public class ReactiveHighscoreServiceImplIntegrationTest {
	
	private TwoActorsStage twoActorsTest;
	
	@Before
	public void init() {
		twoActorsTest = new TwoActorsStage();
	}

	@Test
	public void WHEN_multiple_threads_read_and_update_random_highscores_on_same_level_THEN_no_error() {
		//when
		Supplier<HighscoreService<Observable<Score>>> context = () -> {
			return new ReactiveHighscoreServiceImpl((byte)15);
		};
		ActorAction<HighscoreService<Observable<Score>>, Void> read = (highscoreService) -> {
			for(int i = 0; i < 100_000; i++) {
				highscoreService.getSortedHighscores(1);
			}
			return null;
		};
		
		ActorAction<HighscoreService<Observable<Score>>, Void> update = (highscoreService) -> {
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
		Supplier<HighscoreService<Observable<Score>>> context = () -> {
			return new ReactiveHighscoreServiceImpl((byte)15);
		};
		ActorAction<HighscoreService<Observable<Score>>, Observable<Score>> read = (highscoreService) -> {
			return highscoreService.getSortedHighscores(1);
		};
		
		ActorAction<HighscoreService<Observable<Score>>, Void> update = (highscoreService) -> {
			highscoreService.update(1, 1, 11);
			highscoreService.update(1, 1, 12);
			highscoreService.update(1, 2, 21);
			highscoreService.update(1, 2, 22);
			return null;
		};
		BiFunction<ActionResult<Observable<Score>>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> {
			//TODO
			return false;
//			List<int[]> highscoresList = toList(readResult.getReturnedValue());
//			if(highscoresList == null) {
//				return true;
//			}
//			if(highscoresList.size() == 1) {
//				return (highscoresList.get(0)[0] == 22 && highscoresList.get(0)[1] == 2) ||
//						(highscoresList.get(0)[0] == 21 && highscoresList.get(0)[1] == 2) ||
//						(highscoresList.get(0)[0] == 12 && highscoresList.get(0)[1] == 1) ||
//						(highscoresList.get(0)[0] == 11 && highscoresList.get(0)[1] == 1);
//			}
//			if(highscoresList.size() == 2) {
//				return (highscoresList.get(0)[1] != highscoresList.get(1)[1]) && 
//						(highscoresList.get(0)[0] != highscoresList.get(1)[0]);
//			}
//			return false;
		};
		
		//then
		twoActorsTest.act(read, update, 1_000, actionsAssert, context);
	}
}
