package edu.mscd.thesis.model.bldgs;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
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

	public AbstractBuilding(Pos2D pos, TileType tType, ZoneType zType) {
		this.tileType = tType;
		this.zoneType = zType;
		this.pos = pos;
		this.occupants = new HashSet<Person>();
	}
	
	@Override
	public double update(double growthValue) {
		if (this.getTileType().getMaxDensity().getDensityLevel() >= getDensity().getDensityLevel()) {
			if (growthValue > Rules.GROWTH_THRESHOLD) {
				this.changeDensity(getDensity().getNextLevel());
				return growthValue-Rules.BASE_GROWTH_COST;
			}else{
				this.changeDensity(getDensity().getPrevLevel());
				return growthValue+Rules.BASE_GROWTH_COST*2;
			}
		}
		return growthValue;
	}
	
	@Override
	public int currentOccupancy(){
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

	public boolean addOccupant(Person p) {
		if (maxOccupants <= occupants.size()) {
			return false;
		}
		return occupants.add(p);
	}

	@Override
	public void render(GraphicsContext g) {
		if (!this.image.isError() && !this.image.isBackgroundLoading()) {
			g.drawImage(this.image, pos.getX(), pos.getY(), 1, 1);
		}

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
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Building) {
			Building b = (Building) o;
			if (b.getPos() != null) {
				return b.getPos().equals(this.getPos());
			} else {
				return b.getPos() == null && this.getPos() == null;
			}

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
		for(Person p: this.getOccupants()){
			sb.append("{");
			sb.append(p);
			sb.append("},");
		}
		sb.append("}");
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
