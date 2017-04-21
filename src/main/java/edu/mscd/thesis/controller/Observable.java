package edu.mscd.thesis.controller;

public interface Observable<T> {
	
	public void attachObserver(Observer<T> obs);

	public void detachObserver(Observer<T> obs);

	public void notifyObserver(T newState);

}
