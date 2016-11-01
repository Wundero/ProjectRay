package me.Wundero.Ray.utils;
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

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.config.InternalClickAction;
import me.Wundero.Ray.config.InternalHoverAction;
import me.Wundero.Ray.variables.ParsableData;

/**
 * A class containing methods that help parse Text objects.
 */
public class TextUtils {

	/**
	 * Wipe the color from a text object.
	 */
	public static Text strip(Text t) {
		Text.Builder b = t.toBuilder();
		List<Text> ch = b.getChildren();
		b.removeAll();
		b.format(TextFormat.NONE);
		for (Text f : ch) {
			b.append(strip(f));
		}
		return b.build();
	}

	/**
	 * If the player has permission, translate the string into a colored text,
	 * else a plain text with formatting codes. ALso parses URLS if they have
	 * permission.
	 */
	public static Text transIf(String s, User u) {
		LiteralText text = null;
		if (u == null || u.hasPermission("ray.color")) {
			text = (LiteralText) TextSerializers.FORMATTING_CODE.deserialize(s);
		} else {
			text = Text.of(s);
		}
		if (u == null || u.hasPermission("ray.url")) {
			return urls(text);
		} else {
			return text;
		}
	}

	/**
	 * Darken or replace characters from the original text based off of the
	 * percentages provided.
	 */
	public static Text obfuscate(Text original, Double percentObfuscation, Double percentDiscoloration) {
		if (original == null) {
			return Text.of();
		}
		List<Text> children = original.getChildren();
		original = original.toBuilder().removeAll().build();
		Text.Builder out = Text.builder().color(original.getColor());
		char[] chars = getContent(original, false).toCharArray();
		Integer obC = percentDiscoloration.intValue();
		Integer obS = percentObfuscation.intValue();
		List<Text> cs = Utils.sl();
		Random rng = new Random();
		TextColor co = original.getColor();
		for (Character c : chars) {
			if (rng.nextInt(100) < obS) {
				cs.add(Text.of(co, " "));
			} else {
				cs.add(Text.of(co, c));
			}
		}
		List<Text> toBuild = Utils.sl();
		for (Text t : cs) {
			if (rng.nextInt(100) < obC) {
				toBuild.add(t.toBuilder().color(TextColors.DARK_GRAY).build());
			} else {
				toBuild.add(t);
			}
		}
		out.append(toBuild);
		for (Text t : children) {
			out.append(obfuscate(t, percentObfuscation, percentDiscoloration));
		}
		return out.build();
	}

	/**
	 * Find all variables in a string and parse it into a text template.
	 */
	public static TextTemplate parse(final String i, boolean allowColors) {
		if (i == null) {
			return null;
		}
		String in = i;
		if (!allowColors) {
			in = strip(in);
		}
		in = in.replace("%n", "\n");
		if (!Utils.VAR_PATTERN.matcher(in).find()) {
			return TextTemplate.of(TextSerializers.FORMATTING_CODE.deserialize(in));
		}
		String[] textParts = in.split(Utils.VAR_PATTERN.pattern());
		if (textParts.length == 0) {
			return TextTemplate.of(TextTemplate.arg(in));
		}
		Matcher matcher = Utils.VAR_PATTERN.matcher(in);
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

	/**
	 * Create an internalclickaction with a url template.
	 */
	public static InternalClickAction<?> urlTemplate(TextTemplate t) {
		return new InternalClickAction.UrlTemplate(t);
	}

	/**
	 * Create an internalclickaction with a suggest template.
	 */
	public static InternalClickAction<?> suggestTemplate(TextTemplate t) {
		return new InternalClickAction.SuggestTemplate(t);
	}

	/**
	 * Create an internalclickaction with a run template.
	 */
	public static InternalClickAction<?> runTemplate(TextTemplate t) {
		return new InternalClickAction.RunTemplate(t);
	}

	/**
	 * Create an internalclickaction with a callback.
	 */
	public static InternalClickAction<?> executeCallback(Consumer<CommandSource> c) {
		return new InternalClickAction.ExecuteCallback(c);
	}

	/**
	 * Create an internalclickaction with a page change.
	 */
	public static InternalClickAction<?> changePage(int i) {
		return new InternalClickAction.ChangePage(i);
	}

	/**
	 * Create an internalclickaction with a url.
	 */
	public static InternalClickAction<?> openUrl(URL u) {
		return new InternalClickAction.OpenUrl(u);
	}

	/**
	 * Create an internalclickaction with a suggest command.
	 */
	public static InternalClickAction<?> suggestCommand(String s) {
		return new InternalClickAction.SuggestCommand(s);
	}

	/**
	 * Create an internalclickaction with a run command.
	 */
	public static InternalClickAction<?> runCommand(String s) {
		return new InternalClickAction.RunCommand(s);
	}

	/**
	 * Create an internalhoveraction with an entity
	 */
	public static InternalHoverAction.ShowEntity showEntity(InternalHoverAction.ShowEntity.Ref entity) {
		return new InternalHoverAction.ShowEntity(entity);
	}

	/**
	 * Create an internalhoveraction with an entity
	 */
	public static InternalHoverAction.ShowEntity showEntity(UUID uuid, String name, @Nullable EntityType type) {
		return showEntity(new InternalHoverAction.ShowEntity.Ref(uuid, name, type));
	}

	/**
	 * Create an internalhoveraction with an entity
	 */
	public static InternalHoverAction.ShowEntity showEntity(UUID uuid, String name) {
		return showEntity(new InternalHoverAction.ShowEntity.Ref(uuid, name));
	}

	/**
	 * Create an internalhoveraction with an entity
	 */
	public static InternalHoverAction.ShowEntity showEntity(Entity entity, String name) {
		return showEntity(new InternalHoverAction.ShowEntity.Ref(entity, name));
	}

	/**
	 * Create an internalhoveraction with an item
	 */
	public static InternalHoverAction<?> showItem(ItemStack i) {
		return new InternalHoverAction.ShowItem(i);
	}

	/**
	 * Create an internalhoveraction with an achievement
	 */
	public static InternalHoverAction<?> showAchievement(Achievement a) {
		return new InternalHoverAction.ShowAchievement(a);
	}

	/**
	 * Create an internalhoveraction with a text.
	 */
	public static InternalHoverAction<?> showText(Text t) {
		return new InternalHoverAction.ShowText(t);
	}

	/**
	 * Create an internalhoveraction with a template.
	 */
	public static InternalHoverAction<?> showTemplate(TextTemplate t) {
		return new InternalHoverAction.ShowTemplate(t);
	}

	/**
	 * Remove colors from a string. text boolean has no value check; just
	 * ensures method signatures are proper.
	 * 
	 * @return a text object which has been stripped of color.
	 */
	public static Text strip(String s, boolean text) {
		return Text.of(strip(s));
	}

	/**
	 * Return a String which has been stripped of formatting code colors.
	 */
	public static String strip(String s) {
		if (s == null) {
			return "";
		}
		if (s.contains("\\u00a7")) {
			s = s.replace("\\u00a7", "&");
		}
		return TextSerializers.FORMATTING_CODE.stripCodes(s);
	}

	/**
	 * Parse colors from a String into a text object.
	 */
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

	/**
	 * Replace regular expression in a LiteralText object's content.
	 * 
	 * Note: this method does NOT check between object nodes. So if there is a
	 * match between the original and one of it's children, it is not parsed.
	 * 
	 * @param original
	 *            The original text.
	 * @param matcher
	 *            The pattern to search for in the content.
	 * @param replacer
	 *            The function to call when a match is found in order to supply
	 *            a textual replacement.
	 * @param useClickHover
	 *            Whether or not to parse click and hover actions as well.
	 */
	public static LiteralText replaceRegex(LiteralText original, Pattern matcher,
			Function<String, Optional<LiteralText>> replacer, boolean useClickHover) {
		if (original == null) {
			return null;
		}
		List<Text> children = original.getChildren();
		LiteralText.Builder builder = original.toBuilder();
		builder.removeAll();
		String content = builder.getContent();
		if (matcher.matcher(content).find()) {
			Matcher m = matcher.matcher(content);
			if (m.matches()) {
				builder = replacer.apply(content).orElse(LiteralText.of("")).toBuilder();
			} else {
				String[] parts = content.split(matcher.pattern());
				if (parts.length == 0) {
					String c = content;
					builder = null;
					while ((m = m.reset(c)).find()) {
						String g = m.group();
						LiteralText t = replacer.apply(g).orElse(LiteralText.of(""));
						if (builder == null) {
							builder = t.toBuilder();
						} else {
							builder.append(t);
						}
						c = m.replaceFirst("");
					}
				} else {
					if (content.startsWith(parts[0])) {
						List<String> alt = Utils.alternate(parts, allmatches(content, matcher));
						LiteralText.Builder ob = builder;
						builder = (LiteralText.Builder) LiteralText.builder();
						List<String> pz = Utils.sl(parts);
						for (String s : alt) {
							if (pz.contains(s)) {
								LiteralText t = replaceRegex(bf(s, ob).build(), matcher, replacer, useClickHover);
								builder.append(t);
							} else {
								Optional<LiteralText> t = replacer.apply(s);
								builder.append(t.orElse(LiteralText.of("")));
							}
						}
					} else {
						List<String> alt = Utils.alternate(allmatches(content, matcher), parts);
						LiteralText.Builder ob = builder;
						builder = (LiteralText.Builder) LiteralText.builder();
						List<String> pz = Utils.sl(parts);
						for (String s : alt) {
							if (pz.contains(s)) {
								builder.append(replaceRegex(bf(s, ob).build(), matcher, replacer, useClickHover));
							} else {
								Optional<LiteralText> t = replacer.apply(s);
								builder.append(t.orElse(LiteralText.of("")));
							}
						}
					}
				}
			}
		} else {
			if (builder.getClickAction().isPresent()) {
				boolean r = builder.getClickAction().get() instanceof ClickAction.RunCommand;
				String click = replaceRegexAction(builder.getClickAction().get(), (s) -> {
					if (!matcher.matcher(s).find()) {
						return s;
					}
					String out = matcher.matcher(s).replaceAll("%s");
					String st = s;
					Matcher m = matcher.matcher(s);
					List<Object> b = Utils.sl();
					while ((m = m.reset(st)).find()) {
						String g = m.group();
						b.add(replacer.apply(g).orElse(LiteralText.builder("").build()).toPlain());
						st = m.replaceFirst("");
					}
					out = format(out, b);
					return out;
				}, "");
				if (!click.isEmpty()) {
					if (r) {
						builder.onClick(TextActions.runCommand(click));
					} else {
						builder.onClick(TextActions.suggestCommand(click));
					}
				}
			}
			if (builder.getHoverAction().isPresent()) {
				Text hover = replaceRegexAction(builder.getClickAction().get(), (s) -> {
					String a = TextSerializers.FORMATTING_CODE.serialize(s);
					if (!matcher.matcher(a).find()) {
						return s;
					}
					String out = matcher.matcher(a).replaceAll("%s");
					String st = a;
					Matcher m = matcher.matcher(a);
					List<Object> b = Utils.sl();
					while ((m = m.reset(st)).find()) {
						String g = m.group();
						b.add(TextSerializers.FORMATTING_CODE
								.serialize(replacer.apply(g).orElse(LiteralText.builder("").build())));
						st = m.replaceFirst("");
					}
					out = format(out, b);
					return TextSerializers.FORMATTING_CODE.deserialize(out);
				}, Text.of());
				if (!hover.isEmpty()) {
					builder.onHover(TextActions.showText(hover));
				}
			}
		}
		for (Text child : children) {
			Text o = child;
			if (child instanceof LiteralText) {
				o = replaceRegex((LiteralText) child, matcher, replacer, useClickHover);
			}
			builder.append(o);
		}
		return builder.build();
	}

	/**
	 * Apply the actions from one builder onto another.
	 */
	public static Text.Builder apply(Text.Builder onto, Text.Builder from) {
		return apply(onto, from.getClickAction(), from.getHoverAction(), from.getShiftClickAction());
	}

	/**
	 * Apply the actions provided onto a builder.
	 */
	public static Text.Builder apply(Text.Builder b, Optional<ClickAction<?>> c, Optional<HoverAction<?>> h,
			Optional<ShiftClickAction<?>> s) {
		c.ifPresent(x -> b.onClick(x));
		h.ifPresent(x -> b.onHover(x));
		s.ifPresent(x -> b.onShiftClick(x));
		return b;
	}

	/**
	 * Split the text object at a character reference.
	 */
	public static List<Text> split(Text t, char c, boolean skip) {
		return split(t, String.valueOf(c), skip);
	}

	/**
	 * Split the text object at a string reference.
	 */
	public static List<Text> split(Text t, String c, boolean skip) {
		if (!lit(t)) {
			return Utils.sl(t);
		}
		List<Text> out = Utils.sl();
		List<Text> children = t.getChildren();
		LiteralText.Builder text = ((LiteralText) t).toBuilder();
		String content = text.getContent();
		if (!content.contains(c)) {
			return Utils.sl(t);
		}
		if (content.equals(c)) {
			return skip ? Utils.sl() : Utils.sl(t);
		}
		Pattern p = Pattern.compile(c, Pattern.LITERAL);
		Matcher m = p.matcher(content);
		while ((m = m.reset(content)).find()) {
			int s = m.start();
			int e = m.end();
			Text.Builder b = Text.builder(content.substring(0, s)).format(text.getFormat());
			b = apply(b, text);
			out.add(b.build());
			if (!skip) {
				b = Text.builder(m.group()).format(text.getFormat());
				b = apply(b, text);
				out.add(b.build());
			}
			content = content.substring(0, e);
		}
		if (!content.isEmpty()) {
			Text.Builder b = Text.builder(content).format(text.getFormat());
			b = apply(b, text);
			out.add(b.build());
		}
		Text.Builder tx = out.get(out.size() - 1).toBuilder();
		out.remove(out.size() - 1);
		for (Text child : children) {
			List<Text> lt = split(child, c, skip);
			if (lt.isEmpty()) {
				out.add(tx.build());
				tx = null;
			} else if (lt.size() == 1) {
				tx = tx == null ? lt.get(0).toBuilder() : tx.append(lt.get(0));
			} else {
				out.add(tx == null ? lt.get(0) : tx.append(lt.get(0)).build());
				for (int i = 1; i < lt.size() - 1; i++) {
					out.add(lt.get(i));
				}
				tx = tx == null ? lt.get(lt.size() - 1).toBuilder() : lt.get(lt.size() - 1).toBuilder();
			}
		}
		if (tx != null) {
			out.add(tx.build());
		}
		return out;
	}

	/**
	 * Split the text at a string reference
	 */
	public static Text[] split(Text t, String c, boolean skip, boolean arr) {
		return split(t, c, skip).stream().toArray(in -> new Text[in]);
	}

	/**
	 * Split the text at a character reference
	 */
	public static Text[] split(Text t, char c, boolean skip, boolean arr) {
		return split(t, c + "", skip, arr);
	}

	/**
	 * Split the text at all newline characters.
	 */
	public static List<Text> newlines(Text t) {
		return split(t, '\n', false);
	}

	private static Text s(List<Text> tx, int start, int finish) {
		if (start < 0) {
			throw new IllegalArgumentException("Cannot start below 0!");
		}
		if (finish < start) {
			throw new IllegalArgumentException("End must be after start!");
		}
		if (finish == start) {
			return Text.of();
		}
		if (tx.isEmpty()) {
			throw new IllegalArgumentException("Text must be present!");
		}
		Text t = tx.get(0);
		if (!lit(t)) {
			return t;
		}
		String c = getContent(t, true);
		if (finish > c.length()) {
			if (tx.size() == 1) {
				return t;
			}
			LiteralText.Builder b = (Builder) LiteralText.builder();
			b.format(t.getFormat());
			t.getClickAction().ifPresent((a) -> b.onClick(a));
			t.getHoverAction().ifPresent((a) -> b.onHover(a));
			t.getShiftClickAction().ifPresent((a) -> b.onShiftClick(a));
			int offset = c.substring(start).length();
			b.content(c.substring(start));
			b.append(s(tx.stream().skip(1).collect(RayCollectors.rayList()), 0, finish - offset));
			return b.build();
		} else {
			Text.Builder b = Text.of(c.substring(start, finish)).toBuilder().format(t.getFormat());
			t.getClickAction().ifPresent((a) -> b.onClick(a));
			t.getHoverAction().ifPresent((a) -> b.onHover(a));
			t.getShiftClickAction().ifPresent((a) -> b.onShiftClick(a));
			return b.build();
		}
	}

	/**
	 * Split the text after a certain number of content characters has been
	 * passed. Good for preventing packet overflow of texts with more than
	 * 32,767 characters. [Though it should be noted that you should split at a
	 * number less than or equal to 15,000, to both save on performance and
	 * account for extra characters included in the object]
	 */
	public static Text[] splitAfterCharCount(Text t, int charCount, boolean repeat, boolean arr) {
		return splitAfterCharCount(t, charCount, repeat).stream().toArray(i -> new Text[i]);
	}

	/**
	 * Split the text after a certain number of content characters has been
	 * passed. Good for preventing packet overflow of texts with more than
	 * 32,767 characters. [Though it should be noted that you should split at a
	 * number less than or equal to 15,000, to both save on performance and
	 * account for extra characters included in the object]
	 */
	public static List<Text> splitAfterCharCount(Text t, int charCount, boolean repeat) {
		if (!lit(t)) {
			return Utils.sl(t);
		}
		List<Text> out = Utils.sl();
		int times = length(t) % charCount;
		if (!repeat) {
			times = 1;
		}
		for (int i = 0; i <= times; i++) {
			int start = i * charCount;
			int fin = (i + 1) * charCount;
			out.add(substring(t, start, fin));
		}
		return out;
	}

	/**
	 * Substring a text starting at an index. Follows String.substring(a,b).
	 */
	public static Text substring(Text t, int start) {
		return substring(t, start, length(t));
	}

	/**
	 * Substring a text from one index to another. Follows the same rules as
	 * String.substring(a,b);
	 */
	public static Text substring(Text t, int start, int finish) {
		if (!lit(t)) {
			return t;
		}
		if (start < 0) {
			throw new IllegalArgumentException("Cannot start below 0!");
		}
		if (finish < start) {
			throw new IllegalArgumentException("End must be after start!");
		}
		if (finish == start) {
			return Text.of();
		}
		String c = getContent(t, true);
		if (start > c.length()) {
			if (t.getChildren().isEmpty()) {
				throw new IllegalArgumentException("Cannot start after text ends!");
			} else {
				LiteralText.Builder b = (Builder) LiteralText.builder();
				int offset = start - c.length();
				b.append(s(t.getChildren(), 0, finish - offset));
				return b.build();
			}
		} else if (finish > c.length()) {
			if (t.getChildren().isEmpty()) {
				return t;
			}
			LiteralText.Builder b = (Builder) LiteralText.builder();
			b.format(t.getFormat());
			t.getClickAction().ifPresent((a) -> b.onClick(a));
			t.getHoverAction().ifPresent((a) -> b.onHover(a));
			t.getShiftClickAction().ifPresent((a) -> b.onShiftClick(a));
			int offset = c.substring(start).length();
			b.content(c.substring(start));
			b.append(s(t.getChildren(), 0, finish - offset));
			return b.build();
		} else {
			Text.Builder b = Text.of(c.substring(start, finish)).toBuilder().format(t.getFormat());
			t.getClickAction().ifPresent((a) -> b.onClick(a));
			t.getHoverAction().ifPresent((a) -> b.onHover(a));
			t.getShiftClickAction().ifPresent((a) -> b.onShiftClick(a));
			return b.build();
		}
	}

	/**
	 * Return the character at a certain point in the text.
	 */
	public static char charAt(Text t, int i) {
		return getContent(t, false).charAt(i);
	}

	/**
	 * Compare the formats of two texts.
	 */
	public static boolean formatsEqual(Text one, Text two) {
		return one.getFormat().equals(two.getFormat());
	}

	/**
	 * Compare the actions of two texts.
	 */
	public static boolean extrasEqual(Text one, Text two) {
		return one.getClickAction().equals(two.getClickAction()) && one.getHoverAction().equals(two.getHoverAction())
				&& one.getShiftClickAction().equals(two.getShiftClickAction());
	}

	/**
	 * Check to see if the string contents of two texts are equal.
	 */
	public static boolean contentsEqual(Text one, Text two, boolean ignoreCase) {
		String o = getContent(one, false);
		String t = getContent(two, false);
		return ignoreCase ? o.equalsIgnoreCase(t) : o.equals(t);
	}

	/**
	 * Checks to see if a text is an instance of a LiteralText
	 */
	public static boolean lit(Text t) {
		return t instanceof LiteralText;
	}

	/**
	 * Remove extra whitespace from a text.
	 */
	public static Text trim(Text t) {
		if (!lit(t)) {
			return t;
		}
		LiteralText.Builder b = ((LiteralText) t).toBuilder();
		return b.content(b.getContent().trim()).build();
	}

	/**
	 * Sets the string content of a text to lower case.
	 */
	public static Text toLowerCase(Text in, Locale locale) {
		if (!lit(in)) {
			return in;
		}
		LiteralText.Builder b = ((LiteralText) in).toBuilder();
		b.content(b.getContent().toLowerCase(locale));
		return b.build();
	}

	/**
	 * Sets the string content of a text to lower case.
	 */
	public static Text toLowerCase(Text in) {
		if (!lit(in)) {
			return in;
		}
		LiteralText.Builder b = ((LiteralText) in).toBuilder();
		b.content(b.getContent().toLowerCase());
		return b.build();
	}

	/**
	 * Sets the string content of a text to upper case.
	 */
	public static Text toUpperCase(Text in, Locale locale) {
		if (!lit(in)) {
			return in;
		}
		LiteralText.Builder b = ((LiteralText) in).toBuilder();
		b.content(b.getContent().toUpperCase(locale));
		return b.build();
	}

	/**
	 * Sets the string content of a text to upper case.
	 */
	public static Text toUpperCase(Text in) {
		if (!lit(in)) {
			return in;
		}
		LiteralText.Builder b = ((LiteralText) in).toBuilder();
		b.content(b.getContent().toUpperCase());
		return b.build();
	}

	/**
	 * Check to see if a text contains a char.
	 */
	public static boolean contains(Text one, char two) {
		return indexOf(one, two) > -1;
	}

	/**
	 * Check to see if a text contains a string.
	 */
	public static boolean contains(Text one, String two) {
		return indexOf(one, two) > -1;
	}

	/**
	 * Check to see if a text contains another text.
	 */
	public static boolean contains(Text one, Text two) {
		return indexOf(one, two) > -1;
	}

	/**
	 * Compare the string contents of two texts.
	 */
	public static int compare(Text one, Text two) {
		return getContent(one, false).compareTo(getContent(two, false));
	}

	/**
	 * Check to see if a text ends with another text.
	 */
	public static boolean endsWith(Text in, Text part) {
		return equals(substring(in, length(in) - length(part)), part, true, true, false);
	}

	/**
	 * Check to see if a text ends with a string.
	 */
	public static boolean endsWith(Text in, String part) {
		return getContent(in, false).endsWith(part);
	}

	/**
	 * Check to see if a text starts with another text.
	 */
	public static boolean startsWith(Text in, Text part) {
		return equals(substring(in, 0, length(part)), part, true, true, false);
	}

	/**
	 * Check to see if a text starts with a string.
	 */
	public static boolean startsWith(Text in, String part) {
		return getContent(in, false).startsWith(part);
	}

	/**
	 * Compare two texts.
	 */
	public static boolean equals(Text one, Text two, boolean clickHover, boolean formats, boolean ignoreCase) {
		if (!contentsEqual(one, two, ignoreCase)) {
			return false;
		}
		if (clickHover && !extrasEqual(one, two)) {
			return false;
		}
		if (formats && !formatsEqual(one, two)) {
			return false;
		}
		return true;
	}

	/**
	 * Find the index of the val in the text.
	 */
	public static int indexOf(Text in, String val) {
		return indexOf(in, val, 0);
	}

	/**
	 * Find the index of the val in the text after an offset.
	 */
	public static int indexOf(Text in, String val, int offset) {
		if (!lit(in)) {
			return -1;
		}
		return ((LiteralText) in).getContent().indexOf(val, offset);
	}

	/**
	 * Find the index of the val in the text. All of the following must match
	 * for the index to be chosen: Content, format and actions.
	 */
	public static int indexOf(Text in, Text val) {
		return indexOf(in, val, 0);
	}

	/**
	 * Find the index of the val in the text after an offset. All of the
	 * following must match for the index to be chosen: Content, format and
	 * actions.
	 */
	public static int indexOf(Text in, Text val, int offset) {
		if (!lit(in) && !lit(val)) {
			return in.equals(val) ? 0 : -1;
		} else if (!lit(in) || !lit(val)) {
			return -1;
		}
		LiteralText t1 = (LiteralText) in;
		LiteralText t2 = (LiteralText) val;
		int c = t1.getContent().indexOf(t2.getContent(), offset);
		return extrasEqual(t1, t2) && formatsEqual(t1, t2) ? c : -1;
	}

	/**
	 * Return the index of the val in the text.
	 */
	public static int indexOf(Text in, char val) {
		return indexOf(in, val, 0);
	}

	/**
	 * Return the index of the val in the text after an offset.
	 */
	public static int indexOf(Text in, char val, int offset) {
		if (!lit(in)) {
			return -1;
		}
		return ((LiteralText) in).getContent().indexOf(val, offset);
	}

	/**
	 * Capitalize the first letter of the text object.
	 */
	public static Text capitalize(Text t) {
		if (!lit(t)) {
			return t;
		}
		LiteralText t1 = (LiteralText) t;
		String content = t1.getContent();
		String capped = Utils.capitalize(content);
		if (capped.equals(content)) {
			List<Text> txs = t1.getChildren();
			int i = 0;
			boolean found = false;
			for (; i < txs.size(); i++) {
				if (!capequalsnorm(txs.get(i))) {
					found = true;
					break;
				}
			}
			List<Text> ot = Utils.sl();
			if (found) {
				for (int x = 0; x < txs.size(); x++) {
					if (x == i) {
						ot.add(capitalize(txs.get(x)));
					} else {
						ot.add(txs.get(x));
					}
				}
				return t1.toBuilder().removeAll().append(ot).build();
			} else {
				return t;
			}
		}
		return t1.toBuilder().content(capped).build();
	}

	private static boolean capequalsnorm(Text t) {
		if (!lit(t)) {
			return true;
		}
		LiteralText t1 = (LiteralText) t;
		String content = t1.getContent();
		String capped = Utils.capitalize(content);
		if (t1.getChildren().isEmpty()) {
			return content.equals(capped);
		} else {
			if (content.equals(capped)) {
				return true;
			}
			boolean b = false;
			for (Text c : t1.getChildren()) {
				b = b || capequalsnorm(c);
			}
			return b;
		}
	}

	/**
	 * Return the string content of a text. If it's a LiteralText, just
	 * content(). If not, it will return toPlain().
	 */
	public static String getContent(Text t, boolean strict) {
		if (lit(t)) {
			return ((LiteralText) t).getContent();
		} else {
			if (strict) {
				throw new IllegalArgumentException("Unsupported text " + t);
			} else {
				return t.toPlain();
			}
		}
	}

	/**
	 * Parse a text for variables, and replace them.
	 */
	public static Text vars(Text t, ParsableData data) {
		if (!(t instanceof LiteralText)) {
			return t;
		}
		return parseForVariables((LiteralText) t, data);
	}

	/**
	 * Parse a text for urls and replace them.
	 */
	public static Text urls(Text t) {
		if (!(t instanceof LiteralText)) {
			return t;
		}
		return makeURLClickable((LiteralText) t);
	}

	/**
	 * Clear urls from a text.
	 */
	public static Text noUrls(Text t) {
		if (!(t instanceof LiteralText)) {
			return t;
		}
		return makeURLUnclickable((LiteralText) t);
	}

	private static LiteralText makeURLUnclickable(LiteralText text) {
		return TextUtils.replaceRegex(text, Utils.URL_PATTERN, (match) -> {
			return Optional.of(Text.of(match.replace(".", " ")));
		}, true);
	}

	/**
	 * Parse a text for urls and replace them.
	 */
	public static Text urlsIf(Text t) {
		return urls(t);
	}

	/**
	 * Parse a text for urls and replace them.
	 */
	public static LiteralText makeURLClickable(LiteralText text) {
		return TextUtils.replaceRegex(text, Utils.URL_PATTERN, (match) -> {
			if (!Utils.toUrlSafe(match).isPresent()) {
				return Optional.of(Text.of(match));
			}
			return Optional.of(Text.builder(match).color(TextColors.BLUE).style(TextStyles.ITALIC, TextStyles.UNDERLINE)
					.onClick(TextActions.openUrl(Utils.toUrlSafe(match).get()))
					.onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click here to go to " + match + "!")))
					.build());
		}, true);
	}

	/**
	 * Parse a text for variables and replace them.
	 */
	public static LiteralText parseForVariables(LiteralText text, ParsableData data) {
		return TextUtils.replaceRegex(text, Utils.VAR_PATTERN, (match) -> {
			String proper = match.replace("{", "").replace("}", "");
			Object out = Ray.get().getVariables().get(proper, data, Optional.empty(), Optional.empty());
			if (data.getKnown().isPresent() && data.getKnown().get().containsKey(proper)) {
				out = data.getKnown().get().get(proper);
			}
			if (out == null) {
				return Optional.empty();
			} else if (out instanceof LiteralText) {
				return Optional.of((LiteralText) out);
			} else if (out instanceof Text) {
				return Optional.empty();
			} else {
				return Optional.of(LiteralText.builder(out.toString()).build());
			}
		}, true);
	}

	private static <T> T replaceRegexAction(TextAction<?> action, Function<T, T> replacer, T def) {
		if (def.getClass().isInstance(action.getResult())) {
			@SuppressWarnings("unchecked")
			T result = (T) action.getResult();
			return replacer.apply(result);
		} else {
			return def;
		}
	}

	private static String format(String a, List<Object> b) {
		return String.format(a, b.toArray(new Object[b.size()]));
	}

	private static LiteralText.Builder bf(String p, LiteralText.Builder builder) {
		LiteralText.Builder tb = LiteralText.builder(p).format(builder.getFormat());
		builder.getClickAction().ifPresent(act -> tb.onClick(act));
		builder.getHoverAction().ifPresent(act -> tb.onHover(act));
		return tb;
	}

	private static String[] allmatches(final String a, final Pattern b) {
		String f = a;
		Matcher m = b.matcher(f);
		List<String> o = Utils.sl();
		while ((m = m.reset(f)).find()) {
			o.add(m.group());
			f = m.replaceFirst("");
		}
		return o.toArray(new String[o.size()]);
	}

	/**
	 * Return the length of the content of the text object.
	 */
	public static int length(Text t) {
		if (!lit(t)) {
			return 0;
		}
		List<Text> children = t.getChildren();
		LiteralText lt = (LiteralText) t;
		int l = lt.getContent().length();
		for (Text f : children) {
			l += length(f);
		}
		return l;
	}

	/**
	 * Parse text colors from a string.
	 */
	public static TextColor colorFrom(String s) {
		if (s == null || s.isEmpty()) {
			return TextColors.NONE;
		}
		TextColor c = fromName(s);
		if (c == TextColors.NONE && s.length() < 3) {
			c = fromString(s);
		}
		return c;
	}

	/**
	 * Parse a text color from a string with formatting codes.
	 */
	public static TextColor fromString(String s) {
		return TextSerializers.FORMATTING_CODE.deserialize(s).getColor();
	}

	/**
	 * Parse a text color from a formatting code.
	 */
	public static TextColor fromChar(char c) {
		return fromString("&" + c + "");
	}

	/**
	 * Parse a text color from a name.
	 */
	public static TextColor fromName(String color) {
		switch (color.toLowerCase().trim()) {
		case "aqua":
			return TextColors.AQUA;
		case "black":
			return TextColors.BLACK;
		case "blue":
			return TextColors.BLUE;
		case "dark_aqua":
		case "dark aqua":
		case "dark cyan":
		case "dark_cyan":
			return TextColors.DARK_AQUA;
		case "dark_blue":
		case "navy":
		case "dark blue":
			return TextColors.DARK_BLUE;
		case "dark_gray":
		case "dark gray":
			return TextColors.DARK_GRAY;
		case "dark_green":
		case "dark green":
			return TextColors.DARK_GREEN;
		case "dark_purple":
		case "dark purple":
			return TextColors.DARK_PURPLE;
		case "dark red":
		case "dark_red":
			return TextColors.DARK_RED;
		case "gold":
		case "orange":
			return TextColors.GOLD;
		case "gray":
			return TextColors.GRAY;
		case "green":
		case "lime":
			return TextColors.GREEN;
		case "purple":
		case "light purple":
		case "light_purple":
			return TextColors.LIGHT_PURPLE;
		case "red":
			return TextColors.RED;
		case "reset":
			return TextColors.RESET;
		case "white":
			return TextColors.WHITE;
		case "yellow":
			return TextColors.YELLOW;
		default:
			return TextColors.NONE;
		}
	}

	/**
	 * Get a random text color.
	 */
	public static TextColor randomColor() {
		int hex = new Random().nextInt(16);
		String h = Integer.toHexString(hex);
		return TextSerializers.FORMATTING_CODE.deserialize("&" + h).getColor();
	}

}
