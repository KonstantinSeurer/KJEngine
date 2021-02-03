package de.kjEngine.math.geometry;

import java.io.Serializable;

import de.kjEngine.math.Vec3;

public class Triangle implements Serializable {
	private static final long serialVersionUID = 5829369958801935952L;
	
	public Vec3 p0, p1, p2;

	public Triangle() {
		this(Vec3.create(), Vec3.create(), Vec3.create());
	}
	
	public Triangle(Vec3 p0, Vec3 p1, Vec3 p2) {
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}
}
