package me.Wundero.ProjectRay.utils;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ProxySource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.ProjectRay;
import me.Wundero.ProjectRay.Ray;
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

public class Utils {

	public static final String S = "";
	public static final Pattern URL_PATTERN = Pattern
			.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,}(?:/\\S+)?)");
	public static final Pattern VAR_PATTERN = Pattern.compile("[{][^{}]+[}]");

	public static ConfigurationNode load(File config) {
		return load(config.toPath());
	}

	public static Task schedule(Task.Builder b, boolean async) {
		if (async) {
			b.async();
		}
		return b.submit(Ray.get().getPlugin());
	}

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
		TransactionResult r = a.withdraw(cur, new BigDecimal(c),
				Cause.source(Ray.get().getPlugin()).named("Purchase", c).build());
		switch (r.getResult()) {
		case SUCCESS:
			return true;
		default:
			return false;
		}
	}

	public static <T> List<T> sl(Collection<T> objs) {
		List<T> l = sl();
		l.addAll(objs);
		return l;
	}

	@SafeVarargs
	public static <T> List<T> sl(T... objs) {
		List<T> l = sl();
		for (T t : objs) {
			l.add(t);
		}
		return l;
	}

	public static <K, V> OptionalMap<K, V> om(Map<K, V> pre) {
		OptionalMap<K, V> o = om();
		o.putAll(pre);
		return o;
	}

	public static <K, V> OptionalMap<K, V> om() {
		return new OptionalMap<K, V>();
	}

	public static <T> List<T> sl() {
		// returns a new arraylist that copies itself for iteration (makes
		// unsynchronized iter blocks safe)
		// indexOf method coded such that the equals call supports either object
		// equaling the other
		// this allows support for ChannelMember; it's less strict but usefull
		// to me.
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
						if (get(i).equals(o) || o.equals(get(i))) {
							return i;
						}
					}
				}
				return -1;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<T> iterator() {
				return (Iterator<T>) ImmutableList.builder().add(this.toArray()).build().iterator();
			}

		});
	}

	public static <K, V> Map<K, V> sm(Map<? extends K, ? extends V> m) {
		Map<K, V> m1 = sm();
		m1.putAll(m);
		return m1;
	}

	public static <K, V> Map<K, V> sm() {
		return new ConcurrentHashMap<K, V>();
	}

	public static boolean classinstanceof(String main, Class<?> sub) {
		// recursive -- call with caution
		boolean b = sub.getName().equalsIgnoreCase(main);
		if (!b && sub.equals(Object.class)) {
			return false;
		}
		return b ? b : classinstanceof(main, sub.getSuperclass());
	}

	public static double difference(Location<World> loc1, Location<World> loc2) {
		Validate.isTrue(loc1.getExtent().equals(loc2.getExtent()), "different worlds");
		double x1 = loc1.getX(), x2 = loc2.getX(), y1 = loc1.getY(), y2 = loc2.getY(), z1 = loc1.getZ(),
				z2 = loc2.getZ();
		double l1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2) + Math.pow(z1, 2)),
				l2 = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2) + Math.pow(z2, 2));
		return Math.abs(l1 - l2);
	}

	public static boolean inRange(Location<World> loc1, Location<World> loc2, double range) {
		if (range == Double.MAX_VALUE) {
			return true;
		}
		// pythagorean range calc
		if (range < 0) {
			return true;
		}
		if (range == 0 || !loc1.getExtent().equals(loc2.getExtent())) {
			return false;
		}
		return difference(loc1, loc2) <= range;
	}

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

	public static CommandSource getTrueSource(CommandSource src) {
		CommandSource out = src;
		while (out instanceof ProxySource) {
			out = ((ProxySource) out).getOriginalSource();
		}
		return out;
	}

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

	public static void save(File file, ConfigurationNode root) {
		save(file.toPath(), root);
	}

	public static void save(Path file, ConfigurationNode root) {
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(file)
				.build();
		try {
			loader.save(root);
		} catch (IOException e) {
			Utils.printError(e);
		}
	}

	public static boolean hasSections(ConfigurationNode config, String... toValidate) {
		for (String s : toValidate) {
			if (!config.getChildrenMap().containsKey(s)) {
				return false;
			}
		}
		return true;
	}

	public static <T> List<T> scramble(final List<T> list) {
		List<T> out = sl();
		List<T> in = sl(list);
		Random r = new Random();
		while (!in.isEmpty()) {
			out.add(in.remove(r.nextInt(in.size())));
		}
		return out;
	}

	public static <T> boolean containsOf(List<T> arr, List<T> comp) {
		for (T o : comp) {
			if (arr.contains(o)) {
				return true;
			}
		}
		return false;
	}

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

	public static boolean isURL(String input) {
		String s = input;
		while (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		return URL_PATTERN.matcher(TextUtils.strip(TextSerializers.FORMATTING_CODE.serialize(TextUtils.trans(s))))
				.matches();
	}

	public static boolean containsURL(String input) {
		return URL_PATTERN.matcher(TextUtils.strip(TextSerializers.FORMATTING_CODE.serialize(TextUtils.trans(input))))
				.find();
	}

	public static double convert(double val, PRTimeUnit from, PRTimeUnit to) {
		return to.convert(val, from);
	}

	public static String join(Iterable<String> k) {
		return join(", ", k);
	}

	public static String join(String... k) {
		return join(", ", k);
	}

	public static String join(String sep, String... k) {
		return join(sep, sl(k));
	}

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

	public synchronized static String getAnsi(TextElement c) {
		return replacements.get(c);
	}

	private static final Map<TextElement, String> replacements = Maps.newHashMap();
	static {
		replacements.put(TextColors.BLACK, Ansi.ansi().reset().fg(Ansi.AnsiColor.BLACK).boldOff().toString());
		replacements.put(TextColors.DARK_BLUE, Ansi.ansi().reset().fg(Ansi.AnsiColor.BLUE).boldOff().toString());
		replacements.put(TextColors.DARK_GREEN, Ansi.ansi().reset().fg(Ansi.AnsiColor.GREEN).boldOff().toString());
		replacements.put(TextColors.DARK_AQUA, Ansi.ansi().reset().fg(Ansi.AnsiColor.CYAN).boldOff().toString());
		replacements.put(TextColors.DARK_RED, Ansi.ansi().reset().fg(Ansi.AnsiColor.RED).boldOff().toString());
		replacements.put(TextColors.DARK_PURPLE, Ansi.ansi().reset().fg(Ansi.AnsiColor.MAGENTA).boldOff().toString());
		replacements.put(TextColors.GOLD, Ansi.ansi().reset().fg(Ansi.AnsiColor.YELLOW).boldOff().toString());
		replacements.put(TextColors.GRAY, Ansi.ansi().reset().fg(Ansi.AnsiColor.WHITE).boldOff().toString());
		replacements.put(TextColors.DARK_GRAY, Ansi.ansi().reset().fg(Ansi.AnsiColor.BLACK).bold().toString());
		replacements.put(TextColors.BLUE, Ansi.ansi().reset().fg(Ansi.AnsiColor.BLUE).bold().toString());
		replacements.put(TextColors.GREEN, Ansi.ansi().reset().fg(Ansi.AnsiColor.GREEN).bold().toString());
		replacements.put(TextColors.AQUA, Ansi.ansi().reset().fg(Ansi.AnsiColor.CYAN).bold().toString());
		replacements.put(TextColors.RED, Ansi.ansi().reset().fg(Ansi.AnsiColor.RED).bold().toString());
		replacements.put(TextColors.LIGHT_PURPLE, Ansi.ansi().reset().fg(Ansi.AnsiColor.MAGENTA).bold().toString());
		replacements.put(TextColors.YELLOW, Ansi.ansi().reset().fg(Ansi.AnsiColor.YELLOW).bold().toString());
		replacements.put(TextColors.WHITE, Ansi.ansi().reset().fg(Ansi.AnsiColor.WHITE).bold().toString());
		replacements.put(TextStyles.OBFUSCATED, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
		replacements.put(TextStyles.BOLD, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
		replacements.put(TextStyles.STRIKETHROUGH, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
		replacements.put(TextStyles.UNDERLINE, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
		replacements.put(TextStyles.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
		replacements.put(TextColors.RESET, Ansi.ansi().reset().toString());
	}

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

	public static <T> List<String> toStringList(List<T> o, Optional<Method> toCall) {
		List<String> out = sl();
		if (!toCall.isPresent() || toCall.get().getReturnType() != String.class
				|| toCall.get().getParameters().length > 0) {
			for (Object o1 : o) {
				out.add(o1.toString());
			}
			return out;
		}
		Method m = toCall.get();
		for (Object o1 : o) {
			List<Method> objMethods = sl(o1.getClass().getMethods());
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

	// before index i
	public static <T> List<T> sub(int i, Iterable<T> j) {
		List<T> o = sl();
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

	@SafeVarargs
	public static <T> List<T> sub(int i, T... j) {
		List<T> o = sl();
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

	// after and including index i
	public static <T> List<T> post(int i, Iterable<T> j) {
		List<T> o = sl();
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

	@SafeVarargs
	public static <T> List<T> post(int i, T... j) {
		List<T> o = sl();
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

	public static <T> List<T> removeDuplicates(List<T> list) {
		return sl(list.stream().distinct().collect(Collectors.toList()));
	}

	public static <T> T pop(Deque<T> deque, T def) {
		return deque.isEmpty() ? def : deque.pop();
	}

	public static boolean call(Event e) {
		return !Sponge.getEventManager().post(e);
	}

	public static Pattern profanityPattern() {
		return compile("(" + "(f[ua4][c]?[k]([e3]r|[a4])?)|(b[i!1]?tch([e3]s|y)?)|(s(lut|h[i!1]t[3e]?))"
				+ "|(n[i1!]g[g]?([a4]|[3e]r))|([4a][sz]{2})|(cunt)|(d[i!1][c]?k)|([kd][yi1!]ke)"
				+ "|(f[a43e]g([g]?[0oi1!]t)?)|(cum|j[i1!]zz)|(p[e3]n[i1!u]s)|(qu[3e]{2}r)"
				+ "|(pu[zs]{2}y)|(tw[4a]t)|(v[a4]g[i1!]n[a4])|(wh[o0]r[e3])"
				+ "|([ck][o0][ck]?[k])|([d][o][ou][c][h][e])" + ")", Pattern.CASE_INSENSITIVE);
	}

	public static boolean containsProfanities(String s) {
		return profanityPattern().matcher(s).find();
	}

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

	public static <T> List<T> alternate(T[] one, T[] two) {
		int dif = one.length - two.length;
		List<T> out = sl();
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

	public static <T> List<T> toList(T[] array) {
		return sl(array);
	}

	public static <T> List<T> alternate(List<T> one, List<T> two) {
		int dif = one.size() - two.size();
		List<T> out = sl();
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

	public static <T> Optional<T> getFirst(List<T> list) {
		if (list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(list.get(0));
	}

	public static <T> Optional<T> getLast(List<T> list) {
		if (list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(list.get(list.size() - 1));
	}

	public static Character makeUnicode(Character c) {
		int v = c.charValue();
		if (v < 33 || v > 126) {
			return c;
		}
		return Character.valueOf((char) (v + 65248));// magic number lol
	}

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

	public static <T> List<T> fill(T original, BiFunction<T, Integer, T> mod, int times, boolean includeOriginal) {
		List<T> out = sl();
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

	private static List<Player> playersAlphabetical = sl();

	private static List<Player> sort(List<Player> list) {
		list.sort(new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		return list;
	}

	public static void updatePlayersAlpha() {
		playersAlphabetical = sort(sl(Ray.get().getPlugin().getGame().getServer().getOnlinePlayers()));

	}

	public static List<Player> getPlayers() {
		updatePlayersAlpha();
		return sl(playersAlphabetical);
	}

	public static TextColor randomColor() {
		int hex = new Random().nextInt(16);
		String h = Integer.toHexString(hex);
		return TextSerializers.FORMATTING_CODE.deserialize("&" + h).getColor();
	}

}
