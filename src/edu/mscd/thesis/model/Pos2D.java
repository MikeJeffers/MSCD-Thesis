package edu.mscd.thesis.model;

import java.util.Objects;

public class Pos2D {
	private int x;
	private int y;

	public Pos2D(int _x, int _y) {
		this.x = _x;
		this.y = _y;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	Pos2D copy() {
		return new Pos2D(x, y);
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
