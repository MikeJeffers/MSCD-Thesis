package edu.mscd.thesis.view;

import edu.mscd.thesis.model.zones.ZoneType;

public class Selection {
	private boolean isSelected;
	private ZoneType type;

	public Selection(boolean isSelected, ZoneType zone) {
		this.isSelected = isSelected;
		this.type =zone;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public ZoneType getType() {
		return type;
	}

	public void setType(ZoneType type) {
		this.type = type;
	}

}
