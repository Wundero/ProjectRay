package me.Wundero.ProjectRay.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.ServerType;
import me.Wundero.ProjectRay.fanciful.FancyMessage;
import me.Wundero.ProjectRay.fanciful.JsonString;
import me.Wundero.ProjectRay.fanciful.MessagePart;
import me.Wundero.ProjectRay.framework.PlayerWrapper;
import me.Wundero.ProjectRay.framework.config.ConfigSection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

	public static boolean validateConfigSections(ConfigSection config,
			String... toValidate) {
		for (String s : toValidate) {
			if (!config.contains(s)) {
				return false;
			}
		}
		return true;
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

	public static <T> ArrayList<T> modifiedList(ArrayList<T> in,
			Modifier<T, ?> mod) {
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
		if (!strippedURL.startsWith("www.")
				&& !strippedURL.startsWith("http://")
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
		return ChatColor.stripColor(s);
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
		return url.endsWith(".") || url.endsWith("!") || url.endsWith("?")
				|| url.endsWith("!") || url.endsWith(">") || url.endsWith("<");
	}

	public synchronized static String getAnsi(ChatColor c) {// TODO spongify
		return replacements.get(c);
	}

	// TODO Sponge stuff
	private static final Map<ChatColor, String> replacements = Maps
			.newHashMap();
	static {
		replacements.put(ChatColor.BLACK,
				Ansi.ansi().reset().fg(Ansi.Color.BLACK).boldOff().toString());
		replacements.put(ChatColor.DARK_BLUE,
				Ansi.ansi().reset().fg(Ansi.Color.BLUE).boldOff().toString());
		replacements.put(ChatColor.DARK_GREEN,
				Ansi.ansi().reset().fg(Ansi.Color.GREEN).boldOff().toString());
		replacements.put(ChatColor.DARK_AQUA,
				Ansi.ansi().reset().fg(Ansi.Color.CYAN).boldOff().toString());
		replacements.put(ChatColor.DARK_RED,
				Ansi.ansi().reset().fg(Ansi.Color.RED).boldOff().toString());
		replacements
				.put(ChatColor.DARK_PURPLE,
						Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).boldOff()
								.toString());
		replacements.put(ChatColor.GOLD,
				Ansi.ansi().reset().fg(Ansi.Color.YELLOW).boldOff().toString());
		replacements.put(ChatColor.GRAY,
				Ansi.ansi().reset().fg(Ansi.Color.WHITE).boldOff().toString());
		replacements.put(ChatColor.DARK_GRAY,
				Ansi.ansi().reset().fg(Ansi.Color.BLACK).bold().toString());
		replacements.put(ChatColor.BLUE, Ansi.ansi().reset()
				.fg(Ansi.Color.BLUE).bold().toString());
		replacements.put(ChatColor.GREEN,
				Ansi.ansi().reset().fg(Ansi.Color.GREEN).bold().toString());
		replacements.put(ChatColor.AQUA, Ansi.ansi().reset()
				.fg(Ansi.Color.CYAN).bold().toString());
		replacements.put(ChatColor.RED, Ansi.ansi().reset().fg(Ansi.Color.RED)
				.bold().toString());
		replacements.put(ChatColor.LIGHT_PURPLE,
				Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).bold().toString());
		replacements.put(ChatColor.YELLOW,
				Ansi.ansi().reset().fg(Ansi.Color.YELLOW).bold().toString());
		replacements.put(ChatColor.WHITE,
				Ansi.ansi().reset().fg(Ansi.Color.WHITE).bold().toString());
		replacements.put(ChatColor.MAGIC,
				Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
		replacements.put(ChatColor.BOLD,
				Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
		replacements.put(ChatColor.STRIKETHROUGH,
				Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
		replacements.put(ChatColor.UNDERLINE,
				Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
		replacements.put(ChatColor.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC)
				.toString());
		replacements.put(ChatColor.RESET, Ansi.ansi().reset().toString());
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
		return (T[]) wrappedCollection.toArray(new Object[wrappedCollection
				.size()]);
	}

	public static <T> ArrayList<String> toStringList(ArrayList<T> o,
			Optional<Method> toCall) {
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
			ArrayList<Method> objMethods = Lists.newArrayList(o1.getClass()
					.getMethods());
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
	public static boolean isFormat(ChatColor paramChatColor) {
		return paramChatColor.isFormat();
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
			s2 = s2.replace("&", ChatColor.COLOR_CHAR + "");
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
	public static boolean call(Cancellable e) {
		Bukkit.getPluginManager().callEvent((Event) e);
		return !e.isCancelled();
	}

	public static UUID time() {
		UUID u = UUID.randomUUID();
		times.put(u, System.nanoTime());
		return u;
	}

	public static boolean containsProfanities(String s) {
		return compile(
				"("
						+ "(f[ua4][c]?[k]([e3]r|[a4])?)|(b[i!1]?tch([e3]s|y)?)|(s(lut|h[i!1]t[3e]?))"
						+ "|(n[i1!]g[g]?([a4]|[3e]r))|([4a][sz]{2})|(cunt)|(d[i!1][c]?k)|([kd][yi1!]ke)"
						+ "|(f[a43e]g([g]?[0oi1!]t)?)|(cum|j[i1!]zz)|(p[e3]n[i1!u]s)|(qu[3e]{2}r)"
						+ "|(pu[zs]{2}y)|(tw[4a]t)|(v[a4]g[i1!]n[a4])|(wh[o0]r[e3])"
						+ "|([ck][o0][ck]?[k])|([d][o][ou][c][h][e])" + ")",
				Pattern.CASE_INSENSITIVE).matcher(s).find();
	}

	public static void checkTime(long n, String m) {
		if (!isSafeTime(n)) {
			Ray.get().getPlugin()
					.log("Warning: unsafe time for " + m, Level.WARNING);
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
					s = s.substring(0, i) + ((char) (c.charValue() - 32))
							+ s.substring(i + 1);
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

	private static ArrayList<PlayerWrapper<?>> playersAlphabetical = Lists
			.newArrayList();

	private static ArrayList<PlayerWrapper<?>> sort(
			ArrayList<PlayerWrapper<?>> list) {
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

}
