package forceengine.game;

public interface Living {
	/**
	 * Ages this object by the given time.
	 * 
	 * @param time the amount of time to age this object by.
	 */
	public void age(double time);
	
	/**
	 * Sets the age of this object.
	 * 
	 * @param age the age of this object.
	 */
	public void setAge(double age);
	
	/**
	 * @return Whether or not this object is alive.
	 */
	public boolean isAlive();
}
