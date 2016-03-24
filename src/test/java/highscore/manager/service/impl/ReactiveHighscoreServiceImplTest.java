package highscore.manager.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import highscore.manager.service.HighscoreService;
import highscore.manager.service.impl.ReactiveHighscoreServiceImpl.Score;
import rx.Observable;

@Ignore("reactive implementation not done yet")
public class ReactiveHighscoreServiceImplTest {

	private HighscoreService<Observable<Score>> highscoreService; 
	
	@Before
	public void init() {
		highscoreService = new ReactiveHighscoreServiceImpl((byte)3);
	}
	
	@Test
	public void testUpdateOneLevelAndUser() {
		highscoreService.update(1, 2, 15);
		highscoreService.update(1, 2, 13);
		highscoreService.update(1, 2, 16);
		Observable<Score> highscores = highscoreService.getSortedHighscores(1);
		List<Score> highscoresList = toList(highscores);
		System.out.println(highscoresList);
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
//		Collection<int[]> highscores = toCollection(highscoreService.getSortedHighscores(1));
//		assertEquals(3, highscores.size());
//		assertEquals(2, highscores.stream().findFirst().get()[1]);
//		assertEquals(16, highscores.stream().findFirst().get()[0]);
//		assertEquals(4, highscores.stream().skip(1).findFirst().get()[1]);
//		assertEquals(15, highscores.stream().skip(1).findFirst().get()[0]);
//		assertEquals(3, highscores.stream().skip(2).findFirst().get()[1]);
//		assertEquals(14, highscores.stream().skip(2).findFirst().get()[0]);
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
		
//		Collection<int[]> highscores = toCollection(highscoreService.getSortedHighscores(1));
//		assertEquals(3, highscores.size());
//		assertEquals(2, highscores.stream().findFirst().get()[1]);
//		assertEquals(16, highscores.stream().findFirst().get()[0]);
//		assertEquals(4, highscores.stream().skip(1).findFirst().get()[1]);
//		assertEquals(15, highscores.stream().skip(1).findFirst().get()[0]);
//		assertEquals(3, highscores.stream().skip(2).findFirst().get()[1]);
//		assertEquals(14, highscores.stream().skip(2).findFirst().get()[0]);
//		
//		highscores = toCollection(highscoreService.getSortedHighscores(2));
//		assertEquals(3, highscores.size());
//		assertEquals(2, highscores.stream().findFirst().get()[1]);
//		assertEquals(16, highscores.stream().findFirst().get()[0]);
//		assertEquals(4, highscores.stream().skip(1).findFirst().get()[1]);
//		assertEquals(15, highscores.stream().skip(1).findFirst().get()[0]);
//		assertEquals(3, highscores.stream().skip(2).findFirst().get()[1]);
//		assertEquals(14, highscores.stream().skip(2).findFirst().get()[0]);
	}
	
	@Test
	public void testOneLevelOneUserListNotFull() {
		highscoreService.update(1, 2, 15);
		highscoreService.update(1, 2, 13);
		highscoreService.update(1, 2, 16);		
		highscoreService.update(1, 3, 5);
		highscoreService.update(1, 3, 3);
		highscoreService.update(1, 3, 6);
		
//		Collection<int[]> highscores = toCollection(highscoreService.getSortedHighscores(1));
//		assertEquals(2, highscores.size());
//		assertEquals(16, highscores.stream().findFirst().get()[0]);
//		assertEquals(2, highscores.stream().findFirst().get()[1]);
//		assertEquals(6, highscores.stream().skip(1).findFirst().get()[0]);
//		assertEquals(3, highscores.stream().skip(1).findFirst().get()[1]);
	}
	
	@Test
	public void testSameUserImprovesScore() {
		highscoreService.update(1, 2, 15);	
		highscoreService.update(1, 3, 30);
		highscoreService.update(1, 4, 4);
		
//		Collection<int[]> highscores = toCollection(highscoreService.getSortedHighscores(1));
//		assertEquals(3, highscores.size());
//		assertEquals(30, highscores.stream().findFirst().get()[0]);
//		assertEquals(3, highscores.stream().findFirst().get()[1]);
//		assertEquals(15, highscores.stream().skip(1).findFirst().get()[0]);
//		assertEquals(2, highscores.stream().skip(1).findFirst().get()[1]);
//		assertEquals(4, highscores.stream().skip(2).findFirst().get()[0]);
//		assertEquals(4, highscores.stream().skip(2).findFirst().get()[1]);
//		
//		highscoreService.update(1, 4, 44);
//		
//		highscores = toCollection(highscoreService.getSortedHighscores(1));
//		assertEquals(3, highscores.size());
//		assertEquals(44, highscores.stream().findFirst().get()[0]);
//		assertEquals(4, highscores.stream().findFirst().get()[1]);
//		assertEquals(30, highscores.stream().skip(1).findFirst().get()[0]);
//		assertEquals(3, highscores.stream().skip(1).findFirst().get()[1]);
//		assertEquals(15, highscores.stream().skip(2).findFirst().get()[0]);
//		assertEquals(2, highscores.stream().skip(2).findFirst().get()[1]);
	}
	
	private List<Score> toList(Observable<Score> highscores) {
		List<Score> highscoresList = new ArrayList<>();
		highscores.subscribe(score -> highscoresList.add(score));
		return highscoresList;
	}
}
