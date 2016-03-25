package highscore.manager.IT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import highscore.manager.IT.mock.AspectableIntHashMap;

public class TwoActorsStage {
	
	private static final BigDecimal EXPECTED_SUCCESS_RATE = new BigDecimal("0.85");

	public <C, R1, R2> void act(ActorAction<C, R1> action1, ActorAction<C, R2> action2, 
			int repetitions, 
			BiFunction<ActionResult<R1>, ActionResult<R2>, Boolean> actionsAssert,
			Supplier<C> contextSupplier,
			String... threadNames) {
		
		AtomicInteger action1RunCount = new AtomicInteger();
		AtomicInteger action2RunCount = new AtomicInteger();
		int successfullAssertionsCount = 0;
		try {
			for(int i = 0; i < repetitions; i++) {
				C context = contextSupplier.get();
				Callable<ActionResult<R1>> task1 = () -> {
					long st = System.nanoTime();
					R1 actionResult = null;
					try {
						actionResult = action1.apply(context);
					} catch(Throwable t) {
						t.printStackTrace();
					}
					long et = System.nanoTime();
					action1RunCount.incrementAndGet();
					return new ActionResult<R1>(actionResult, st, et);
					
				};
				Callable<ActionResult<R2>> task2 = () -> {
					long st = System.nanoTime();
					R2 actionResult = null;
					try {
						actionResult = action2.apply(context);
					} catch(Throwable t) {
						t.printStackTrace();
					}
					long et = System.nanoTime();
					action2RunCount.incrementAndGet();
					return new ActionResult<R2>(actionResult, st, et);
				};
				
				FutureTask<ActionResult<R1>> action1Future = new FutureTask<>(task1);
				FutureTask<ActionResult<R2>> action2Future = new FutureTask<>(task2);
				Thread thread1 = new Thread(action1Future, threadNames.length == 0 ? "Actor1" : threadNames[0]);
				Thread thread2 = new Thread(action2Future, threadNames.length == 0 ? "Actor2" : threadNames[1]);
				thread1.start();
				thread2.start();
				ActionResult<R1> action1Result = futureGet(action1Future);
				ActionResult<R2> action2Result = futureGet(action2Future);
				
				if(actionsAssert.apply(action1Result, action2Result)) {
					successfullAssertionsCount++;
				}
			}
		} finally {
			assertEquals("Action 1 didn't do the expected number of runs", repetitions, action1RunCount.get());
			assertEquals("Action 2 didn't do the expected number of runs", repetitions, action2RunCount.get());
			assertTrue("Expected success rate not attained", calculateSuccessRate(repetitions, successfullAssertionsCount).compareTo(EXPECTED_SUCCESS_RATE) >=0);
		}
	}
	
	private BigDecimal calculateSuccessRate(int repetitions, int successfullAssertionsCount) {
		BigDecimal ratio = new BigDecimal(successfullAssertionsCount).divide(new BigDecimal(repetitions), 4, RoundingMode.HALF_EVEN);
		return ratio.setScale(2, RoundingMode.HALF_EVEN);
	}
	
	private <T> T futureGet(FutureTask<T> actionFuture) {
		try {
			return actionFuture.get();
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static interface ActorAction<C, R> extends Function<C, R> {
		
		public static void waitForOverlapWithSlowThread() {
			try {
				Thread.sleep(AspectableIntHashMap.FAST_THREAD_RECOMMENDED_DELAY_MS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static class ActionResult<R> {
		
		private final R returnedValue;
		private final long startTimeNano;
		private final long endTimeNano;
		
		public ActionResult(R returnedValue, long startTimeNano, long endTimeNano) {
			this.returnedValue = returnedValue;
			this.startTimeNano = startTimeNano;
			this.endTimeNano = endTimeNano;
		}

		public R getReturnedValue() {
			return returnedValue;
		}

		public long getStartTimeNano() {
			return startTimeNano;
		}

		public long getEndTimeNano() {
			return endTimeNano;
		}
		
		public long getDurationNano() {
			return endTimeNano - startTimeNano;
		}
		
		public boolean hasStartedBefore(ActionResult<?> otherAction) {
			return startTimeNano <= otherAction.getStartTimeNano();
		}
		
		public boolean hasFinishedAfter(ActionResult<?> otherAction) {
			return endTimeNano >= otherAction.getEndTimeNano();
		}
		
		public boolean hasStartedAfter(ActionResult<?> otherAction) {
			return startTimeNano >= otherAction.getStartTimeNano();
		}
		
		public boolean hasFinishedBefore(ActionResult<?> otherAction) {
			return endTimeNano <= otherAction.getEndTimeNano();
		}
		
		public boolean hasALongerDurationThan(ActionResult<?> otherAction) {
			if(getDurationNano() >= otherAction.getDurationNano()) {
				return true;
			}
			return getDurationNano() >= otherAction.getDurationNano();
		}
		
		public boolean hasOverlapped(ActionResult<?> otherAction) {
			return (
					( hasStartedBefore(otherAction) && hasFinishedAfter(otherAction) ) || // ---[S1---{S2---E2}---E1]---
					( hasStartedAfter(otherAction) && hasFinishedBefore(otherAction) ) || // ---{S2---[S1---E1]---E2}---
							( hasStartedBefore(otherAction) && getDurationNano() > getDuration(otherAction.getStartTimeNano(), startTimeNano) ) || // ---[S1-{S2--E1]-------
									( hasStartedAfter(otherAction) && getDurationNano() > getDuration(otherAction.getEndTimeNano(), startTimeNano) ) // ---{S2---[S1---E2}--------
					);				
		}
		
		@Override
		public String toString() {
			return returnedValue + " Start: " + startTimeNano + " End: " + endTimeNano;
		}
		
		private static long getDuration(long endTime, long startTime) {
			return endTime - startTime;
		}
	}
}
