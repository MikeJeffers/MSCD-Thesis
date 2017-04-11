package edu.mscd.thesis.view;

import javafx.event.Event;
import javafx.event.EventType;

public class DataReceived extends Event{
	private static final long serialVersionUID = 1L;
	
	public DataReceived(EventType<? extends Event> eventType) {
		super(eventType);
	}

}
