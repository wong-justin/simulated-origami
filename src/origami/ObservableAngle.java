package origami;

import java.util.Observable;

public class ObservableAngle extends Observable{
	private double thisAngle;
	
	public void setAngle(double a)
	{
		synchronized (this) {
			this.thisAngle = a;
		}
		setChanged();
		notifyObservers();
	}
	
	public synchronized double getAngle()		// synchronized prevents thread interference and memory consistency errors
	{
		return thisAngle;
	}
}
