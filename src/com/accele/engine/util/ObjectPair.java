package com.accele.engine.util;

public class ObjectPair<T, U> {

	private T first;
	private U second;
	
	public ObjectPair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	
	public T getFirst() {
		return first;
	}
	
	public U getSecond() {
		return second;
	}
	
}
