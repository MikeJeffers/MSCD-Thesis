package edu.mscd.thesis.view.viewdata;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.ZoneType;

public interface Action extends ViewData{

	public boolean isAI();
	public Pos2D getTarget();
	public ZoneType getZoneType();
	public int getRadius();
	public boolean isSquare();
	public boolean isMove();
	
	public Action copy();
	
	public String getLabelText();
	


}
