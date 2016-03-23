package highscore.manager.IT.mock;

import java.time.LocalDateTime;

import highscore.manager.service.EncodedSessionDataService;
import highscore.manager.service.EncodedSessionKeyService;
import highscore.manager.service.datastructure.IntHashMapFactory;
import highscore.manager.service.impl.SessionManagementServiceImpl;

public class TimeTravelingSessionManagementServiceImpl extends SessionManagementServiceImpl {

	private int minutesInFuture ;
	
	public TimeTravelingSessionManagementServiceImpl(
			EncodedSessionKeyService encodedSessionKeyService, EncodedSessionDataService encodedSessionDataService,
			int sessionTimeoutMinutes, LocalDateTime serverStartTime,
			IntHashMapFactory intHashMapFactory) {
		super(encodedSessionKeyService, sessionTimeoutMinutes, serverStartTime,
				intHashMapFactory);
	}
	
	public int getMinutesFromServerStart() {
		return super.getMinutesFromServerStart() + minutesInFuture;
	}
	
	public void setTimeAdvancement(int minutesInFuture) {
		this.minutesInFuture = minutesInFuture; 
	}

}
