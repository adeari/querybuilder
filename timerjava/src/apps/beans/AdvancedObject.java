package apps.beans;

import java.sql.Timestamp;

public class AdvancedObject {
	private Timestamp startAt;
	private long memoryUsed;
	private long memoryMax;
	public Timestamp getStartAt() {
		return startAt;
	}
	public void setStartAt(Timestamp startAt) {
		this.startAt = startAt;
	}
	public long getMemoryUsed() {
		return memoryUsed;
	}
	public void setMemoryUsed(long memoryUsed) {
		this.memoryUsed = memoryUsed;
	}
	public long getMemoryMax() {
		return memoryMax;
	}
	public void setMemoryMax(long memoryMax) {
		this.memoryMax = memoryMax;
	}
	
	
}
