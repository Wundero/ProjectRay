package me.Wundero.Ray.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ProxySource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableList;

import me.Wundero.Ray.ProjectRay;
import me.Wundero.Ray.Ray;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

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
 * This class contains various utility methods and fields for use throughout the
 * plugin.
 */
public class Utils {

	/**
	 * Emptry string constant.
	 */
	public static final String S = "";
	/**
	 * Pattern for matching any URL.
	 */
	public static final Pattern URL_PATTERN = Pattern
			.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,}(?:/\\S+)?)");
	/**
	 * Pattern for matching variables.
	 */
	public static final Pattern VAR_PATTERN = Pattern.compile("[{][^{}]+[}]");

	/**
	 * Load a config from a path.
	 */
	public static ConfigurationNode load(File config) {
		return load(config.toPath());
	}

	/**
	 * Random optional uuid.
	 */
	public static Optional<UUID> presentUUID() {
		return wrap(UUID.randomUUID());
	}

	/**
	 * Apply a function to all objects in a list.
	 */
	public static <T, C extends Collection<T>> Collection<T> applyToAll(Collection<T> original, Function<T, T> function,
			Supplier<C> factory) {
		return original.stream().map(function).collect(Collectors.toCollection(factory));
	}

	/**
	 * Remove non-null elements from a collection
	 */
	public static <T, C extends Collection<T>> Collection<T> nonNull(Collection<T> original, Supplier<C> factory) {
		return original.stream().filter(t -> t != null).collect(Collectors.toCollection(factory));
	}

	/**
	 * Add a value to a map containing a list of values mapped to a key.
	 */
	public static <K, T> Map<K, List<T>> addTo(Map<K, List<T>> map, K key, T value) {
		List<T> tl = map.get(key);
		tl.add(value);
		map.put(key, tl);
		return map;
	}

	/**
	 * Turn an array into a list of objects.
	 */
	public static List<Object> deconstructArray(Object[] arr) {
		List<Object> out = al();
		for (Object o : arr) {
			if (o == null) {
				out.add(o);
				continue;
			}
			if (o.getClass().isArray()) {
				out.addAll(deconstructArray((Object[]) o));
			} else {
				out.add(o);
			}
		}
		return out;
	}

	/**
	 * Combine multiple lists into one. Boolean to filter unique items.
	 */
	@SafeVarargs
	public static <T> List<T> intersect(List<T>... lists) {
		if (lists == null || lists.length == 0) {
			return al();
		}
		List<T> l = lists[0];
		List<T> mod = l;
		boolean f = true;
		for (List<T> l2 : lists) {
			if (f) {
				f = false;
				// skip first list to increase efficiency
			} else {
				for (T t : l) {
					if (!l2.contains(t)) {
						mod.remove(mod.indexOf(t));
					}
				}
				l = mod;
			}
		}
		return l;
	}

	/**
	 * Combine multiple arrays into one. Boolean to filter unique items.
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> T[] intersect(T[]... arrs) {
		if (arrs == null || arrs.length == 0) {
			return (T[]) new Object[0];
		}
		List<T> l = al(arrs[0]);
		List<T> mod = l;
		boolean f = true;
		for (T[] a2 : arrs) {
			if (f) {
				f = false;
				// skip first list to increase efficiency
			} else {
				List<T> l2 = al(a2);
				for (T t : l) {
					if (!l2.contains(t)) {
						mod.remove(mod.indexOf(t));
					}
				}
				l = mod;
			}
		}
		if (l.isEmpty()) {
			return (T[]) new Object[0];
		}
		T[] out = (T[]) new Object[l.size()];
		for (int i = 0; i < l.size(); i++) {
			out[i] = l.get(i);
		}
		return out;
	}

	/**
	 * Combine multiple lists into one. Boolean to filter unique items.
	 */
	@SafeVarargs
	public static <T> List<T> combine(boolean distinct, List<T>... lists) {
		List<T> out = al();
		for (List<T> l : lists) {
			for (T t : l) {
				if (!out.contains(t) || !distinct) {
					out.add(t);
				}
			}
		}
		return out;
	}

	/**
	 * Combine multiple arrays into one. Boolean to filter unique items.
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> T[] combine(boolean distinct, T[]... arrs) {
		List<T> out = al();
		for (T[] a : arrs) {
			for (T t : a) {
				if (!out.contains(t) || !distinct) {
					out.add(t);
				}
			}
		}
		return (T[]) out.toArray(new Object[out.size()]);
	}

	/**
	 * Check to see if an array contains an object.
	 */
	public static <T> boolean contains(T[] arr, T obj) {
		List<T> list = al(arr, true);
		return list.contains(obj);
	}

	/**
	 * Check to see if num is within bounds b1 and b2, with defined exclusivity.
	 */
	public boolean in(int num, int b1, int b2, boolean el, boolean eg) {
		int bx = Math.min(b1, b2);
		int by = Math.max(b1, b2);
		if (el) {
			if (eg) {
				return num >= bx && num <= by;
			} else {
				return num >= bx && num < by;
			}
		} else {
			if (eg) {
				return num > bx && num <= by;
			} else {
				return num > bx && num < by;
			}
		}
	}

	/**
	 * Cast an object to another type. If the cast does not work or is null,
	 * returns empty.
	 */
	public static <T, R> Optional<R> cast(T object, Class<R> to) {
		try {
			return wrap(to.cast(object));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Cast an object to another type. If the cast fails or is null, returns
	 * null.
	 */
	public static <T, R> R castNull(T object, Class<R> to) {
		return cast(object, to).orElse(null);
	}

	/**
	 * Wrap an object into an Optional object using ofNullable. Checks against
	 * the object will be made by the functions. The functions return true if
	 * the checks are passed. If any function returns false, the returned
	 * optional is empty.
	 */
	@SafeVarargs
	public static <T> Optional<T> wrap2(T t, Function<Optional<T>, Boolean>... checks) {
		Optional<T> o = Optional.ofNullable(t);
		for (Function<Optional<T>, Boolean> b : checks) {
			if (!b.apply(o)) {
				return Optional.empty();
			}
		}
		return o;
	}

	/**
	 * Wrap an object into an Optional using ofNullable. If any of the booleans
	 * in checks are false, empty is returned.
	 */
	public static <T> Optional<T> wrap(T t, boolean... checks) {
		for (boolean b : checks) {
			if (!b) {
				return Optional.empty();
			}
		}
		return Optional.ofNullable(t);
	}

	/**
	 * Schedule a task
	 */
	public static Task schedule(Task.Builder b, boolean async) {
		if (async) {
			b.async();
		}
		return b.submit(Ray.get().getPlugin());
	}

	/**
	 * Get the permissionservice set option for a player.
	 */
	public static String getOption(Player p, String opt) {
		return p.getOption(opt).orElse(null);
	}

	/**
	 * Get the permissionservice set option for a player as a text object.
	 */
	public static Text getOption(Player p, String opt, boolean text) {
		return TextSerializers.FORMATTING_CODE.deserialize(p.getOption(opt).orElse(""));
	}

	/**
	 * Remove money from a balance of a player.
	 */
	public static boolean charge(Player p, double c) {
		Optional<EconomyService> eco = Sponge.getServiceManager().provide(EconomyService.class);
		if (!eco.isPresent()) {
			return false;
		}
		EconomyService e = eco.get();
		Optional<UniqueAccount> acc = e.getOrCreateAccount(p.getUniqueId());
		if (!acc.isPresent()) {
			return false;
		}
		UniqueAccount a = acc.get();
		Currency cur = e.getDefaultCurrency();
		if (c > 0) {
			TransactionResult r = a.withdraw(cur, new BigDecimal(c),
					Cause.source(Ray.get().getPlugin()).named("Purchase", c).build());
			switch (r.getResult()) {
			case SUCCESS:
				return true;
			default:
				return false;
			}
		} else if (c == 0) {
			return false;
		} else {
			TransactionResult r = a.deposit(cur, new BigDecimal(c),
					Cause.source(Ray.get().getPlugin()).named("Purchase", c).build());
			switch (r.getResult()) {
			case SUCCESS:
				return true;
			default:
				return false;
			}
		}
	}

	/**
	 * Create a synchronous array deque containing all objects in the
	 * collection.
	 */
	public static <T> Deque<T> sd(Collection<T> objs, boolean collection) {
		Deque<T> d = ad();
		for (T t : objs) {
			d.add(t);
		}
		return d;
	}

	/**
	 * Create a synchronous array deque containing all objects in the array.
	 */
	public static <T> Deque<T> sd(T[] objs, boolean arr) {
		Deque<T> d = ad();
		for (T t : objs) {
			d.add(t);
		}
		return d;
	}

	/**
	 * Create a synchronous array deque containing all objects in the
	 * parameters.
	 */
	@SafeVarargs
	public static <T> Deque<T> sd(T... objs) {
		Deque<T> d = ad(Tristate.FALSE);
		for (T t : objs) {
			d.add(t);
		}
		return d;
	}

	/**
	 * Create an empty, synchronous array deque.
	 */
	public static <T> Deque<T> sd() {
		return ad(Tristate.FALSE);
	}

	/**
	 * Create an empty, synchronous array deque. Equality strictness based on
	 * tristate.
	 */
	public static <T> Deque<T> sd(Tristate strict) {
		return (Deque<T>) Collections.synchronizedCollection(new ArRayDeque<T>(strict, true));
	}

	/**
	 * Create an array deque containing all objects in the collection.
	 */
	public static <T> Deque<T> ad(Collection<T> objs, boolean collection) {
		Deque<T> d = ad();
		for (T t : objs) {
			d.add(t);
		}
		return d;
	}

	/**
	 * Create an array deque containing all objects in the array.
	 */
	public static <T> Deque<T> ad(T[] objs, boolean arr) {
		Deque<T> d = ad();
		for (T t : objs) {
			d.add(t);
		}
		return d;
	}

	/**
	 * Create an array deque containing all objects in the parameters.
	 */
	@SafeVarargs
	public static <T> Deque<T> ad(T... objs) {
		Deque<T> d = ad(Tristate.FALSE);
		for (T t : objs) {
			d.add(t);
		}
		return d;
	}

	/**
	 * Create an empty array deque.
	 */
	public static <T> Deque<T> ad() {
		return ad(Tristate.FALSE);
	}

	/**
	 * Create an empty array deque. Equality strictness based on tristate.
	 */
	public static <T> Deque<T> ad(Tristate strict) {
		return new ArRayDeque<T>(strict, false);
	}

	/**
	 * Create an array list containing all objects in the collection.
	 */
	public static <T> List<T> al(Collection<T> objs, boolean collection) {
		List<T> l = al();
		l.addAll(objs);
		return l;
	}

	/**
	 * Create an array list containing all objects in the array.
	 */
	public static <T> List<T> al(T[] objs, boolean arr) {
		List<T> l = al();
		for (T t : objs) {
			l.add(t);
		}
		return l;
	}

	/**
	 * Create an array list containing all objects in the parameters.
	 */
	@SafeVarargs
	public static <T> List<T> al(T... objs) {
		List<T> l = al(Tristate.FALSE);
		for (T t : objs) {
			l.add(t);
		}
		return l;
	}

	/**
	 * Create an empty array list.
	 */
	public static <T> List<T> al() {
		return al(Tristate.FALSE);
	}

	/**
	 * Create an empty array list with indexOf strictness based off of the
	 * tristate. TRUE means both o and get(i) equals methods must be true, FALSE
	 * means one of them must be true, and NONE means that the default list impl
	 * will be used.
	 */
	public static <T> List<T> al(final Tristate strict) {
		// indexOf method coded such that the equals call supports either object
		// equaling the other
		// this allows support for ChannelMember; it's less strict but useful
		// to me.
		return (new ArrayList<T>() {

			private static final long serialVersionUID = -9083645937669914699L;

			@Override
			public int indexOf(Object o) {
				if (o == null) {
					for (int i = 0; i < size(); i++) {
						if (get(i) == null) {
							return i;
						}
					}
				} else {
					for (int i = 0; i < size(); i++) {
						if (strict == Tristate.TRUE) {
							if (get(i).equals(o) && o.equals(get(i))) {
								return i;
							}
						} else if (strict == Tristate.FALSE) {
							if (get(i).equals(o) || o.equals(get(i))) {
								return i;
							}
						} else {
							if (get(i).equals(o)) {
								return i;
							}
						}
					}
				}
				return -1;
			}

		});
	}

	/**
	 * Create a synchronized list containing all objects in the collection.
	 */

	public static <T> List<T> sl(Collection<T> objs, boolean collection) {
		List<T> l = sl();
		l.addAll(objs);
		return l;
	}

	/**
	 * Create a synchronized list containing all objects in the array.
	 */

	public static <T> List<T> sl(T[] objs, boolean array) {
		List<T> l = sl();
		for (T t : objs) {
			l.add(t);
		}
		return l;
	}

	/**
	 * Create a synchronized list containing all objects in the parameters.
	 */
	@SafeVarargs
	public static <T> List<T> sl(T... objs) {
		List<T> l = sl(Tristate.FALSE);
		for (T t : objs) {
			l.add(t);
		}
		return l;
	}

	/**
	 * Create an OptionalMap and fill it with the pre-existing map.
	 */
	public static <K, V> OptionalMap<K, V> om(Map<K, V> pre) {
		OptionalMap<K, V> o = om();
		o.putAll(pre);
		return o;
	}

	/**
	 * Create an empty OptionalMap.
	 */
	public static <K, V> OptionalMap<K, V> om() {
		return new OptionalMap<K, V>();
	}

	/**
	 * Create an empty synchronized list.
	 */
	public static <T> List<T> sl() {
		return sl(Tristate.FALSE);
	}

	/**
	 * Create an empty synchronized list with indexOf strictness based off of
	 * the tristate. TRUE means both o and get(i) equals methods must be true,
	 * FALSE means one of them must be true, and NONE means that the default
	 * list impl will be used. The iterator of this list is immutable as well,
	 * meaning that no modifications can be made through the iterator. All
	 * iterable (and consequentially non-synchronizable within Java's framework)
	 * methods are wrapped with an immutable copy of this array. This list is
	 * completely thread safe and itrable without synchrnous blocks.
	 */

	public static <T> List<T> sl(final Tristate strict) {
		// returns a new arraylist that copies itself for iteration (makes
		// unsynchronized
		// iter blocks safe)
		// indexOf method coded such that the equals call
		// supports either object equaling the other this allows support
		// for ChannelMember; it's less strict but usefull to me.
		return Collections.synchronizedList(new ArrayList<T>() {

			private static final long serialVersionUID = -9083645937669914699L;

			@Override
			public int indexOf(Object o) {
				if (o == null) {
					for (int i = 0; i < size(); i++) {
						if (get(i) == null) {
							return i;
						}
					}
				} else {
					for (int i = 0; i < size(); i++) {
						if (strict == Tristate.TRUE) {
							if (get(i).equals(o) && o.equals(get(i))) {
								return i;
							}
						} else if (strict == Tristate.FALSE) {
							if (get(i).equals(o) || o.equals(get(i))) {
								return i;
							}
						} else {
							if (get(i).equals(o)) {
								return i;
							}
						}
					}
				}
				return -1;
			}

			public Stream<T> stream() {
				return imlist().stream();
			}

			@Override
			public Stream<T> parallelStream() {
				return imlist().parallelStream();
			}

			@SuppressWarnings("unchecked")
			private ImmutableList<T> imlist() {
				return (ImmutableList<T>) ImmutableList.builder().add(this.toArray()).build();
			}

			@Override
			public Spliterator<T> spliterator() {
				return imlist().spliterator();
			}

			@Override
			public ListIterator<T> listIterator(int index) {
				return imlist().listIterator(index);
			}

			@Override
			public ListIterator<T> listIterator() {
				return imlist().listIterator();
			}

			@Override
			public Iterator<T> iterator() {
				return imlist().iterator();
			}

		});
	}

	/**
	 * Return a hash map containing the pre-existing map of values.
	 */
	public static <K, V> Map<K, V> hm(Map<? extends K, ? extends V> m) {
		Map<K, V> m1 = hm();
		m1.putAll(m);
		return m1;
	}

	/**
	 * Create an empty HashMap.
	 */
	public static <K, V> Map<K, V> hm() {
		return new HashMap<K, V>();
	}

	/**
	 * Return a synchronous map containing the pre-existing map of values.
	 */
	public static <K, V> Map<K, V> sm(Map<? extends K, ? extends V> m) {
		Map<K, V> m1 = sm();
		m1.putAll(m);
		return m1;
	}

	/**
	 * Create an empty ConcurrentHashMap.
	 */
	public static <K, V> Map<K, V> sm() {
		return new ConcurrentHashMap<K, V>();
	}

	/**
	 * Check to see if the class is an instanceof the main (as a string). This
	 * method is recursive, and will recurse until it reaches Object.java.
	 */
	public static boolean classinstanceof(String main, Class<?> sub) {
		// recursive -- call with caution
		boolean b = sub.getName().equalsIgnoreCase(main);
		if (!b && sub.equals(Object.class)) {
			return false;
		}
		return b ? b : classinstanceof(main, sub.getSuperclass());
	}

	private static double calcdelta(double... coord) {
		double cx = 0;
		for (int i = 0; i < coord.length; i++) {
			cx += Math.pow(coord[i], 2);
		}
		return Math.sqrt(cx);
	}

	/**
	 * Get the pythagorean difference between two sets of coordinates. This
	 * gives you a scalar value for the distance between coordinates such that,
	 * in any dimension, it follows Pythagorean theorum. Both coordinate arrays
	 * must have the same number of dimensions.
	 */
	public static double pythrange(double[] coord1, double[] coord2) {
		if (coord1.length < 2 || coord1.length != coord2.length) {
			return -1.0d;
		}
		double[] dists = new double[coord1.length];
		for (int i = 0; i < coord1.length; i++) {
			dists[i] = coord1[i] - coord2[i];
		}
		return Math.abs(calcdelta(dists));
	}

	/**
	 * Check to see if two coordinate values have a difference within a range.
	 */
	public static boolean inrange(double range, double[] coord1, double[] coord2) {
		if (range == Double.MAX_VALUE || range < 0) {
			return true;
		}
		if (range == 0) {
			return false;
		}
		return pythrange(coord1, coord2) <= range;
	}

	/**
	 * Pythagorean difference using a 3d location.
	 */
	public static double difference(Location<World> loc1, Location<World> loc2) {
		Validate.isTrue(loc1.getExtent().equals(loc2.getExtent()), "different worlds");
		double x1 = loc1.getX(), x2 = loc2.getX(), y1 = loc1.getY(), y2 = loc2.getY(), z1 = loc1.getZ(),
				z2 = loc2.getZ();
		double[] c1 = { x1, y1, z1 };
		double[] c2 = { x2, y2, z2 };
		return pythrange(c1, c2);
	}

	/**
	 * Pythagorean range checker for 3d locations.
	 */
	public static boolean inRange(Location<World> loc1, Location<World> loc2, double range) {
		if (range == Double.MAX_VALUE) {
			return true;
		}
		if (range < 0) {
			return true;
		}
		if (range == 0 || !loc1.getExtent().equals(loc2.getExtent())) {
			return false;
		}
		return difference(loc1, loc2) <= range;
	}

	/**
	 * Get the line number of the class calling this method and the class
	 * itself. Useful for STDOUT calls.
	 */
	public static String getLineNumber(boolean includeClass) {
		StackTraceElement[] arr = Thread.currentThread().getStackTrace();
		if (arr.length < 3) {
			return "ERROR";
		}
		StackTraceElement elmt = arr[2];
		if (includeClass) {
			return elmt.getClassName() + "." + elmt.getMethodName() + "(" + elmt.getFileName() + ":"
					+ elmt.getLineNumber() + ")";
		} else {
			return elmt.getLineNumber() + "";
		}
	}

	/**
	 * Get the line number of the stack object specified by the index. Useful
	 * for STDOUT calls.
	 */
	public static String getLineNumber(boolean includeClass, int index) {
		StackTraceElement[] arr = Thread.currentThread().getStackTrace();
		if (arr.length < index + 1) {
			return "ERROR";
		}
		StackTraceElement elmt = arr[index];
		if (includeClass) {
			return elmt.getClassName() + "." + elmt.getMethodName() + "(" + elmt.getFileName() + ":"
					+ elmt.getLineNumber() + ")";
		} else {
			return elmt.getLineNumber() + "";
		}
	}

	/**
	 * Get the true source of a CommandSource; scales through ProxySource cmd
	 * srcs to get the original. Non-recursive.
	 */
	public static CommandSource getTrueSource(CommandSource src) {
		CommandSource out = src;
		while (out instanceof ProxySource) {
			out = ((ProxySource) out).getOriginalSource();
		}
		return out;
	}

	/**
	 * Load a configuration node from a path. Automatically registered for
	 * saving on termination of the server.
	 */
	public static ConfigurationNode load(Path config) {
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(config)
				.build();
		try {
			ConfigurationNode node = loader.load(ProjectRay.updateSerializers(loader.getDefaultOptions()));
			Ray.get().registerLoader(loader, node);
			return node;
		} catch (Exception e) {
			Utils.printError(e);
			return null;
		}
	}

	/**
	 * Save the config to a file.
	 */
	public static void save(File file, ConfigurationNode root) {
		save(file.toPath(), root);
	}

	/**
	 * Save the config to a path.
	 */
	public static void save(Path file, ConfigurationNode root) {
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(file)
				.build();
		try {
			loader.save(root);
		} catch (IOException e) {
			Utils.printError(e);
		}
	}

	/**
	 * Check to see if the config value contains subsections specified in the
	 * varargs parameter.
	 */
	public static boolean hasSections(ConfigurationNode config, String... toValidate) {
		for (String s : toValidate) {
			if (!config.getChildrenMap().containsKey(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Randomly arrange a List.
	 */
	public static <T> List<T> scramble(final List<T> list) {
		List<T> out = al();
		List<T> in = al(list, true);
		Random r = new Random();
		while (!in.isEmpty()) {
			out.add(in.remove(r.nextInt(in.size())));
		}
		return out;
	}

	/**
	 * Checks to see if arr contains any of the objects from comp.
	 */
	public static <T> boolean containsOf(List<T> arr, List<T> comp) {
		for (T o : comp) {
			if (arr.contains(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compile patterns with a list of flags. Same as Pattern.compile(p, flag1 |
	 * flag2 | ...).
	 */
	public static Pattern compile(String pattern, int... flags) {
		int flag = Integer.MIN_VALUE;
		for (int i : flags) {
			if (flag == Integer.MIN_VALUE) {
				flag = i;
				continue;
			} else {
				flag |= i;
			}
		}
		if (flag == Integer.MIN_VALUE) {
			return Pattern.compile(pattern);
		}
		return Pattern.compile(pattern, flag);
	}

	/**
	 * Check to see if a string is matched by the URL pattern.
	 */
	public static boolean isURL(String input) {
		String s = input;
		while (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		return URL_PATTERN.matcher(TextUtils.strip(TextSerializers.FORMATTING_CODE.serialize(TextUtils.trans(s))))
				.matches();
	}

	/**
	 * Checks to see if the input contains a url.
	 */
	public static boolean containsURL(String input) {
		return URL_PATTERN.matcher(TextUtils.strip(TextSerializers.FORMATTING_CODE.serialize(TextUtils.trans(input))))
				.find();
	}

	/**
	 * Convert a double to a different time unit.
	 */
	public static double convert(double val, PRTimeUnit from, PRTimeUnit to) {
		return to.convert(val, from);
	}

	/**
	 * Join a group of strings into one with a separator.
	 */
	public static String join(Iterable<String> k) {
		return join(", ", k);
	}

	/**
	 * Join a group of strings into one with a separator.
	 */
	public static String join(String... k) {
		return join(", ", k);
	}

	/**
	 * Join a group of strings into one with a separator.
	 */
	public static String join(String sep, String... k) {
		return join(sep, al(k, true));
	}

	/**
	 * Join a group of strings into one with a separator.
	 */
	public static String join(String sep, Iterable<String> k) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (String s : k) {
			if (first) {
				first = false;
			} else {
				builder.append(sep);
			}
			builder.append(s);
		}
		return builder.toString();
	}

	/**
	 * Force a file to exist. If it does and doesn't match what it needs to be,
	 * it is recreated.
	 */
	public static void force(File f, boolean file) throws IOException {
		if (f.exists() && f.isFile() != file) {
			f.delete();
		}
		if (!f.exists()) {
			if (file) {
				f.createNewFile();
			} else {
				f.mkdirs();
			}
		}
	}

	/**
	 * Turn a list of objects into a list of strings. Optionally provide a
	 * method to call which takes no parameters and returns a string.
	 */
	public static <T> List<String> toStringList(List<T> o, Optional<Method> toCall) {
		List<String> out = al();
		if (!toCall.isPresent() || toCall.get().getReturnType() != String.class
				|| toCall.get().getParameters().length > 0) {
			for (Object o1 : o) {
				out.add(o1.toString());
			}
			return out;
		}
		Method m = toCall.get();
		for (Object o1 : o) {
			List<Method> objMethods = al(o1.getClass().getMethods(), true);
			if (!objMethods.contains(m)) {
				out.add(o1.toString());
			} else {
				try {
					out.add((String) m.invoke(o1));
				} catch (Exception e) {
					out.add(o1.toString());
				}
			}
		}
		return out;
	}

	/**
	 * Print an error to the sponge console.
	 */
	public static void printError(Exception e) {
		e.printStackTrace(new PrintStream(new OutputStream() {

			private String toprint = "";

			@Override
			public void write(int b) throws IOException {
				toprint += Character.valueOf((char) b);
			}

			@Override
			public void flush() {
				toprint = toprint.trim().replaceAll("\n", "");
				if (toprint.isEmpty()) {
					return;
				}
				Ray.get().getLogger().error(toprint);
				toprint = "";
			}
		}, true));
	}

	/**
	 * Return a list that takes all of the parts in J before index i.
	 */
	public static <T> List<T> sub(int i, Iterable<T> j) {
		List<T> o = al();
		int k = 0;
		for (T s : j) {
			k++;
			if (k > i) {
				break;
			}
			o.add(s);
		}
		return o;
	}

	/**
	 * Return a list that takes all of the parts in J before index i.
	 */
	@SafeVarargs
	public static <T> List<T> sub(int i, T... j) {
		List<T> o = al();
		int k = 0;
		for (T s : j) {
			k++;
			if (k > i) {
				break;
			}
			o.add(s);
		}
		return o;
	}

	/**
	 * Return a list that takes all of the parts in J after and including index
	 * i.
	 */
	public static <T> List<T> post(int i, Iterable<T> j) {
		List<T> o = al();
		int k = 0;
		for (T s : j) {
			k++;
			if (k <= i) {
				continue;
			}
			o.add(s);
		}
		return o;
	}

	/**
	 * Return a list that takes all of the parts in J after and including index
	 * i.
	 */
	@SafeVarargs
	public static <T> List<T> post(int i, T... j) {
		List<T> o = al();
		int k = 0;
		for (T s : j) {
			k++;
			if (k <= i) {
				continue;
			}
			o.add(s);
		}
		return o;
	}

	/**
	 * Remove duplicate items from a list.
	 */
	public static <T> List<T> removeDuplicates(List<T> list) {
		return al(list.stream().distinct().collect(Collectors.toList()), true);
	}

	/**
	 * Pop the first object from a deque, if it exists. If it does not exist,
	 * the default value is returned.
	 */
	public static <T> T pop(Deque<T> deque, T def) {
		T o = def;
		return deque.isEmpty() ? def : (o = deque.pop()) == null ? def : o;
	}

	/**
	 * Call an event. @return whether the event was successful.
	 */
	public static boolean call(Event e) {
		return !Sponge.getEventManager().post(e);
	}

	/**
	 * Return the pattern that matches against profanities.
	 */
	public static Pattern profanityPattern() {
		return compile("(" + "(f[ua4][c]?[k]([e3]r|[a4])?)|(b[i!1]?tch([e3]s|y)?)|(s(lut|h[i!1]t[3e]?))"
				+ "|(n[i1!]g[g]?([a4]|[3e]r))|([4a][sz]{2})|(cunt)|(d[i!1][c]?k)|([kd][yi1!]ke)"
				+ "|(f[a43e]g([g]?[0oi1!]t)?)|(cum|j[i1!]zz)|(p[e3]n[i1!u]s)|(qu[3e]{2}r)"
				+ "|(pu[zs]{2}y)|(tw[4a]t)|(v[a4]g[i1!]n[a4])|(wh[o0]r[e3])"
				+ "|([ck][o0][ck]?[k])|([d][o][ou][c][h][e]?)" + ")", Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Check to see if a string contains profanities.
	 */
	public static boolean containsProfanities(String s) {
		return profanityPattern().matcher(s).find();
	}

	/**
	 * Capitalize a string.
	 */
	public static String capitalize(String s) {
		int i = 0;
		for (Character c : s.toCharArray()) {
			// 97 to 122, subtract 32
			if (c.charValue() > 96 && c.charValue() < 123) {
				if (i == 0) {
					s = ((char) (c.charValue() - 32)) + s.substring(1);
					return s;
				} else {
					s = s.substring(0, i) + ((char) (c.charValue() - 32)) + s.substring(i + 1);
					return s;
				}
			}
			i++;
		}
		return s;
	}

	/**
	 * Create a list that alternates in picking values from one and two, in that
	 * order. i.e. one two one two one one one. [one, one, one, one, one] and
	 * [two, two].
	 */
	public static <T> List<T> alternate(T[] one, T[] two) {
		int dif = one.length - two.length;
		List<T> out = al();
		if (dif > 0) {
			int i = 0;
			for (; i < two.length; i++) {
				out.add(one[i]);
				out.add(two[i]);
			}
			for (; i < one.length; i++) {
				out.add(one[i]);
			}
		} else if (dif == 0) {
			int i = 0;
			for (; i < two.length; i++) {
				out.add(one[i]);
				out.add(two[i]);
			}
		} else {
			int i = 0;
			for (; i < one.length; i++) {
				out.add(one[i]);
				out.add(two[i]);
			}
			for (; i < two.length; i++) {
				out.add(two[i]);
			}
		}
		return out;
	}

	/**
	 * Create a list that alternates in picking values from one and two, in that
	 * order. i.e. one two one two one one one. [one, one, one, one, one] and
	 * [two, two].
	 */
	public static <T> List<T> alternate(List<T> one, List<T> two) {
		int dif = one.size() - two.size();
		List<T> out = al();
		if (dif > 0) {
			int i = 0;
			for (; i < two.size(); i++) {
				out.add(one.get(i));
				out.add(two.get(i));
			}
			for (; i < one.size(); i++) {
				out.add(one.get(i));
			}
		} else if (dif == 0) {
			int i = 0;
			for (; i < two.size(); i++) {
				out.add(one.get(i));
				out.add(two.get(i));
			}
		} else {
			int i = 0;
			for (; i < one.size(); i++) {
				out.add(one.get(i));
				out.add(two.get(i));
			}
			for (; i < two.size(); i++) {
				out.add(two.get(i));
			}
		}
		return out;
	}

	/**
	 * Get the first object in a list, if it exists.
	 */
	public static <T> Optional<T> getFirst(List<T> list) {
		if (list == null || list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(list.get(0));
	}

	/**
	 * Get the last object in a list, if it exists.
	 */
	public static <T> Optional<T> getLast(List<T> list) {
		if (list == null || list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(list.get(list.size() - 1));
	}

	/**
	 * Convert an ASCII english character into a UTF-8 Latin character.
	 */
	public static Character makeUnicode(Character c) {
		int v = c.charValue();
		if (v < 33 || v > 126) {
			return c;
		}
		return Character.valueOf((char) (v + 65248));// magic number lol
	}

	/**
	 * Convert ASCII english characters into UTF-8 Latin characters.
	 */
	public static String makeUnicode(String n) {
		String s = "";
		boolean skipNext = true;
		for (Character c : n.toCharArray()) {
			if (skipNext) {
				s += c;
				skipNext = true;
				continue;
			}
			s += makeUnicode(c);
			if (c == '\u00a7') {
				skipNext = true;
			}
		}
		return s;
	}

	/**
	 * Creates a url from a string. If the string does not have a proper prefix,
	 * it is inserted. If creation still fails, empty is returned.
	 */
	public static Optional<URL> toUrlSafe(final String a) {
		if (!URL_PATTERN.matcher(a).matches()) {
			return Optional.empty();
		}
		String s = a;
		if (!(a.startsWith("www") || a.startsWith("http"))) {
			s = "http://" + a;
		}
		try {
			return Optional.of(new URI(s).toURL());
		} catch (MalformedURLException | URISyntaxException e) {
			return Optional.empty();
		}
	}

	/**
	 * Populate a list with an object, modified for each index used, over a
	 * certain number of times.
	 */
	public static <T> List<T> fill(T original, BiFunction<T, Integer, T> mod, int times, boolean includeOriginal) {
		List<T> out = al();
		if (includeOriginal) {
			out.add(original);
		}
		int i = 0;
		while (i < times) {
			try {
				T m = mod.apply(original, i);
				out.add(m);
			} catch (Exception e) {
				times++;
			}
			i++;
		}
		return out;
	}

	/**
	 * Return a user if there is one with the given name.
	 */
	public static Optional<User> getUser(String name) {
		try {
			return getUser(UUID.fromString(name));
		} catch (Exception e) {
			if (Sponge.getServer().getPlayer(name).isPresent()) {
				return wrap(Sponge.getServer().getPlayer(name).orElse(null));
			}
			UserStorageService storage = Ray.get().getPlugin().getGame().getServiceManager()
					.provide(UserStorageService.class).get();
			Optional<User> opt = storage.get(name);
			return opt;
		}
	}

	/**
	 * Return a user if there is one with the given name.
	 */
	public static Optional<User> getUser(String name, boolean useCache) {
		try {
			return getUser(UUID.fromString(name), useCache);
		} catch (Exception e) {
			if (Sponge.getServer().getPlayer(name).isPresent()) {
				return wrap(Sponge.getServer().getPlayer(name).orElse(null));
			}
			UserStorageService storage = Ray.get().getPlugin().getGame().getServiceManager()
					.provide(UserStorageService.class).get();
			Optional<User> opt = storage.get(name);
			return opt;
		}
	}

	/**
	 * Return a user, if it exists, based off of the uuid.
	 */
	public static Optional<User> getUser(UUID uuid) {
		return getUser(uuid, true);
	}

	/**
	 * Return a user, if it exists, based off of the uuid.
	 */
	public static Optional<User> getUser(UUID uuid, boolean useCache) {
		return wrap(getUser(uuid, useCache, null));
	}

	private static User getUser(UUID uuid, boolean cache, Object irrelevant) {
		Optional<Player> p = Sponge.getServer().getPlayer(uuid);
		if (!p.isPresent()) {
			if (cache) {
				UserCache c = Ray.get().getCache();
				if (c.containsKey(uuid)) {
					if (c.get(uuid).isPresent()) {
						return c.get(uuid).get();
					}
				}
			}
			UserStorageService storage = Ray.get().getPlugin().getGame().getServiceManager()
					.provide(UserStorageService.class).get();
			Optional<User> opt = storage.get(uuid);
			if (opt.isPresent()) {
				return opt.get();
			}
			return null;
		}
		Player u2 = p.get();
		return u2;
	}

	/**
	 * Create a mapping of indices to objects from a list of objects.
	 */
	public static <T> Map<Integer, T> from(List<T> list) {
		Map<Integer, T> out = hm();
		for (int i = 0; i < list.size(); i++) {
			out.put(i, list.get(i));
		}
		return out;
	}

	/**
	 * Create a list of objects from a mapping of indices to objects. all
	 * integer values are allowed, but only sensible ones will be added. If the
	 * indices are not incremental, a sparse list will be returned. That is,
	 * values without index mappings will be null.
	 */
	public static <T> List<T> from(Map<Integer, T> map) {
		List<T> out = al();
		List<Integer> order = al(map.keySet(), true);
		order.sort(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Integer.compare(o1, o2);
			}

		});
		int last = 0;
		for (int i : order) {
			if (i < 0) {
				continue;
			}
			if (i == 0) {
				out.add(map.get(i));
				continue;
			}
			int dif = i - last;
			while (dif > 1) {
				out.add(null);
				dif--;
			}
			out.add(map.get(i));
			last = i;
		}
		return out;
	}

	/**
	 * Turn a list of lists into a singular list.
	 */
	public static <T> List<T> flatten(List<List<T>> list) {
		List<T> out = al();
		for (List<T> l : list) {
			for (T t : l) {
				out.add(t);
			}
		}
		return out;
	}

	/**
	 * Print the current stack trace.
	 */
	public static void stackTrace() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		boolean b = false;
		for (StackTraceElement e : trace) {
			System.out.println(
					e.getClassName() + "." + e.getMethodName() + "(" + e.getFileName() + ":" + e.getLineNumber() + ")");
			if (e.getMethodName().equals("stackTrace") && e.getClassName().endsWith("Utils")) {
				if (b) {
					return;
				} else {
					b = true;
				}
			}
		}
	}

}
