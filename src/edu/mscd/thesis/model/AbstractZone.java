package edu.mscd.thesis.model;

public abstract class AbstractZone implements Zone{
	private Pos2D pos;
	
	public AbstractZone(Pos2D pos){
		this.pos = pos;
	}

	@Override
	public Pos2D getPos() {
		return this.pos.copy();
	}



}
