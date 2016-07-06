package me.Wundero.ProjectRay.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.ServerType;
import me.Wundero.ProjectRay.fanciful.FancyMessage;
import me.Wundero.ProjectRay.fanciful.JsonString;
import me.Wundero.ProjectRay.fanciful.MessagePart;
import me.Wundero.ProjectRay.fanciful.NullMessagePart;
import me.Wundero.ProjectRay.fanciful.TextualComponent;
import me.Wundero.ProjectRay.framework.PlayerWrapper;
import me.Wundero.ProjectRay.framework.common.Color;
import me.Wundero.ProjectRay.framework.config.ConfigSection;

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
//TODO move bukkit stuff to BukkitUtils class and Sponge stuff to SpongeUtils class
public class Utils {

	private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
	private static List<ScheduledFuture<?>> threads = Lists.newArrayList();

	public static synchronized int run(Runnable r, int delay, int repeat) {
		threads.add(executor.scheduleAtFixedRate(r, delay * 50, repeat * 50, TimeUnit.MILLISECONDS));
		return threads.size() - 1;
	}

	public static synchronized int run(Runnable r, int delay) {
		threads.add(executor.schedule(r, delay * 50, TimeUnit.MILLISECONDS));
		return threads.size() - 1;
	}

	public static synchronized int run(Runnable r) {
		return run(r, 0);
	}

	public static boolean cancel(int task) {
		return threads.get(task).cancel(false);
	}

	private static final FancyMessage F = new FancyMessage("");
	public static final String S = "";
	public static final Pattern URL_PATTERN = Pattern
			.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,}(?:/\\S+)?)");

	public static FancyMessage F() {
		try {
			return F.clone();
		} catch (CloneNotSupportedException e) {
			return F;
		}
	}

	public static boolean isBungee() {
		return Ray.get().getPlugin().getType() == ServerType.BUNGEE;
	}

	public static boolean isRedisBungee() {
		return Ray.get().getPlugin().getType() == ServerType.REDIS_BUNGEE;
	}

	public static boolean isSponge() {
		return Ray.get().getPlugin().getType() == ServerType.SPONGE;
	}

	public static boolean isBukkit() {
		return Ray.get().getPlugin().getType() == ServerType.BUKKIT;
	}

	public static boolean isSpigot() {
		return Ray.get().getPlugin().getType() == ServerType.SPIGOT;
	}

	public static boolean isPaperSpigot() {
		return Ray.get().getPlugin().getType() == ServerType.PAPER_SPIGOT;
	}

	public static boolean isForge() {
		return Ray.get().getPlugin().getType() == ServerType.FORGE;
	}

	public static boolean isCauldren() {
		return Ray.get().getPlugin().getType() == ServerType.CAULDRON;
	}

	public static boolean validateConfigSections(ConfigSection config, String... toValidate) {
		for (String s : toValidate) {
			if (!config.contains(s)) {
				return false;
			}
		}
		return true;
	}

	public static <T> ArrayList<T> scramble(ArrayList<T> list) {
		List<T> out = Lists.newArrayList();
		List<T> in = Lists.newArrayList(list);
		Random r = new Random();
		while (!in.isEmpty()) {
			out.add(in.remove(r.nextInt(in.size())));
		}
		return (ArrayList<T>) out;
	}

	public static boolean containsOf(ArrayList<?> arr, ArrayList<?> comp) {
		for (Object o : comp) {
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
		return Pattern.compile(pattern, flag);
	}

	// Move this to Bukkit
	/*
	 * @SuppressWarnings("deprecation") public static OfflinePlayer
	 * getPlayer(String name) { if (Bukkit.getOfflinePlayer(name) != null &&
	 * Bukkit.getOfflinePlayer(name).getFirstPlayed() > 0) { return
	 * Bukkit.getOfflinePlayer(name); } return null; }
	 * 
	 * public static OfflinePlayer getPlayer(UUID uuid) { return
	 * Bukkit.getOfflinePlayer(uuid); }
	 */

	public static final UUID pregenUUID = UUID.randomUUID();

	private static HashMap<UUID, Long> times = Maps.newHashMap();

	public static <T> ArrayList<T> modifiedList(ArrayList<T> in, Modifier<T, ?> mod) {
		ArrayList<T> out = Lists.newArrayList();
		for (T t : in) {
			@SuppressWarnings("unchecked")
			T t2 = mod.modify(t);
			out.add(t2);
		}
		return out;
	}

	public static MessagePart insertURLData(MessagePart m, String strippedURL) {
		if (m.clickActionData != null) {
			return m;
		}
		if (m.hoverActionData != null) {
			return m;
		}
		if (!strippedURL.startsWith("www.") && !strippedURL.startsWith("http://")
				&& !strippedURL.startsWith("https://")) {
			strippedURL = "http://" + strippedURL;
		}
		m.clickActionName = "open_url";
		m.clickActionData = strippedURL;
		String kf = "&7Click to open &9&n%s%7!";
		kf = kf.replace("%s", strippedURL);
		if (!kf.isEmpty()) {
			m.hoverActionData = new JsonString(kf);
		}
		return m;
	}

	@SafeVarargs
	public static <T> ArrayList<T> of(T... t) {
		return Lists.newArrayList(t);
	}

	public static boolean isURL(String input) {
		String s = input;
		while (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		return URL_PATTERN.matcher(strip(trans(s))).matches();
	}

	public static boolean containsURL(String input) {
		return URL_PATTERN.matcher(strip(trans(input))).find();
	}

	public static double convert(double val, PRTimeUnit from, PRTimeUnit to) {
		return to.convert(val, from);
	}

	public static String strip(String s) {// TODO figure out way to make this
											// sponge compatible
		return Color.stripColor(s);
	}

	public static String join(Iterable<String> k) {
		return join(", ", k);
	}

	public static String join(String... k) {
		return join(", ", k);
	}

	public static String join(String sep, String... k) {
		return join(sep, Lists.newArrayList(k));
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

	public static boolean badEnding(String url) {
		return url.endsWith(".") || url.endsWith("!") || url.endsWith("?") || url.endsWith("!") || url.endsWith(">")
				|| url.endsWith("<");
	}

	public synchronized static String getAnsi(Color c) {// TODO spongify
		return replacements.get(c);
	}

	private static final Map<Color, String> replacements = Maps.newHashMap();
	static {
		replacements.put(Color.BLACK, Ansi.ansi().reset().fg(Ansi.AnsiColor.BLACK).boldOff().toString());
		replacements.put(Color.DARK_BLUE, Ansi.ansi().reset().fg(Ansi.AnsiColor.BLUE).boldOff().toString());
		replacements.put(Color.DARK_GREEN, Ansi.ansi().reset().fg(Ansi.AnsiColor.GREEN).boldOff().toString());
		replacements.put(Color.DARK_AQUA, Ansi.ansi().reset().fg(Ansi.AnsiColor.CYAN).boldOff().toString());
		replacements.put(Color.DARK_RED, Ansi.ansi().reset().fg(Ansi.AnsiColor.RED).boldOff().toString());
		replacements.put(Color.DARK_PURPLE, Ansi.ansi().reset().fg(Ansi.AnsiColor.MAGENTA).boldOff().toString());
		replacements.put(Color.GOLD, Ansi.ansi().reset().fg(Ansi.AnsiColor.YELLOW).boldOff().toString());
		replacements.put(Color.GRAY, Ansi.ansi().reset().fg(Ansi.AnsiColor.WHITE).boldOff().toString());
		replacements.put(Color.DARK_GRAY, Ansi.ansi().reset().fg(Ansi.AnsiColor.BLACK).bold().toString());
		replacements.put(Color.BLUE, Ansi.ansi().reset().fg(Ansi.AnsiColor.BLUE).bold().toString());
		replacements.put(Color.GREEN, Ansi.ansi().reset().fg(Ansi.AnsiColor.GREEN).bold().toString());
		replacements.put(Color.AQUA, Ansi.ansi().reset().fg(Ansi.AnsiColor.CYAN).bold().toString());
		replacements.put(Color.RED, Ansi.ansi().reset().fg(Ansi.AnsiColor.RED).bold().toString());
		replacements.put(Color.LIGHT_PURPLE, Ansi.ansi().reset().fg(Ansi.AnsiColor.MAGENTA).bold().toString());
		replacements.put(Color.YELLOW, Ansi.ansi().reset().fg(Ansi.AnsiColor.YELLOW).bold().toString());
		replacements.put(Color.WHITE, Ansi.ansi().reset().fg(Ansi.AnsiColor.WHITE).bold().toString());
		replacements.put(Color.MAGIC, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
		replacements.put(Color.BOLD, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
		replacements.put(Color.STRIKETHROUGH, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
		replacements.put(Color.UNDERLINE, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
		replacements.put(Color.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
		replacements.put(Color.RESET, Ansi.ansi().reset().toString());
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

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Iterable<T> collection) {
		ArrayList<T> wrappedCollection = Lists.newArrayList(collection);
		return (T[]) wrappedCollection.toArray(new Object[wrappedCollection.size()]);
	}

	public static <T> ArrayList<String> toStringList(ArrayList<T> o, Optional<Method> toCall) {
		ArrayList<String> out = Lists.newArrayList();
		if (!toCall.isPresent() || toCall.get().getReturnType() != String.class
				|| toCall.get().getParameters().length > 0) {
			for (Object o1 : o) {
				out.add(o1.toString());
			}
			return out;
		}
		Method m = toCall.get();
		for (Object o1 : o) {
			ArrayList<Method> objMethods = Lists.newArrayList(o1.getClass().getMethods());
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

	// TODO sponge
	public static boolean isFormat(Color paramColor) {
		return paramColor.isFormat();
	}

	public static String concat(String sep, String... a) {
		String o = "";
		String f = "";
		for (String s : a) {
			o += f + s;
			f = sep;
		}
		return o;
	}

	// TODO sponge
	public static String trans(String s) {
		if (s == null) {
			return "";
		}
		if (s.contains("\\u00a7")) {
			s = s.replace("\\u00a7", "&");
		}
		if (!s.contains("&")) {
			return s;
		}
		Pattern p = Pattern.compile("[&][0123456789AaBbCcDdEeFfKkLlMmNnOoRr]");

		Matcher m = p.matcher(s);
		while (m.find()) {
			String s2 = m.group();
			String s1 = s2;
			s2 = s2.replace("&", Color.COLOR_CHAR + "");
			s = s.replace(s1, s2);
		}
		return s;
	}

	public static void printError(Exception e) {
		ArrayList<String> toPrint = Lists.newArrayList(e.getMessage());
		for (StackTraceElement element : e.getStackTrace()) {
			toPrint.add("at " + element.toString());
		}
		for (String s : toPrint) {
			Ray.get().getPlugin().log(s, Level.SEVERE);
		}
	}

	// before index i
	public static <T> ArrayList<T> sub(int i, Iterable<T> j) {
		ArrayList<T> o = Lists.newArrayList();
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
	public static <T> ArrayList<T> sub(int i, T... j) {
		ArrayList<T> o = Lists.newArrayList();
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
	public static <T> ArrayList<T> post(int i, Iterable<T> j) {
		ArrayList<T> o = Lists.newArrayList();
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
	public static <T> ArrayList<T> post(int i, T... j) {
		ArrayList<T> o = Lists.newArrayList();
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

	public static String concat(String sep, Iterable<String> a) {
		String o = "";
		String f = "";
		for (String s : a) {
			o += f + s;
			f = sep;
		}
		return o;
	}

	// TODO sponge
	/*
	 * public static boolean call(Cancellable e) {
	 * Bukkit.getPluginManager().callEvent((Event) e); return !e.isCancelled();
	 * }
	 */

	public static UUID time() {
		UUID u = UUID.randomUUID();
		times.put(u, System.nanoTime());
		return u;
	}

	public static boolean containsProfanities(String s) {
		return compile("(" + "(f[ua4][c]?[k]([e3]r|[a4])?)|(b[i!1]?tch([e3]s|y)?)|(s(lut|h[i!1]t[3e]?))"
				+ "|(n[i1!]g[g]?([a4]|[3e]r))|([4a][sz]{2})|(cunt)|(d[i!1][c]?k)|([kd][yi1!]ke)"
				+ "|(f[a43e]g([g]?[0oi1!]t)?)|(cum|j[i1!]zz)|(p[e3]n[i1!u]s)|(qu[3e]{2}r)"
				+ "|(pu[zs]{2}y)|(tw[4a]t)|(v[a4]g[i1!]n[a4])|(wh[o0]r[e3])"
				+ "|([ck][o0][ck]?[k])|([d][o][ou][c][h][e])" + ")", Pattern.CASE_INSENSITIVE).matcher(s).find();
	}

	public static void checkTime(long n, String m) {
		if (!isSafeTime(n)) {
			Ray.get().getPlugin().log("Warning: unsafe time for " + m, Level.WARNING);
		}
	}

	public static long getTime(UUID u) {
		if (times.containsKey(u)) {
			long l = times.get(u);
			times.remove(u);
			return System.nanoTime() - l;
		} else {
			return 0;
		}
	}

	public static boolean isSafeTime(long nanoseconds) {
		return nanoseconds < 200000000l;
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

	public static <T> T getFirst(ArrayList<T> list) {
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public static <T> T getLast(ArrayList<T> list) {
		if (list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	public static Character makeUnicode(Character c) {
		int v = c.charValue();
		if (v < 33 || v > 126) {
			return c;
		}
		return Character.valueOf((char) (v + 65248));
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

	// TODO getAllOnline();

	private static ArrayList<PlayerWrapper<?>> playersAlphabetical = Lists.newArrayList();

	@SuppressWarnings("unused")
	private static ArrayList<PlayerWrapper<?>> sort(ArrayList<PlayerWrapper<?>> list) {// NOTE:
																						// REQUIRES
																						// JAVA
																						// 8
																						// -
																						// TODO
																						// MAKE
																						// IT
																						// J7
		list.sort(new Comparator<PlayerWrapper<?>>() {
			@Override
			public int compare(PlayerWrapper<?> o1, PlayerWrapper<?> o2) {
				return o1.getLastName().compareToIgnoreCase(o2.getLastName());
			}
		});
		return list;
	}

	public static void updatePlayersAplha() {
		/*
		 * playersAlphabetical = sort(Lists .newArrayList(players));
		 */
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<PlayerWrapper<?>> getPlayers() {
		return (ArrayList<PlayerWrapper<?>>) playersAlphabetical.clone();
	}

	public static FancyMessage applyRegex(final FancyMessage message, final Pattern regex, final MessagePart actionData,
			final Modifier<String, String> clickDataModifier, final Modifier<JsonString, String> hoverDataModifier) {
		FancyMessage message1 = recompile(message);
		String m = "";
		int i = 0;
		HashMap<Integer, MessagePart> indices = Maps.newHashMap();
		HashMap<MessagePart, ArrayList<Integer>> reversedIndices = Maps.newHashMap();
		for (MessagePart p : message1.getList()) {
			String s = p.getText();
			m += s;
			int f = i;
			ArrayList<Integer> ix = Lists.newArrayList();
			for (; i < (s.length() + f); i++) {
				indices.put(i, p);
				ix.add(i);
			}
			reversedIndices.put(p, ix);
		}
		// indices.values() is in order based on lowest to highest index
		if (!regex.matcher(m).find()) {
			return message;
		}
		Matcher matcher = regex.matcher(m);
		matcher.find();
		ArrayList<MessagePart> recompiled = Lists.newArrayList();
		for (i = 0; i < matcher.groupCount() - 1; i++) {
			String group = matcher.group(i);
			int index = m.indexOf(group);
			int endex = m.indexOf(group) + group.length();
			String g = group;
			ArrayList<MessagePart> allMessageParts = Lists.newArrayList();
			boolean trimFront = true, trimEnd = true;
			for (int f = index - 1; f <= endex + 1; f++) {
				if (indices.containsKey(f)) {
					MessagePart p = indices.get(f);
					if (!allMessageParts.contains(p)) {
						allMessageParts.add(p);
						if (f == index) {
							trimFront = false;
						}
						if (f == endex + 1) {
							trimEnd = false;
						}
					}
				}
			}
			if (!trimFront) {
				allMessageParts = Utils.post(1, allMessageParts);
			} else {
				MessagePart partOne = Utils.getFirst(allMessageParts);
				int indexClone = index;
				indexClone -= m.substring(0, m.indexOf(partOne.getText())).length();
				MessagePart partToMod = partOne.clone();
				String text = partOne.getText();
				String p1text = text.substring(0, indexClone);
				String p2text = text.substring(indexClone);
				partOne.text = TextualComponent.rawText(p1text);
				partToMod.text = TextualComponent.rawText(p2text);
				allMessageParts.set(0, partToMod);
				ArrayList<Integer> k = Lists.newArrayList();
				for (int j = m.indexOf(text); j < index; j++) {
					indices.put(j, partOne);
					k.add(j);
				}
				reversedIndices.put(partOne, k);
				ArrayList<Integer> k1 = Lists.newArrayList();
				for (int j = index; j < index + partToMod.getText().length(); j++) {
					indices.put(j, partToMod);
					k1.add(j);
				}
				reversedIndices.put(partToMod, k1);
			}
			if (!trimEnd) {
				allMessageParts = Utils.sub(allMessageParts.size() - 1, allMessageParts);
			} else {
				MessagePart partOne = Utils.getLast(allMessageParts);
				int indexClone = endex;
				indexClone -= m.substring(0, m.indexOf(partOne.getText())).length();
				MessagePart partToMod = partOne.clone();
				String text = partOne.getText();
				String p1text = text.substring(0, indexClone);
				String p2text = text.substring(indexClone);
				partOne.text = TextualComponent.rawText(p1text);
				partToMod.text = TextualComponent.rawText(p2text);
				allMessageParts.set(allMessageParts.size() - 1, partOne);
				ArrayList<Integer> k = Lists.newArrayList();
				for (int j = m.indexOf(text); j < index; j++) {
					indices.put(j, partOne);
					k.add(j);
				}
				reversedIndices.put(partOne, k);
				ArrayList<Integer> k1 = Lists.newArrayList();
				for (int j = endex; j < endex + partToMod.getText().length(); j++) {
					indices.put(j, partToMod);
					k1.add(j);
				}
				reversedIndices.put(partToMod, k1);
			}
			for (MessagePart m1 : allMessageParts) {
				ArrayList<Integer> indixes = reversedIndices.get(m1);
				m1.clickActionData = clickDataModifier.modify(actionData.clickActionData, g);
				m1.clickActionName = actionData.clickActionName;
				m1.hoverActionData = hoverDataModifier.modify((JsonString) actionData.hoverActionData, g);
				m1.hoverActionName = actionData.hoverActionName;
				for (int j : indixes) {
					indices.put(j, m1);
				}
			}
		}
		recompiled = Lists.newArrayList();
		for (MessagePart m2 : indices.values()) {
			if (!recompiled.contains(m2)) {
				recompiled.add(m2);
			}
		}
		FancyMessage fm = new FancyMessage();
		fm.setList(recompiled);
		return recompile(fm);
	}

	// TODO move to BukkitUtils
	public static FancyMessage replaceRegex(final FancyMessage message, final Pattern regex,
			final FancyMessage replaceWith) {
		FancyMessage message1 = recompile(message);
		FancyMessage message2 = recompile(replaceWith);
		String m = "";
		int i = 0;
		HashMap<Integer, MessagePart> indices = Maps.newHashMap();
		HashMap<MessagePart, ArrayList<Integer>> reversedIndices = Maps.newHashMap();
		for (MessagePart p : message1.getList()) {
			String s = p.getText();
			m += s;
			int f = i;
			ArrayList<Integer> ix = Lists.newArrayList();
			for (; i < (s.length() + f); i++) {
				indices.put(i, p);
				ix.add(i);
			}
			reversedIndices.put(p, ix);
		}
		// indices.values() is in order based on lowest to highest index

		Matcher matcher = regex.matcher(m).reset();
		matcher.find();
		ArrayList<MessagePart> recompiled = Lists.newArrayList();
		for (i = 0; i < matcher.groupCount(); i++) {
			String group = matcher.group(i);
			int index = m.indexOf(group);
			int endex = m.indexOf(group) + group.length();
			ArrayList<MessagePart> allMessageParts = Lists.newArrayList();
			boolean trimFront = true, trimEnd = true;
			for (int f = index - 1; f <= endex + 1; f++) {
				if (indices.containsKey(f)) {
					MessagePart p = indices.get(f);
					if (!allMessageParts.contains(p)) {
						allMessageParts.add(p);
						if (f == index) {
							trimFront = false;
						}
						if (f == endex + 1) {
							trimEnd = false;
						}
					}
				}
			}
			if (!trimFront) {
				allMessageParts = Utils.post(1, allMessageParts);
			} else {
				MessagePart partOne = Utils.getFirst(allMessageParts);
				int indexClone = index;
				indexClone -= m.substring(0, m.indexOf(partOne.getText())).length();
				MessagePart partToMod = partOne.clone();
				String text = partOne.getText();
				String p1text = text.substring(0, indexClone);
				String p2text = text.substring(indexClone);
				partOne.text = TextualComponent.rawText(p1text);
				partToMod.text = TextualComponent.rawText(p2text);
				allMessageParts.set(0, partToMod);
				ArrayList<Integer> k = Lists.newArrayList();
				for (int j = m.indexOf(text); j < index; j++) {
					indices.put(j, partOne);
					k.add(j);
				}
				reversedIndices.put(partOne, k);
				ArrayList<Integer> k1 = Lists.newArrayList();
				for (int j = index; j < index + partToMod.getText().length(); j++) {
					indices.put(j, partToMod);
					k1.add(j);
				}
				reversedIndices.put(partToMod, k1);
			}
			if (!trimEnd) {
				allMessageParts = Utils.sub(allMessageParts.size() - 1, allMessageParts);
			} else {
				MessagePart partOne = Utils.getLast(allMessageParts);
				int indexClone = endex;
				indexClone -= m.substring(0, m.indexOf(partOne.getText())).length();
				MessagePart partToMod = partOne.clone();
				String text = partOne.getText();
				String p1text = text.substring(0, indexClone);
				String p2text = text.substring(indexClone);
				partOne.text = TextualComponent.rawText(p1text);
				partToMod.text = TextualComponent.rawText(p2text);
				allMessageParts.set(allMessageParts.size() - 1, partOne);
				ArrayList<Integer> k = Lists.newArrayList();
				for (int j = m.indexOf(text); j < index; j++) {
					indices.put(j, partOne);
					k.add(j);
				}
				reversedIndices.put(partOne, k);
				ArrayList<Integer> k1 = Lists.newArrayList();
				for (int j = endex; j < endex + partToMod.getText().length(); j++) {
					indices.put(j, partToMod);
					k1.add(j);
				}
				reversedIndices.put(partToMod, k1);
			}
			ArrayList<Integer> allIndixesToReplace = Lists.newArrayList();
			for (MessagePart m1 : allMessageParts) {
				ArrayList<Integer> indixes = reversedIndices.get(m1);
				allIndixesToReplace.addAll(indixes);
			}
			for (int j : allIndixesToReplace) {
				indices.put(j, NullMessagePart.PART);
			}
		}
		recompiled = Lists.newArrayList();
		ArrayList<Integer> toA = Lists.newArrayList();
		for (MessagePart m2 : indices.values()) {
			if (m2 == NullMessagePart.PART || m2.text == null) {
				toA.add(recompiled.size() - 1);
				continue;
			}
			if (!recompiled.contains(m2)) {
				recompiled.add(m2);
			}
		}
		for (int it : toA) {
			ArrayList<MessagePart> l = message2.getList();
			for (int ix = l.size() - 1; i >= 0; i--) {
				recompiled.add(it, l.get(ix));
			}
		}
		FancyMessage fm = new FancyMessage();
		fm.setList(recompiled);
		return recompile(fm);
	}

	// TODO move to BukkitUtils
	/**
	 * Splits the message into proper color-based message parts Ex: blah&bhalb
	 * becomes {blah},{&bblah} (crude pseudocode, but similar concept) Hover and
	 * click data is not modified.
	 * 
	 * meh, fast enough
	 * 
	 * @return The recompiled message
	 */
	public static FancyMessage recompile(final FancyMessage message) {
		if (message == null) {
			return null;
		}
		ArrayList<MessagePart> toCompile = Lists.newArrayList();
		try {
			FancyMessage in = message.clone();
			MessagePart last = null;
			for (MessagePart part : in.getList()) {
				part.checkColor();
				if (part.color == null) {
					if (last != null && last.color != null) {
						part.color = last.color;
						// colors should reset styles from last, so no parse if
						// color is not null
						if (last != null && last.styles != null && !last.styles.isEmpty()) {
							if (part.styles == null) {
								part.styles = last.styles;
							} else {
								for (Color style : last.styles) {
									if (part.styles.contains(style)) {
										continue;
									}
									part.styles.add(style);
								}
							}
						}
					}
				}
				String text = part.getText(true);
				if (text.contains("" + Color.COLOR_CHAR)) {
					FancyMessage sub = makeFancy(text);
					MessagePart l = null;
					for (MessagePart subpart : sub.getList()) {
						subpart.clickActionData = part.clickActionData;
						subpart.clickActionName = part.clickActionName;
						subpart.hoverActionData = part.hoverActionData;
						subpart.hoverActionName = part.hoverActionName;
						subpart.applyCols();
						l = subpart;
						toCompile.add(subpart);
					}
					last = l;
				} else {
					last = part;
					toCompile.add(part);
				}
			}
			FancyMessage out = new FancyMessage();
			ArrayList<MessagePart> comp = Lists.newArrayList();
			for (MessagePart part : toCompile) {
				if (!Utils.strip(part.getText(true)).isEmpty()) {
					comp.add(part);
				}
			}
			out.setList(comp);
			return out;
		} catch (CloneNotSupportedException e) {
		}
		return message;
	}

	// TODO move to BukkitUtils
	// translates text (only difference)
	public static FancyMessage recompile(final FancyMessage message, boolean i) {
		if (message == null) {
			return null;
		}
		ArrayList<MessagePart> toCompile = Lists.newArrayList();
		try {
			FancyMessage in = message.clone();
			MessagePart last = null;
			for (MessagePart part : in.getList()) {
				part.checkColor();
				if (part.color == null) {
					if (last != null && last.color != null) {
						part.color = last.color;
						// colors should reset styles from last, so no parse if
						// color is not null
						if (last != null && last.styles != null && !last.styles.isEmpty()) {
							if (part.styles == null) {
								part.styles = last.styles;
							} else {
								for (Color style : last.styles) {
									if (part.styles.contains(style)) {
										continue;
									}
									part.styles.add(style);
								}
							}
						}
					}
				}
				String text = part.getText(true);
				text = Utils.trans(text);
				if (text.contains("" + Color.COLOR_CHAR)) {
					FancyMessage sub = makeFancy(text);
					MessagePart l = null;
					for (MessagePart subpart : sub.getList()) {
						subpart.clickActionData = part.clickActionData;
						subpart.clickActionName = part.clickActionName;
						subpart.hoverActionData = part.hoverActionData;
						subpart.hoverActionName = part.hoverActionName;
						l = subpart;
						toCompile.add(subpart);
					}
					last = l;
				} else {
					last = part;
					toCompile.add(part);
				}
			}
			FancyMessage out = new FancyMessage();
			ArrayList<MessagePart> comp = Lists.newArrayList();
			for (MessagePart part : toCompile) {
				if (!Utils.strip(part.getText(true)).isEmpty()) {
					comp.add(part);
				}
			}
			out.setList(comp);
			return out;
		} catch (CloneNotSupportedException e) {
		}
		return message;
	}// TODO move to BukkitUtils

	public static FancyMessage makeFancy(String paramString) {
		if (paramString == null) {
			paramString = "";
		}
		if (!paramString.contains("§")) {
			return new FancyMessage(paramString);
		}
		FancyMessage localFancyMessage = null;

		int i = 1;

		String[] arrayOfString1 = paramString.split("§");

		Color localObject1 = null;

		Color localObject2 = null;
		for (String str1 : arrayOfString1) {
			if (!str1.isEmpty()) {
				Color localColor = Color.getByChar(str1.charAt(0));

				String str2 = !str1.startsWith(" ") ? (str1.substring(1)) : str1;
				if ((str2.isEmpty()) && (localColor != null) && (!localColor.equals(Color.RESET))) {
					if (Utils.isFormat(localColor)) {
						localObject2 = localColor;
					} else {
						localObject1 = localColor;
					}
				} else if (localColor == null) {
					if (i != 0) {
						i = 0;
						localFancyMessage = new FancyMessage(str2);
					} else {
						localFancyMessage.then(str2);
					}
					if (localObject1 != null) {
						localFancyMessage.color(localObject1);
						localObject1 = null;
					}
					if (localObject2 != null) {
						localFancyMessage.style(new Color[] { localObject2 });
						localObject2 = null;
					}
				} else if (i != 0) {
					i = 0;

					localFancyMessage = new FancyMessage(str2);
					if (localObject1 != null) {
						localFancyMessage.color(localObject1);
						localObject1 = null;
					}
					if (localObject2 != null) {
						localFancyMessage.style(new Color[] { localObject2 });
						localObject2 = null;
					}
					if (!localColor.equals(Color.RESET)) {
						if (Utils.isFormat(localColor)) {
							localFancyMessage.style(new Color[] { localColor });
						} else {
							localFancyMessage.color(localColor);
						}
					}
				} else {
					localFancyMessage.then(str2);
					if (localObject1 != null) {
						localFancyMessage.color(localObject1);
						localObject1 = null;
					}
					if (localObject2 != null) {
						localFancyMessage.style(new Color[] { localObject2 });
						localObject2 = null;
					}
					if (!localColor.equals(Color.RESET)) {
						if (Utils.isFormat(localColor)) {
							localFancyMessage.style(new Color[] { localColor });
						} else {
							localFancyMessage.color(localColor);
						}
					}
				}
			}
		}
		if (localFancyMessage == null) {
			return new FancyMessage(paramString);
		}
		return localFancyMessage;
	}

	// TODO move to BukkitUtils
	public static FancyMessage applyUrlsNew(final FancyMessage m) {
		MessagePart part = new MessagePart(TextualComponent.rawText(""));
		part.clickActionName = "open_url";
		part.hoverActionName = "show_text";
		part.hoverActionData = new JsonString(
				"&7Click top open &9&n%s&7!"/*
											 * C.get( "open_url" )
											 */);
		part.clickActionData = "%s";
		Modifier<String, String> click = new Modifier<String, String>() {
			@Override
			public String modify(String object, String... params) {
				if (params == null || params.length == 0) {
					return object;
				}
				String site = params[0];
				site = Utils.strip(site);
				if (!site.startsWith("www.") && !site.startsWith("http://") && !site.startsWith("https://")) {
					site = "http://" + site;
				}
				return String.format(object, site);
			}
		};
		Modifier<JsonString, String> hover = new Modifier<JsonString, String>() {
			@Override
			public JsonString modify(JsonString object, String... params) {
				String preHover = object.getValue();
				String site = params[0];
				site = Utils.strip(site);
				if (!site.startsWith("www.") && !site.startsWith("http://") && !site.startsWith("https://")) {
					site = "http://" + site;
				}
				preHover = String.format(preHover, site);
				object.setValue(preHover);
				return object;
			}
		};
		return applyRegex(m, Utils.URL_PATTERN, part, click, hover);
	}

	public static String convertToJson(String paramString) {
		return makeFancy(paramString).toJSONString();
	}

	public static FancyMessage makeUrlsClickable(FancyMessage m) throws Exception {
		return applyUrlsNew(m);
	}

}
