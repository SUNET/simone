package se.uhr.simone.core.admin.control;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FeedBlocker {

	private boolean blocked = false;

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
}
