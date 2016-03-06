package me.Wundero.ProjectRay.utils;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

/*
 The MIT License (MIT)

 Copyright (c) 2016 Wundero

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
/**
 * Class of Utils made by me / other utils
 * 
 * @author Wundero
 * 
 *         Contains utils from @Authors Slikey and iSach
 *
 */
public class MathUtils {

	static public final float nanoToSec = 1 / 1000000000f;

	// ---
	static public final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
	static public final float PI = 3.1415927f;
	static public final float PI2 = PI * 2;

	static public final float SQRT_3 = 1.73205f;

	static public final float E = 2.7182818f;

	static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
	static private final int SIN_MASK = ~(-1 << SIN_BITS);
	static private final int SIN_COUNT = SIN_MASK + 1;

	static private final float radFull = PI * 2;
	static private final float degFull = 360;
	static private final float radToIndex = SIN_COUNT / radFull;
	static private final float degToIndex = SIN_COUNT / degFull;

	/** multiply by this to convert from radians to degrees */
	static public final float radiansToDegrees = 180f / PI;
	static public final float radDeg = radiansToDegrees;
	/** multiply by this to convert from degrees to radians */
	static public final float degreesToRadians = PI / 180;
	static public final float degRad = degreesToRadians;

	static private class Sin {
		static final float[] table = new float[SIN_COUNT];
		static {
			for (int i = 0; i < SIN_COUNT; i++)
				table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
			for (int i = 0; i < 360; i += 90)
				table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i
						* degreesToRadians);
		}
	}

	static public final int a(int i) {
		return Math.abs(i);
	}

	/** Returns the sine in radians from a lookup table. */
	static public final float sin(float radians) {
		return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
	}

	/** Returns the cosine in radians from a lookup table. */
	static public final float cos(float radians) {
		return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
	}

	/** Returns the sine in radians from a lookup table. */
	static public final float sinDeg(float degrees) {
		return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
	}

	/** Returns the cosine in radians from a lookup table. */
	static public final float cosDeg(float degrees) {
		return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
	}

	// ---

	static private final int ATAN2_BITS = 7; // Adjust for accuracy.
	static private final int ATAN2_BITS2 = ATAN2_BITS << 1;
	static private final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
	static private final int ATAN2_COUNT = ATAN2_MASK + 1;
	static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
	static private final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

	static private class Atan2 {
		static final float[] table = new float[ATAN2_COUNT];
		static {
			for (int i = 0; i < ATAN2_DIM; i++) {
				for (int j = 0; j < ATAN2_DIM; j++) {
					float x0 = (float) i / ATAN2_DIM;
					float y0 = (float) j / ATAN2_DIM;
					table[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
				}
			}
		}
	}

	/** Returns atan2 in radians from a lookup table. */
	static public final float atan2(float y, float x) {
		float add, mul;
		if (x < 0) {
			if (y < 0) {
				y = -y;
				mul = 1;
			} else
				mul = -1;
			x = -x;
			add = -PI;
		} else {
			if (y < 0) {
				y = -y;
				mul = -1;
			} else
				mul = 1;
			add = 0;
		}
		float invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);

		if (invDiv == Float.POSITIVE_INFINITY)
			return ((float) Math.atan2(y, x) + add) * mul;

		int xi = (int) (x * invDiv);
		int yi = (int) (y * invDiv);
		return (Atan2.table[yi * ATAN2_DIM + xi] + add) * mul;
	}

	// ---

	/**
	 * Returns a random number between 0 (inclusive) and the specified value
	 * (inclusive).
	 */
	static public final int random(int range) {
		return random.nextInt(range + 1);
	}

	/** Returns a random number between start (inclusive) and end (inclusive). */
	static public final int random(int start, int end) {
		int temp = Math.max(start, end);
		int t2 = Math.min(start, end);
		start = t2;
		end = temp;
		return start + random.nextInt(end - start + 1);
	}

	/** Returns a random boolean value. */
	static public final boolean randomBoolean() {
		return random.nextBoolean();
	}

	/**
	 * Returns true if a random value between 0 and 1 is less than the specified
	 * value.
	 */
	static public final boolean randomBoolean(float chance) {
		return MathUtils.random() < chance;
	}

	/** Returns random number between 0.0 (inclusive) and 1.0 (exclusive). */
	static public final float random() {
		return random.nextFloat();
	}

	/**
	 * Returns a random number between 0 (inclusive) and the specified value
	 * (exclusive).
	 */
	static public final float random(float range) {
		return random.nextFloat() * range;
	}

	/** Returns a random number between start (inclusive) and end (exclusive). */
	static public final float random(float start, float end) {
		return start + random.nextFloat() * (end - start);
	}

	// ---

	/**
	 * Returns the next power of two. Returns the specified value if the value
	 * is already a power of two.
	 */
	static public final int nextPowerOfTwo(int value) {
		if (value == 0)
			return 1;
		value--;
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return value + 1;
	}

	static public final boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	// ---

	static public final int clamp(int value, int min, int max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public final short clamp(short value, short min, short max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public final float clamp(float value, float min, float max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	// ---

	static private final int BIG_ENOUGH_INT = 16 * 1024;
	static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
	static private final double CEIL = 0.9999999;
	// static private final double BIG_ENOUGH_CEIL = NumberUtils
	// .longBitsToDouble(NumberUtils.doubleToLongBits(BIG_ENOUGH_INT + 1) - 1);
	static private final double BIG_ENOUGH_CEIL = 16384.999999999996;
	static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

	/**
	 * Returns the largest integer less than or equal to the specified float.
	 * This method will only properly floor floats from -(2^14) to
	 * (Float.MAX_VALUE - 2^14).
	 */
	static public final int floor(float x) {
		return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	/**
	 * Returns the largest integer less than or equal to the specified float.
	 * This method will only properly floor floats that are positive. Note this
	 * method simply casts the float to int.
	 */
	static public final int floorPositive(float x) {
		return (int) x;
	}

	/**
	 * Returns the smallest integer greater than or equal to the specified
	 * float. This method will only properly ceil floats from -(2^14) to
	 * (Float.MAX_VALUE - 2^14).
	 */
	static public final int ceil(float x) {
		return (int) (x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
	}

	/**
	 * Returns the smallest integer greater than or equal to the specified
	 * float. This method will only properly ceil floats that are positive.
	 */
	static public final int ceilPositive(float x) {
		return (int) (x + CEIL);
	}

	/**
	 * Returns the closest integer to the specified float. This method will only
	 * properly round floats from -(2^14) to (Float.MAX_VALUE - 2^14).
	 */
	static public final int round(float x) {
		return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
	}

	/**
	 * Returns the closest integer to the specified float. This method will only
	 * properly round floats that are positive.
	 */
	static public final int roundPositive(float x) {
		return (int) (x + 0.5f);
	}

	/**
	 * Returns true if the value is zero (using the default tolerance as upper
	 * bound)
	 */
	static public final boolean isZero(float value) {
		return isZero(value, FLOAT_ROUNDING_ERROR);
	}

	/**
	 * Returns true if the value is zero.
	 * 
	 * @param tolerance
	 *            represent an upper bound below which the value is considered
	 *            zero.
	 */
	static public final boolean isZero(float value, float tolerance) {
		return Math.abs(value) <= tolerance;
	}

	/**
	 * Returns true if a is nearly equal to b. The function uses the default
	 * floating error tolerance.
	 * 
	 * @param a
	 *            the first value.
	 * @param b
	 *            the second value.
	 */
	static public final boolean isEqual(float a, float b) {
		return isEqual(a, b, FLOAT_ROUNDING_ERROR);
	}

	/**
	 * Returns true if a is nearly equal to b.
	 * 
	 * @param a
	 *            the first value.
	 * @param b
	 *            the second value.
	 * @param tolerance
	 *            represent an upper bound below which the two values are
	 *            considered equal.
	 */
	static public final boolean isEqual(float a, float b, float tolerance) {
		return Math.abs(a - b) <= tolerance;
	}

	public static Random random = new Random();

	public static final Vector rotateAroundAxisX(final Vector v, double angle) {
		double y, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		y = v.getY() * cos - v.getZ() * sin;
		z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}

	public static final Vector rotateAroundAxisY(final Vector v, double angle) {
		double x, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos + v.getZ() * sin;
		z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public static final Vector rotateAroundAxisZ(final Vector v, double angle) {
		double x, y, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos - v.getY() * sin;
		y = v.getX() * sin + v.getY() * cos;
		return v.setX(x).setY(y);
	}

	public static final Vector rotateVector(final Vector v, double angleX,
			double angleY, double angleZ) {
		Vector v1 = v;
		v1 = rotateAroundAxisX(v1, angleX);
		v1 = rotateAroundAxisY(v1, angleY);
		v1 = rotateAroundAxisZ(v1, angleZ);
		return v1;
	}

	public static final double angleToXAxis(final Vector vector) {
		return Math.atan2(vector.getX(), vector.getY());
	}

	public static final Vector getRandomVector() {
		double x = random.nextDouble() * 2.0D - 1.0D;
		double y = random.nextDouble() * 2.0D - 1.0D;
		double z = random.nextDouble() * 2.0D - 1.0D;

		return new Vector(x, y, z).normalize();
	}

	public static final Vector getRandomCircleVector() {
		double rnd = random.nextDouble() * 2.0D * 3.141592653589793D;
		double x = Math.cos(rnd);
		double z = Math.sin(rnd);

		return new Vector(x, 0.0D, z);
	}

	public static final double randomDouble(double min, double max) {
		return Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min)
				: (Math.random() * (max - min) + min);
	}

	public static final float randomRangeFloat(float min, float max) {
		return (float) (Math.random() < 0.5 ? ((1 - Math.random())
				* (max - min) + min) : (Math.random() * (max - min) + min));
	}

	/**
	 * Returns a random integer between the value min and the value max.
	 * 
	 * @param min
	 *            the minimum integer value.
	 * @param max
	 *            the maximum integer value.
	 * @return a random integer between two values.
	 */
	public static final int randomRangeInt(int min, int max) {
		return (int) (Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min)
				: (Math.random() * (max - min) + min));
	}

	public static final double offset(Entity a, Entity b) {
		return offset(a.getLocation().toVector(), b.getLocation().toVector());
	}

	public static final double offset(Location a, Location b) {
		return offset(a.toVector(), b.toVector());
	}

	public static final double offset(Vector a, Vector b) {
		return a.subtract(b).length();
	}

	public static final double round(final double value) {
		return (double) Math.round(value * 100) / 100;
	}

	private static final double getRightPosY(double x, double r) {
		return round(Math.sqrt(round(Math.pow(r, 2)) - round(Math.pow(x, 2))));
	}

	private static final double getRightNegY(double x, double r) {
		return -getRightPosY(x, r);
	}

	public static final ArrayList<Coord> genCircleCoords(final double x,
			final double y, final double r, int count) {
		ArrayList<Coord> out = new ArrayList<>();
		double x1 = x - r;
		double step = 1;
		if (count > 0) {
			step = (40 / (double) count);
		}
		int i = 0;
		for (; x1 <= x + r; x1 += step) {
			out.add(new Coord(x1, getRightPosY(x1 - x, r)));
			if (getRightPosY(x1 - x, r) != getRightNegY(x1 - x, r)) {
				out.add(new Coord(x1, getRightNegY(x1 - x, r)));
				i++;
			}
			i++;
		}
		System.out.println(i);
		return out;
	}

	public static final ArrayList<Location> generateHalo(Player p) {
		Location l = p.getLocation().clone().add(0, 2, 0);
		ArrayList<Location> out = Lists.newArrayList();
		for (Coord c : genCircleCoords(l.getX(), l.getZ(), 0.5, 20)) {
			Location l1 = l.clone();
			l1.setZ(c.y);
			l1.setX(c.x);
			out.add(l1);
		}
		return out;
	}

	public static final class Coord {
		public double x, y;

		public Coord(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "[" + round(x) + "," + round(y) + "]";
		}
	}

}
