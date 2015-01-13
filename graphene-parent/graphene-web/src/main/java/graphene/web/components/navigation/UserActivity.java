package graphene.web.components.navigation;

import java.util.Date;

import org.apache.tapestry5.annotations.Property;

public class UserActivity {
	@Property
	private final int numberTasks = 5;

	@Property
	private final int numberNotifications = 2;

	@Property
	private final int numberMessages = 3;

	public Date getCurrentTime() {
		return new Date();
	}
}
