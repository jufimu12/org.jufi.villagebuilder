package org.jufi.lwjglutil;

import java.util.ArrayList;

public class SyncArrayList<T> extends ArrayList<T> {
	private static final long serialVersionUID = -5026509951762674454L;
	private final boolean SLEEPWHENBUSY;
	public boolean busy = false;
	
	public SyncArrayList(boolean sleepwhenbusy) {
		this.SLEEPWHENBUSY = sleepwhenbusy;
	}
	
	public void sadd(T e) {
		busywait();
		busy = true;
		add(e);
		busy = false;
	}
	public T sget(int index) {
		busywait();
		busy = true;
		T value = get(index);
		busy = false;
		return value;
	}
	public void sclear() {
		busywait();
		busy = true;
		clear();
		busy = false;
	}
	public boolean scontains(Object o) {
		busywait();
		busy = true;
		boolean value = contains(o);
		busy = false;
		return value;
	}
	public void sremove(int index) {
		busywait();
		busy = true;
		remove(index);
		busy = false;
	}
	public void sremove(Object o) {
		busywait();
		busy = true;
		remove(o);
		busy = false;
	}
	public int sindexOf(Object o) {
		busywait();
		busy = true;
		int value = indexOf(o);
		busy = false;
		return value;
	}
	public boolean sisEmpty() {
		busywait();
		busy = true;
		boolean value = isEmpty();
		busy = false;
		return value;
	}
	public int ssize() {
		busywait();
		busy = true;
		int value = size();
		busy = false;
		return value;
	}
	private void busywait() {
		while (busy) {
			if (SLEEPWHENBUSY) {
				Thread.yield();
			}
		}
	}
}
