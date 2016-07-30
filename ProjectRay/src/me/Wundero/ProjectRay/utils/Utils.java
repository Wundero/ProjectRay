package me.Wundero.ProjectRay.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.Wundero.ProjectRay.ProjectRay;
import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.config.InternalClickAction;
import me.Wundero.ProjectRay.config.InternalHoverAction;
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

	public static <T> List<T> sl(Iterable<T> objs) {
		List<T> l = sl();
		for (T t : objs) {
			l.add(t);
		}
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

	public static <T> List<T> sl() {
		// returns a new arraylist that copies itself for iteration (makes
		// unsynchronized iter blocks safe)
		return Collections.synchronizedList(new ArrayList<T>() {

			private static final long serialVersionUID = -9083645937669914699L;

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

	public static boolean inRange(Location<World> loc1, Location<World> loc2, double range) {
		// pythagorean range calc
		if (range < 0) {
			return true;
		}
		if (range == 0) {
			return false;
		}
		double x1 = loc1.getX(), x2 = loc2.getX(), y1 = loc1.getY(), y2 = loc2.getY(), z1 = loc1.getZ(),
				z2 = loc2.getZ();
		double l1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2) + Math.pow(z1, 2)),
				l2 = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2) + Math.pow(z2, 2));
		return Math.abs(l1 - l2) <= range;
	}

	public static TextTemplate parse(final String i, boolean allowColors) {
		String in = i;
		if (!allowColors) {
			in = strip(in);
		}
		in = in.replace("%n", "\n");
		if (!VAR_PATTERN.matcher(in).find()) {
			return TextTemplate.of(TextSerializers.FORMATTING_CODE.deserialize(in));
		}
		String[] textParts = in.split(VAR_PATTERN.pattern());
		Matcher matcher = VAR_PATTERN.matcher(in);
		TextTemplate out = TextTemplate.of(TextSerializers.FORMATTING_CODE.deserialize(textParts[0]));
		int x = 1;
		while (matcher.reset(in).find()) {
			String mg = matcher.group().substring(1);
			mg = mg.substring(0, mg.length() - 1);
			out = out.concat(TextTemplate.of(TextTemplate.arg(mg)));
			if (x < textParts.length) {
				out = out.concat(TextTemplate.of(TextSerializers.FORMATTING_CODE.deserialize(textParts[x])));
			}
			in = matcher.replaceFirst("");
			x++;
		}
		return out;
	}

	public static InternalClickAction<?> urlTemplate(TextTemplate t) {
		return new InternalClickAction.UrlTemplate(t);
	}

	public static InternalClickAction<?> suggestTemplate(TextTemplate t) {
		return new InternalClickAction.SuggestTemplate(t);
	}

	public static InternalClickAction<?> runTemplate(TextTemplate t) {
		return new InternalClickAction.RunTemplate(t);
	}

	public static InternalClickAction<?> executeCallback(Consumer<CommandSource> c) {
		return new InternalClickAction.ExecuteCallback(c);
	}

	public static InternalClickAction<?> changePage(int i) {
		return new InternalClickAction.ChangePage(i);
	}

	public static InternalClickAction<?> openUrl(URL u) {
		return new InternalClickAction.OpenUrl(u);
	}

	public static InternalClickAction<?> suggestCommand(String s) {
		return new InternalClickAction.SuggestCommand(s);
	}

	public static InternalClickAction<?> runCommand(String s) {
		return new InternalClickAction.RunCommand(s);
	}

	public static InternalHoverAction.ShowEntity showEntity(InternalHoverAction.ShowEntity.Ref entity) {
		return new InternalHoverAction.ShowEntity(entity);
	}

	public static InternalHoverAction.ShowEntity showEntity(UUID uuid, String name, @Nullable EntityType type) {
		return showEntity(new InternalHoverAction.ShowEntity.Ref(uuid, name, type));
	}

	public static InternalHoverAction.ShowEntity showEntity(UUID uuid, String name) {
		return showEntity(new InternalHoverAction.ShowEntity.Ref(uuid, name));
	}

	public static InternalHoverAction.ShowEntity showEntity(Entity entity, String name) {
		return showEntity(new InternalHoverAction.ShowEntity.Ref(entity, name));
	}

	public static InternalHoverAction<?> showItem(ItemStack i) {
		return new InternalHoverAction.ShowItem(i);
	}

	public static InternalHoverAction<?> showAchievement(Achievement a) {
		return new InternalHoverAction.ShowAchievement(a);
	}

	public static InternalHoverAction<?> showText(Text t) {
		return new InternalHoverAction.ShowText(t);
	}

	public static InternalHoverAction<?> showTemplate(TextTemplate t) {
		return new InternalHoverAction.ShowTemplate(t);
	}

	public static Text transIf(String s, User u) {

		if (u == null || u.hasPermission("ray.color")) {
			return TextSerializers.FORMATTING_CODE.deserialize(s);
		} else {
			return Text.of(s);
		}
	}

	public static ConfigurationNode load(Path config) {
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(config)
				.build();
		try {
			return loader.load(ProjectRay.updateSerializers(loader.getDefaultOptions()));
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

	public static <T> ArrayList<T> scramble(ArrayList<T> list) {
		List<T> out = sl();
		List<T> in = sl(list);
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

	public static final UUID pregenUUID = UUID.randomUUID();

	private static Map<UUID, Long> times = sm();

	public static boolean isURL(String input) {
		String s = input;
		while (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		return URL_PATTERN.matcher(strip(TextSerializers.FORMATTING_CODE.serialize(trans(s)))).matches();
	}

	public static boolean containsURL(String input) {
		return URL_PATTERN.matcher(strip(TextSerializers.FORMATTING_CODE.serialize(trans(input)))).find();
	}

	public static double convert(double val, PRTimeUnit from, PRTimeUnit to) {
		return to.convert(val, from);
	}

	public static String strip(String s) {
		return TextSerializers.FORMATTING_CODE.stripCodes(s);
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

	public static boolean badEnding(String url) {
		return url.endsWith(".") || url.endsWith("!") || url.endsWith("?") || url.endsWith("!") || url.endsWith(">")
				|| url.endsWith("<");
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

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Iterable<T> collection) {
		ArrayList<T> wrappedCollection = Lists.newArrayList(collection);
		return (T[]) wrappedCollection.toArray(new Object[wrappedCollection.size()]);
	}

	public static <T> List<String> toStringList(ArrayList<T> o, Optional<Method> toCall) {
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

	public static boolean isFormat(TextElement paramColor) {
		return paramColor instanceof TextStyle;
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

	public static Text trans(String s) {
		if (s == null) {
			return Text.of();
		}
		if (s.contains("\\u00a7")) {
			s = s.replace("\\u00a7", "&");
		}
		if (!s.contains("&")) {
			return Text.of(s);
		}
		return TextSerializers.FORMATTING_CODE.deserialize(s);
	}

	public static void printError(Exception e) {
		List<String> toPrint = sl(e.getMessage());
		for (StackTraceElement element : e.getStackTrace()) {
			toPrint.add("at " + element.toString());
		}
		for (String s : toPrint) {
			if (Ray.get() != null && Ray.get().getPlugin() != null && Ray.get().getPlugin().getLogger() != null) {
				Ray.get().getPlugin().getLogger().error(s);
			} else {
				e.printStackTrace();
			}
		}
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

	public static String concat(String sep, Iterable<String> a) {
		String o = "";
		String f = "";
		for (String s : a) {
			o += f + s;
			f = sep;
		}
		return o;
	}

	public static boolean call(Event e) {
		return !Sponge.getEventManager().post(e);
	}

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
			Ray.get().getPlugin().log("Warning: unsafe time for " + m);
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

}
