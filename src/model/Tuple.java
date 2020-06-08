package model;

import java.io.Serializable;

public class Tuple<o1, o2> implements Serializable {
	private o1 first;
	private o2 second;
	
	public Tuple(o1 first, o2 second) {
		super();
		this.first = first;
		this.second = second;
	}

	public o1 getFirst() {
		return first;
	}

	public void setFirst(o1 first) {
		this.first = first;
	}

	public o2 getSecond() {
		return second;
	}

	public void setSecond(o2 second) {
		this.second = second;
	}
	
}
