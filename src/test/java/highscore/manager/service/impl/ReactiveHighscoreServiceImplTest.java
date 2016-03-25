package highscore.manager.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import highscore.manager.service.HighscoreService;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl.ScoreEntry;

public class ReactiveHighscoreServiceImplTest {

	private HighscoreService<Stream<ScoreEntry>> highscoreService; 
	
	@Before
	public void init() {
		highscoreService = new ReactiveHighscoreServiceImpl((byte)3);
	}
	
	@Test
	public void testUpdateOneLevelAndUser() {
		highscoreService.update(1, 2, 15);
		highscoreService.update(1, 2, 13);
		highscoreService.update(1, 2, 16);
		Stream<ScoreEntry> highscores = highscoreService.getSortedHighscores(1);
		List<ScoreEntry> highscoresList = highscores.collect(Collectors.toList());
		assertEquals(1, highscoresList.size());
		assertEquals(16, highscoresList.get(0).getScore());
		assertEquals(2, highscoresList.get(0).getUser());
	}
	
	@Test
	public void testOneLevelMultipleUsers() {
		highscoreService.update(1, 2, 15);
		highscoreService.update(1, 2, 13);
		highscoreService.update(1, 2, 16);
		highscoreService.update(1, 3, 14);
		highscoreService.update(1, 4, 15);
		Stream<ScoreEntry> highscores = highscoreService.getSortedHighscores(1);
		List<ScoreEntry> highscoresList = highscores.collect(Collectors.toList());
		assertEquals(3, highscoresList.size());
		assertEquals(2, highscoresList.stream().findFirst().get().getUser());
		assertEquals(16, highscoresList.stream().findFirst().get().getScore());
		assertEquals(4, highscoresList.stream().skip(1).findFirst().get().getUser());
		assertEquals(15, highscoresList.stream().skip(1).findFirst().get().getScore());
		assertEquals(3, highscoresList.stream().skip(2).findFirst().get().getUser());
		assertEquals(14, highscoresList.stream().skip(2).findFirst().get().getScore());
	}
	
	@Test
	public void testMultipleLevelsAndUsers() {
		highscoreService.update(1, 2, 15);
		highscoreService.update(1, 2, 13);
		highscoreService.update(1, 2, 16);
		highscoreService.update(1, 3, 14);
		highscoreService.update(1, 4, 15);
		highscoreService.update(2, 2, 15);
		highscoreService.update(2, 2, 13);
		highscoreService.update(2, 2, 16);
		highscoreService.update(2, 3, 14);
		highscoreService.update(2, 4, 15);
		
		Stream<ScoreEntry> highscores = highscoreService.getSortedHighscores(1);
		List<ScoreEntry> highscoresList = highscores.collect(Collectors.toList());
		assertEquals(3, highscoresList.size());
		assertEquals(2, highscoresList.stream().findFirst().get().getUser());
		assertEquals(16, highscoresList.stream().findFirst().get().getScore());
		assertEquals(4, highscoresList.stream().skip(1).findFirst().get().getUser());
		assertEquals(15, highscoresList.stream().skip(1).findFirst().get().getScore());
		assertEquals(3, highscoresList.stream().skip(2).findFirst().get().getUser());
		assertEquals(14, highscoresList.stream().skip(2).findFirst().get().getScore());
		
		highscores = highscoreService.getSortedHighscores(2);
		highscoresList = highscores.collect(Collectors.toList());
		assertEquals(3, highscoresList.size());
		assertEquals(2, highscoresList.stream().findFirst().get().getUser());
		assertEquals(16, highscoresList.stream().findFirst().get().getScore());
		assertEquals(4, highscoresList.stream().skip(1).findFirst().get().getUser());
		assertEquals(15, highscoresList.stream().skip(1).findFirst().get().getScore());
		assertEquals(3, highscoresList.stream().skip(2).findFirst().get().getUser());
		assertEquals(14, highscoresList.stream().skip(2).findFirst().get().getScore());
	}
	
	@Test
	public void testOneLevelOneUserListNotFull() {
		highscoreService.update(1, 2, 15);
		highscoreService.update(1, 2, 13);
		highscoreService.update(1, 2, 16);		
		highscoreService.update(1, 3, 5);
		highscoreService.update(1, 3, 3);
		highscoreService.update(1, 3, 6);
		
		Stream<ScoreEntry> highscores = highscoreService.getSortedHighscores(1);
		List<ScoreEntry> highscoresList = highscores.collect(Collectors.toList());
		assertEquals(2, highscoresList.size());
		assertEquals(16, highscoresList.stream().findFirst().get().getScore());
		assertEquals(2, highscoresList.stream().findFirst().get().getUser());
		assertEquals(6, highscoresList.stream().skip(1).findFirst().get().getScore());
		assertEquals(3, highscoresList.stream().skip(1).findFirst().get().getUser());
	}
	
	@Test
	public void testSameUserImprovesScore() {
		highscoreService.update(1, 2, 15);	
		highscoreService.update(1, 3, 30);
		highscoreService.update(1, 4, 4);
		
		Stream<ScoreEntry> highscores = highscoreService.getSortedHighscores(1);
		List<ScoreEntry> highscoresList = highscores.collect(Collectors.toList());
		assertEquals(3, highscoresList.size());
		assertEquals(30, highscoresList.stream().findFirst().get().getScore());
		assertEquals(3, highscoresList.stream().findFirst().get().getUser());
		assertEquals(15, highscoresList.stream().skip(1).findFirst().get().getScore());
		assertEquals(2, highscoresList.stream().skip(1).findFirst().get().getUser());
		assertEquals(4, highscoresList.stream().skip(2).findFirst().get().getScore());
		assertEquals(4, highscoresList.stream().skip(2).findFirst().get().getUser());
		
		highscoreService.update(1, 4, 44);
		
		highscores = highscoreService.getSortedHighscores(1);
		highscoresList = highscores.collect(Collectors.toList());
		assertEquals(3, highscoresList.size());
		assertEquals(44, highscoresList.stream().findFirst().get().getScore());
		assertEquals(4, highscoresList.stream().findFirst().get().getUser());
		assertEquals(30, highscoresList.stream().skip(1).findFirst().get().getScore());
		assertEquals(3, highscoresList.stream().skip(1).findFirst().get().getUser());
		assertEquals(15, highscoresList.stream().skip(2).findFirst().get().getScore());
		assertEquals(2, highscoresList.stream().skip(2).findFirst().get().getUser());
	}
}
