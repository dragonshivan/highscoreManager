package highscore.manager.IT.service.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import highscore.manager.IT.IntegrationTestCategory;
import highscore.manager.IT.TwoActorsStage;
import highscore.manager.IT.TwoActorsStage.ActionResult;
import highscore.manager.IT.TwoActorsStage.ActorAction;
import highscore.manager.service.HighscoreService;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl.ScoreEntry;

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
		Supplier<HighscoreService<Stream<ScoreEntry>>> context = () -> {
			return new ReactiveHighscoreServiceImpl((byte)15);
		};
		ActorAction<HighscoreService<Stream<ScoreEntry>>, Void> read = (highscoreService) -> {
			for(int i = 0; i < 100_000; i++) {
				highscoreService.getSortedHighscores(1);
			}
			return null;
		};
		
		ActorAction<HighscoreService<Stream<ScoreEntry>>, Void> update = (highscoreService) -> {
			for(int i = 0; i < 100_000; i++) {
				highscoreService.update(1, ThreadLocalRandom.current().nextInt(5000), ThreadLocalRandom.current().nextInt(5000));
			}
			return null;
		};
		BiFunction<ActionResult<Void>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> true;
		
		//then
		twoActorsTest.act(read, update, 10, actionsAssert, context);
	}
	
	@Test
	public void WHEN_multiple_threads_update_certain_highscores_on_same_level_THEN_only_those_values_are_read() {
		//when
		Supplier<HighscoreService<Stream<ScoreEntry>>> context = () -> {
			return new ReactiveHighscoreServiceImpl((byte)15);
		};
		ActorAction<HighscoreService<Stream<ScoreEntry>>, Stream<ScoreEntry>> read = (highscoreService) -> {
			return highscoreService.getSortedHighscores(1);
		};
		
		ActorAction<HighscoreService<Stream<ScoreEntry>>, Void> update = (highscoreService) -> {
			highscoreService.update(1, 1, 11);
			highscoreService.update(1, 1, 12);
			highscoreService.update(1, 2, 21);
			highscoreService.update(1, 2, 22);
			return null;
		};
		BiFunction<ActionResult<Stream<ScoreEntry>>, ActionResult<Void>, Boolean> actionsAssert = (readResult, updateResult) -> {
			List<ScoreEntry> highscoresList = readResult.getReturnedValue().collect(Collectors.toList());
			if(highscoresList.size() == 0) {
				return true;
			}
			if(highscoresList.size() == 1) {
				return (highscoresList.get(0).getScore() == 22 && highscoresList.get(0).getUser() == 2) ||
						(highscoresList.get(0).getScore() == 21 && highscoresList.get(0).getUser() == 2) ||
						(highscoresList.get(0).getScore() == 12 && highscoresList.get(0).getUser() == 1) ||
						(highscoresList.get(0).getScore() == 11 && highscoresList.get(0).getUser() == 1);
			}
			if(highscoresList.size() == 2) {
				return (highscoresList.get(0).getUser() != highscoresList.get(1).getUser()) && 
						(highscoresList.get(0).getScore() != highscoresList.get(1).getScore());
			}
			return false;
		};
		
		//then
		twoActorsTest.act(read, update, 1_000, actionsAssert, context);
	}
}
