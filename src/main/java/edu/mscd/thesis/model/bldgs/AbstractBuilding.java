package edu.mscd.thesis.model.bldgs;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public abstract class AbstractBuilding implements Building {
	private Pos2D pos;
	private Image image;
	private TileType tileType;
	private ZoneType zoneType;
	private Rectangle2D rect;
	private double width;
	private double height;
	private Collection<Person> occupants;
	private int maxOccupants;
	private int wealthLevel;
	private Density density;
	

	public AbstractBuilding(Pos2D pos, TileType tType, ZoneType zType, Density density) {
		this.tileType = tType;
		this.zoneType = zType;
		this.pos = pos;
		this.occupants = new HashSet<Person>();
		this.changeDensity(density);
	}

	@Override
	public double update(double growthValue) {
		int tileMaxDensity = this.getTileType().getMaxDensity().getDensityLevel();
		int currentDensityLevel = this.getDensity().getDensityLevel();
		int currentOccupancy = this.currentOccupancy();
		int maxOccupancy = this.getMaxOccupants();
		int growthCost = Rules.BASE_GROWTH_COST*currentDensityLevel;
		if(currentOccupancy>=maxOccupancy && (growthValue-growthCost)>Rules.GROWTH_THRESHOLD && tileMaxDensity>currentDensityLevel){
			this.changeDensity(getDensity().getNextLevel());
			return growthValue - growthCost;
		}else if(currentDensityLevel==0 && (growthValue-growthCost)>Rules.GROWTH_THRESHOLD){
			this.changeDensity(getDensity().getNextLevel());
			return growthValue - growthCost;
		}else if(growthValue<Rules.GROWTH_THRESHOLD){
			this.changeDensity(getDensity().getPrevLevel());
			return growthValue;
		}
		return growthValue;
	}


	@Override
	public void changeDensity(Density density) {
		this.density = density;
	}

	@Override
	public int currentOccupancy() {
		return this.occupants.size();
	}

	@Override
	public Collection<Person> getOccupants() {
		return this.occupants;
	}

	@Override
	public int getMaxOccupants() {
		return maxOccupants;
	}

	@Override
	public int getWealth() {
		return wealthLevel;
	}

	public void setWealthLevel(int wealth) {
		this.wealthLevel = wealth;
	}

	public void setMaxOccupancy(int max) {
		this.maxOccupants = max;
	}



	@Override
	public boolean setImage(URL url) {
		System.out.println(url.toString());
		try {
			this.image = new Image(url.getPath());
			if (this.image.isError()) {
				System.err.println(this.image.getException().getMessage());
			}
			return true;
		} catch (Exception e) {
			System.err.println(e);
			return false;
		}
	}

	@Override
	public boolean setImage(Image img) {
		this.image = img;
		return true;
	}

	@Override
	public Image getImage() {
		return this.image;
	}

	@Override
	public Rectangle2D getRect() {
		return this.rect;
	}

	@Override
	public double getWidth() {
		return this.width;
	}

	@Override
	public double getHeight() {
		return this.height;
	}

	@Override
	public Pos2D getPos() {
		return this.pos;
	}

	void setPos(Pos2D pos) {
		this.pos = pos;
	}

	@Override
	public Density getDensity() {
		return this.density;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Building) {
			Building b = (Building) o;
			return b.getPos().equals(this.getPos()) && b.getDensity() == this.getDensity()
					&& b.getMaxOccupants() == this.getMaxOccupants();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pos, zoneType, tileType, wealthLevel, maxOccupants);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("Building:{at=");
		sb.append(pos);
		sb.append(" maxOccupants:");
		sb.append(this.maxOccupants);
		sb.append(" currentCount:");
		sb.append(this.currentOccupancy());
		sb.append(" Occupants: ");
		for (Person p : this.getOccupants()) {
			sb.append("{");
			sb.append(p);
			sb.append("},");
		}
		sb.append("}");
		return sb.toString();
	}
	
	@Override
	public String getLabelText(){
		StringBuilder sb = new StringBuilder("");
		sb.append("\nDensity: ");
		sb.append(this.getDensity());
		sb.append("\nmaxOccupants: ");
		sb.append(this.getMaxOccupants());
		sb.append("\ncurrentCount: ");
		sb.append(this.currentOccupancy());
		return sb.toString();
	}

	public TileType getTileType() {
		return tileType;
	}

	public void setTileType(TileType tileType) {
		this.tileType = tileType;
	}

	public ZoneType getZoneType() {
		return zoneType;
	}

	public void setZoneType(ZoneType zoneType) {
		this.zoneType = zoneType;
	}

}
