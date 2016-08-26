package com.codingforcookies.worldbuilder.util;

public class Vector3 {
	private float x;
	private float y;
	private float z;

	public Vector3() { set(0, 0, 0); }
	public Vector3(float v) { set(v, v, v); }
	public Vector3(float x, float y, float z) { set(x, y, z); }
	
	public Vector3 set(float f) {
		return set(f, f, f);
	}
	
	public Vector3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public float x() {
		return x;
	}

	public Vector3 x(float x) {
		this.x = x;
		return this;
	}

	public float y() {
		return y;
	}

	public Vector3 y(float y) {
		this.y = y;
		return this;
	}

	public float z() {
		return z;
	}

	public Vector3 z(float z) {
		this.z = z;
		return this;
	}

	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	public Vector3 normalize() {
		float length = 1f / length();
		x *= length;
		y *= length;
		z *= length;
		return this;
	}

	public float dot(Vector3 vec) {
		return x * vec.x + y * vec.y + z * vec.z;
	}

	public Vector3 cross(Vector3 vec1, Vector3 vec2) {
		return set(vec1.y * vec2.z - vec2.y * vec1.z, vec1.z * vec2.x - vec2.z * vec1.x, vec1.x * vec2.y - vec2.x * vec1.y);
	}

	public Vector3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector3 add(Vector3 vec) {
		return add(vec.x, vec.y, vec.z);
	}

	public Vector3 sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Vector3 sub(Vector3 vec) {
		return sub(vec.x, vec.y, vec.z);
	}

	public Vector3 mult(float f) {
		return mult(f, f, f);
	}

	public Vector3 mult(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	public Vector3 mult(Vector3 vec) {
		return mult(vec.x, vec.y, vec.z);
	}

	public Vector3 divide(float f) {
		return divide(f, f, f);
	}

	public Vector3 divide(float x, float y, float z) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		return this;
	}

	public Vector3 divide(Vector3 vec) {
		return divide(vec.x, vec.y, vec.z);
	}

	public Vector3 mod(float f) {
		x %= f;
		y %= f;
		z %= f;

		return this;
	}

	public boolean equals(Vector3 v) {
		return x == v.x && y == v.y && z == v.z;
	}

	public int hashCode() {
		return (int)(x * (2 << 4) + y * (2 << 2) + z);
	}

	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public Vector3 normal(Vector3 p1, Vector3 p2, Vector3 p3) {
	    Vector3 calU = new Vector3(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
	    Vector3 calV = new Vector3(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

	    x = calU.y * calV.z - calU.z * calV.y;
	    y = calU.z * calV.x - calU.x * calV.z;
	    z = calU.x * calV.y - calU.y * calV.x;

	    return normalize();
	}
}
