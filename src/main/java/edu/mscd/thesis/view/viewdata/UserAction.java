package edu.mscd.thesis.view.viewdata;

public class UserAction extends AbstractAction{
	
	public UserAction(){
		super();
	}

	@Override
	public boolean isAI() {
		return false;
	}

	@Override
	public Action copy() {
		UserAction a = new UserAction();
		a.setRadius(super.getRadius());
		a.setSquare(super.isSquare());
		a.setTarget(super.getTarget());
		a.setZoneType(super.getZoneType());
		a.setMove(super.isMove());
		return (Action) a;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof UserAction){
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
		sb.append(" move:");
		sb.append(super.isMove());
		sb.append("}");
		return sb.toString();
	}

}
