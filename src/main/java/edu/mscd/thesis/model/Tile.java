package edu.mscd.thesis.model;



import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.view.OverlayNode;
import edu.mscd.thesis.view.SelectableNode;


public interface Tile extends OverlayNode, SelectableNode{
	
	double getZoneValue();
	
	Density getZoneDensity();
	
	ZoneType getZoneType();
	
	Pos2D getPos();

	Zone getZone();

	boolean setZone(ZoneType ztype);

	boolean isZonable();

	boolean isPassable();

	double materialValue();

	double baseLandValue();
	
	/**
	 * Get the current amount of Pollution present in this tile
	 * @return pollution value [0..MAX]
	 */
	double getPollution();
	
	/**
	 * Add pollution to tile
	 * @param pollution - amount to add
	 */
	void pollute(double pollution);
	
	/**
	 * Get the current landValue accumulated on this tile
	 * @return landvalue
	 */
	double getCurrentLandValue();
	
	/**
	 * Add some amount of landvalue as a result of nearby effects
	 * @param factor - amount by which to modify landvalue
	 */
	void modifyLandValue(double factor);

	Density maxDensity();
	
	void update();
	
	TileType getType();
	
	public String getLabelText();

}
