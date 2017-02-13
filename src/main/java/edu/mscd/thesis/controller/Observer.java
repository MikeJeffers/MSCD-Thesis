package edu.mscd.thesis.controller;

/**
 * Observer for event-based notification
 * @author Mike
 *
 * @param <T> Type of data to notify observer with
 */
public interface Observer<T> {
	
	public void notifyNewData(T data);

}
