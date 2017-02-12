package edu.mscd.thesis.controller;

public interface Controller extends Observer, Runnable{
	
	public void start();
	public void stop();
	

}
