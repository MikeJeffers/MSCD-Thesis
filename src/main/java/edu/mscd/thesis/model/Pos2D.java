package edu.mscd.thesis.model;


import java.util.Objects;

import javafx.geometry.Point2D;

public class Pos2D{
	private double x;
	private double y;

	public Pos2D(double _x, double _y) {
		this.x = _x;
		this.y = _y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public Pos2D copy() {
		return new Pos2D(x, y);
	}
	
	Point2D getPt2D(){
		return new Point2D(x, y);
	}
	
	public double distBetween(Pos2D o){
		if(o==null){
			return -1;
		}
		double d = Math.sqrt(Math.pow(this.x-o.x, 2)+Math.pow(this.y-o.y, 2));
		return d;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Pos2D) {
			Pos2D o = (Pos2D) other;
			return o.getX() == this.getX() && o.getY() == this.getY();
		}
		return false;
	}

	@Override
	public String toString() {
		return "Pos2D{x=" + x + ", y=" + y + "}";
	}

}
