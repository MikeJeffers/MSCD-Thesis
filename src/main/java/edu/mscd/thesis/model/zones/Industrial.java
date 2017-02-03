package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.bldgs.House;
import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Industrial extends AbstractZone {

	public Industrial(Pos2D pos, Tile tile) {
		super(pos, tile);
		// Industry zone tile-bias
		double tileValues = (tile.baseLandValue() + tile.materialValue())/2.0;
		super.setValue(super.getValue() + tileValues);
		//TODO make indy building class and sprite img
		super.setBuilding(new House(pos, Density.NONE));
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.INDUSTRIAL;
	}

	@Override
	public void draw(GraphicsContext g) {
		g.setFill(new Color(1.0, 1.0, 0, 0.5));
		super.draw(g);
	}



	@Override
	public void update() {
		// TODO add zone rule logic here to eval growth/decay of buildings in
		// zone
		// super.getBuildings().add(new House(super.getPos(), super.getTile(),
		// ZoneType.RESIDENTIAL));
		
		if (this.getTile().maxDensity().getDensityLevel() >= super.getBuilding().getDensity().getDensityLevel()) {
			if (super.getValue() > Util.GROWTH_THRESHOLD) {
				super.setBuilding(new House(this.getPos(), super.getBuilding().getDensity().getNextLevel()));
				super.setValue(super.getValue() - 1);
			}
		}
	}
}
