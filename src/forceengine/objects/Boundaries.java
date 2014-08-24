package forceengine.objects;

/**
 * You hit a wall. Usually used by <code>PhysicsEngine</code> to notify an object of a collision with a boundary of the
 * <code>PhysicsEngine</code> have collided with at least one of the boundaries of the engine.
 * <ul>
 * <li>top = 1
 * <li>right = 2
 * <li>bottom = 4
 * <li>left = 8
 * </ul>
 * 
 * @author Eric
 * 
 */
public class Boundaries {
	private int side;
	private byte type;

	public static final int top = 1;

	public static final int right = 2;
	public static final int topright = 3;

	public static final int bottom = 4;
	public static final int topbottom = 5;
	public static final int rightbottom = 6;
	public static final int toprightbottom = 7;

	public static final int left = 8;
	public static final int topleft = 9;
	public static final int rightleft = 10;
	public static final int toprightleft = 11;
	public static final int bottomleft = 12;
	public static final int topbottomleft = 13;
	public static final int rightbottomleft = 14;
	public static final int toprightbottomleft = 15;

	public static final int all = 15;

	/*
	 * public static final Boundaries topbound_0 = new Boundaries(top); public static final Boundaries rightbound_0 =
	 * new Boundaries(right); public static final Boundaries bottombound_0 = new Boundaries(bottom); public static final
	 * Boundaries leftbound_0 = new Boundaries(left); public static final Boundaries allbounds_0 = new Boundaries(all);
	 */

	public Boundaries(int side, byte type){
		this.side = side;
		this.type = type;
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Boundaries other = (Boundaries)obj;
		if(side != other.side)
			return false;
		return true;
	}

	public int getSide(){
		return side;
	}

	public byte getType(){
		return type;
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + side;
		return result;
	}

	public void setSide(int side){
		this.side = side;
	}

	public void setType(byte type){
		this.type = type;
	}

	@Override
	public String toString(){
		String str = "";
		// not exactly possible to return the list in any other order while
		// maintaining simplicity
		int s = side;
		if(s >= left){
			str += "left";
			s -= left;
			if(s > 0)
				str += ", ";
		}
		if(s >= bottom){
			str += "bottom";
			s -= bottom;
			if(s > 0)
				str += ", ";
		}
		if(s >= right){
			str += "right";
			s -= right;
			if(s > 0)
				str += ", ";
		}
		if(s >= top){
			str += "top";
			s -= top;
		}
		return str;
	}
}
