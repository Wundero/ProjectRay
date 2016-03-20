package me.Wundero.ProjectRay.utils;

/**
 * A {@code TimeUnit} represents time durations at a given unit of granularity
 * and provides utility methods to convert across units, and to perform timing
 * and delay operations in these units. A {@code TimeUnit} does not maintain
 * time information, but only helps organize and use time representations that
 * may be maintained separately across various contexts. A nanosecond is defined
 * as one thousandth of a microsecond, a microsecond as one thousandth of a
 * millisecond, a millisecond as one thousandth of a second, a minute as sixty
 * seconds, an hour as sixty minutes, and a day as twenty four hours.
 *
 * <p>
 * A {@code TimeUnit} is mainly used to inform time-based methods how a given
 * timing parameter should be interpreted. For example, the following code will
 * timeout in 50 milliseconds if the {@link java.util.concurrent.locks.Lock
 * lock} is not available:
 *
 * <pre>
 * {@code
 * Lock lock = ...;
 * if (lock.tryLock(50L, TimeUnit.MILLISECONDS)) ...}
 * </pre>
 *
 * while this code will timeout in 50 seconds:
 * 
 * <pre>
 * {@code
 * Lock lock = ...;
 * if (lock.tryLock(50L, TimeUnit.SECONDS)) ...}
 * </pre>
 *
 * Note however, that there is no guarantee that a particular timeout
 * implementation will be able to notice the passage of time at the same
 * granularity as the given {@code TimeUnit}.
 *
 * @since 1.5
 * @author Doug Lea
 */
// My version of the class uses doubles instead of longs to allow decimals.
// Useful for increased accuracy.
public enum PRTimeUnit {
	/**
	 * Time unit representing one thousandth of a microsecond
	 */
	NANOSECONDS {
		public double toNanos(double d) {
			return d;
		}

		public double toMicros(double d) {
			return d / (C1 / C0);
		}

		public double toMillis(double d) {
			return d / (C2 / C0);
		}

		public double toSeconds(double d) {
			return d / (C3 / C0);
		}

		public double toMinutes(double d) {
			return d / (C4 / C0);
		}

		public double toHours(double d) {
			return d / (C5 / C0);
		}

		public double toDays(double d) {
			return d / (C6 / C0);
		}

		public double convert(double d, PRTimeUnit u) {
			return u.toNanos(d);
		}

		int excessNanos(double d, double m) {
			return (int) (d - (m * C2));
		}
	},

	/**
	 * Time unit representing one thousandth of a millisecond
	 */
	MICROSECONDS {
		public double toNanos(double d) {
			return x(d, C1 / C0, MAX / (C1 / C0));
		}

		public double toMicros(double d) {
			return d;
		}

		public double toMillis(double d) {
			return d / (C2 / C1);
		}

		public double toSeconds(double d) {
			return d / (C3 / C1);
		}

		public double toMinutes(double d) {
			return d / (C4 / C1);
		}

		public double toHours(double d) {
			return d / (C5 / C1);
		}

		public double toDays(double d) {
			return d / (C6 / C1);
		}

		public double convert(double d, PRTimeUnit u) {
			return u.toMicros(d);
		}

		int excessNanos(double d, double m) {
			return (int) ((d * C1) - (m * C2));
		}
	},

	/**
	 * Time unit representing one thousandth of a second
	 */
	MILLISECONDS {
		public double toNanos(double d) {
			return x(d, C2 / C0, MAX / (C2 / C0));
		}

		public double toMicros(double d) {
			return x(d, C2 / C1, MAX / (C2 / C1));
		}

		public double toMillis(double d) {
			return d;
		}

		public double toSeconds(double d) {
			return d / (C3 / C2);
		}

		public double toMinutes(double d) {
			return d / (C4 / C2);
		}

		public double toHours(double d) {
			return d / (C5 / C2);
		}

		public double toDays(double d) {
			return d / (C6 / C2);
		}

		public double convert(double d, PRTimeUnit u) {
			return u.toMillis(d);
		}

		int excessNanos(double d, double m) {
			return 0;
		}
	},

	/**
	 * Time unit representing one second
	 */
	SECONDS {
		public double toNanos(double d) {
			return x(d, C3 / C0, MAX / (C3 / C0));
		}

		public double toMicros(double d) {
			return x(d, C3 / C1, MAX / (C3 / C1));
		}

		public double toMillis(double d) {
			return x(d, C3 / C2, MAX / (C3 / C2));
		}

		public double toSeconds(double d) {
			return d;
		}

		public double toMinutes(double d) {
			return d / (C4 / C3);
		}

		public double toHours(double d) {
			return d / (C5 / C3);
		}

		public double toDays(double d) {
			return d / (C6 / C3);
		}

		public double convert(double d, PRTimeUnit u) {
			return u.toSeconds(d);
		}

		int excessNanos(double d, double m) {
			return 0;
		}
	},

	/**
	 * Time unit representing sixty seconds
	 */
	MINUTES {
		public double toNanos(double d) {
			return x(d, C4 / C0, MAX / (C4 / C0));
		}

		public double toMicros(double d) {
			return x(d, C4 / C1, MAX / (C4 / C1));
		}

		public double toMillis(double d) {
			return x(d, C4 / C2, MAX / (C4 / C2));
		}

		public double toSeconds(double d) {
			return x(d, C4 / C3, MAX / (C4 / C3));
		}

		public double toMinutes(double d) {
			return d;
		}

		public double toHours(double d) {
			return d / (C5 / C4);
		}

		public double toDays(double d) {
			return d / (C6 / C4);
		}

		public double convert(double d, PRTimeUnit u) {
			return u.toMinutes(d);
		}

		int excessNanos(double d, double m) {
			return 0;
		}
	},

	/**
	 * Time unit representing sixty minutes
	 */
	HOURS {
		public double toNanos(double d) {
			return x(d, C5 / C0, MAX / (C5 / C0));
		}

		public double toMicros(double d) {
			return x(d, C5 / C1, MAX / (C5 / C1));
		}

		public double toMillis(double d) {
			return x(d, C5 / C2, MAX / (C5 / C2));
		}

		public double toSeconds(double d) {
			return x(d, C5 / C3, MAX / (C5 / C3));
		}

		public double toMinutes(double d) {
			return x(d, C5 / C4, MAX / (C5 / C4));
		}

		public double toHours(double d) {
			return d;
		}

		public double toDays(double d) {
			return d / (C6 / C5);
		}

		public double convert(double d, PRTimeUnit u) {
			return u.toHours(d);
		}

		int excessNanos(double d, double m) {
			return 0;
		}
	},

	/**
	 * Time unit representing twenty four hours
	 */
	DAYS {
		public double toNanos(double d) {
			return x(d, C6 / C0, MAX / (C6 / C0));
		}

		public double toMicros(double d) {
			return x(d, C6 / C1, MAX / (C6 / C1));
		}

		public double toMillis(double d) {
			return x(d, C6 / C2, MAX / (C6 / C2));
		}

		public double toSeconds(double d) {
			return x(d, C6 / C3, MAX / (C6 / C3));
		}

		public double toMinutes(double d) {
			return x(d, C6 / C4, MAX / (C6 / C4));
		}

		public double toHours(double d) {
			return x(d, C6 / C5, MAX / (C6 / C5));
		}

		public double toDays(double d) {
			return d;
		}

		public double convert(double d, PRTimeUnit u) {
			return u.toDays(d);
		}

		int excessNanos(double d, double m) {
			return 0;
		}
	};

	// Handy constants for conversion methods
	static final double C0 = 1L;
	static final double C1 = C0 * 1000L;
	static final double C2 = C1 * 1000L;
	static final double C3 = C2 * 1000L;
	static final double C4 = C3 * 60L;
	static final double C5 = C4 * 60L;
	static final double C6 = C5 * 24L;

	static final double MAX = Double.MAX_VALUE;

	/**
	 * Scale d by m, checking for overflow. This has a short name to make above
	 * code more readable.
	 */
	static double x(double d, double m, double over) {
		if (d > over)
			return Long.MAX_VALUE;
		if (d < -over)
			return Long.MIN_VALUE;
		return d * m;
	}

	// To maintain full signature compatibility with 1.5, and to improve the
	// clarity of the generated javadoc (see 6287639: Abstract methods in
	// enum classes should not be listed as abstract), method convert
	// etc. are not declared abstract but otherwise act as abstract methods.

	/**
	 * Converts the given time duration in the given unit to this unit.
	 * Conversions from finer to coarser granularities truncate, so lose
	 * precision. For example, converting {@code 999} milliseconds to seconds
	 * results in {@code 0}. Conversions from coarser to finer granularities
	 * with arguments that would numerically overflow saturate to
	 * {@code Long.MIN_VALUE} if negative or {@code Long.MAX_VALUE} if positive.
	 *
	 * <p>
	 * For example, to convert 10 minutes to milliseconds, use:
	 * {@code TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)}
	 *
	 * @param sourceDuration
	 *            the time duration in the given {@code sourceUnit}
	 * @param sourceUnit
	 *            the unit of the {@code sourceDuration} argument
	 * @return the converted duration in this unit, or {@code Long.MIN_VALUE} if
	 *         conversion would negatively overflow, or {@code Long.MAX_VALUE}
	 *         if it would positively overflow.
	 */
	public double convert(double sourceDuration, PRTimeUnit sourceUnit) {
		throw new AbstractMethodError();
	}

	/**
	 * Equivalent to {@link #convert(long, TimeUnit)
	 * NANOSECONDS.convert(duration, this)}.
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration, or {@code Long.MIN_VALUE} if conversion
	 *         would negatively overflow, or {@code Long.MAX_VALUE} if it would
	 *         positively overflow.
	 */
	public double toNanos(double duration) {
		throw new AbstractMethodError();
	}

	/**
	 * Equivalent to {@link #convert(long, TimeUnit)
	 * MICROSECONDS.convert(duration, this)}.
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration, or {@code Long.MIN_VALUE} if conversion
	 *         would negatively overflow, or {@code Long.MAX_VALUE} if it would
	 *         positively overflow.
	 */
	public double toMicros(double duration) {
		throw new AbstractMethodError();
	}

	/**
	 * Equivalent to {@link #convert(long, TimeUnit)
	 * MILLISECONDS.convert(duration, this)}.
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration, or {@code Long.MIN_VALUE} if conversion
	 *         would negatively overflow, or {@code Long.MAX_VALUE} if it would
	 *         positively overflow.
	 */
	public double toMillis(double duration) {
		throw new AbstractMethodError();
	}

	/**
	 * Equivalent to {@link #convert(long, TimeUnit) SECONDS.convert(duration,
	 * this)}.
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration, or {@code Long.MIN_VALUE} if conversion
	 *         would negatively overflow, or {@code Long.MAX_VALUE} if it would
	 *         positively overflow.
	 */
	public double toSeconds(double duration) {
		throw new AbstractMethodError();
	}

	/**
	 * Equivalent to {@link #convert(long, TimeUnit) MINUTES.convert(duration,
	 * this)}.
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration, or {@code Long.MIN_VALUE} if conversion
	 *         would negatively overflow, or {@code Long.MAX_VALUE} if it would
	 *         positively overflow.
	 * @since 1.6
	 */
	public double toMinutes(double duration) {
		throw new AbstractMethodError();
	}

	/**
	 * Equivalent to {@link #convert(long, TimeUnit) HOURS.convert(duration,
	 * this)}.
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration, or {@code Long.MIN_VALUE} if conversion
	 *         would negatively overflow, or {@code Long.MAX_VALUE} if it would
	 *         positively overflow.
	 * @since 1.6
	 */
	public double toHours(double duration) {
		throw new AbstractMethodError();
	}

	/**
	 * Equivalent to {@link #convert(long, TimeUnit) DAYS.convert(duration,
	 * this)}.
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration
	 * @since 1.6
	 */
	public double toDays(double duration) {
		throw new AbstractMethodError();
	}

	/**
	 * Utility to compute the excess-nanosecond argument to wait, sleep, join.
	 * 
	 * @param d
	 *            the duration
	 * @param m
	 *            the number of milliseconds
	 * @return the number of nanoseconds
	 */
	abstract int excessNanos(double d, double m);

	/**
	 * Performs a timed {@link Object#wait(double, int) Object.wait} using this
	 * time unit. This is a convenience method that converts timeout arguments
	 * into the form required by the {@code Object.wait} method.
	 *
	 * <p>
	 * For example, you could implement a blocking {@code poll} method (see
	 * {@link BlockingQueue#poll BlockingQueue.poll}) using:
	 *
	 * <pre>
	 * {@code
	 * public synchronized Object poll(double timeout, TimeUnit unit)
	 *     throws InterruptedException {
	 *   while (empty) {
	 *     unit.timedWait(this, timeout);
	 *     ...
	 *   }
	 * }}
	 * </pre>
	 *
	 * @param obj
	 *            the object to wait on
	 * @param timeout
	 *            the maximum time to wait. If less than or equal to zero, do
	 *            not wait at all.
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */
	public void timedWait(Object obj, long timeout) throws InterruptedException {
		if (timeout > 0) {
			long ms = (long) toMillis(timeout);
			int ns = excessNanos(timeout, ms);
			obj.wait(ms, ns);
		}
	}

	/**
	 * Performs a timed {@link Thread#join(long, int) Thread.join} using this
	 * time unit. This is a convenience method that converts time arguments into
	 * the form required by the {@code Thread.join} method.
	 *
	 * @param thread
	 *            the thread to wait for
	 * @param timeout
	 *            the maximum time to wait. If less than or equal to zero, do
	 *            not wait at all.
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */
	public void timedJoin(Thread thread, long timeout)
			throws InterruptedException {
		if (timeout > 0) {
			long ms = (long) toMillis(timeout);
			int ns = excessNanos(timeout, ms);
			thread.join(ms, ns);
		}
	}

	/**
	 * Performs a {@link Thread#sleep(long, int) Thread.sleep} using this time
	 * unit. This is a convenience method that converts time arguments into the
	 * form required by the {@code Thread.sleep} method.
	 *
	 * @param timeout
	 *            the minimum time to sleep. If less than or equal to zero, do
	 *            not sleep at all.
	 * @throws InterruptedException
	 *             if interrupted while sleeping
	 */
	public void sleep(long timeout) throws InterruptedException {
		if (timeout > 0) {
			long ms = (long) toMillis(timeout);
			int ns = excessNanos(timeout, ms);
			Thread.sleep(ms, ns);
		}
	}

}
