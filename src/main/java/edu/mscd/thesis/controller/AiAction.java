package edu.mscd.thesis.controller;

public class AiAction extends AbstractAction{
	
	public AiAction(){
		super();
	}

	@Override
	public boolean isAI() {
		return true;
	}

	@Override
	public Action copy() {
		AiAction a = new AiAction();
		a.setRadius(super.getRadius());
		a.setSquare(super.isSquare());
		a.setTarget(super.getTarget());
		a.setZoneType(super.getZoneType());
		a.setMove(super.isMove());
		return (Action) a;
	}
	
	@Override
	public String getLabelText(){
		return "AiMove:"+super.getLabelText();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof AiAction){
			return super.equals(other);
		}
		return false;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		sb.append("{");
		sb.append(this.getLabelText());
		sb.append("}");
		return sb.toString();
	}

}
