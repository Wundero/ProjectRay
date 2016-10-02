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

public class TextUtils {

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

	public static Text obfuscate(Text original, Double percentObfuscation, Double percentDiscoloration) {
		if (original == null) {
			return Text.of();
		}
		List<Text> children = original.getChildren();
		original = original.toBuilder().removeAll().build();
		Text.Builder out = Text.builder().color(original.getColor());
		char[] chars = original.toPlain().toCharArray();
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

	public static Text strip(String s, boolean text) {
		if (s == null) {
			return Text.of();
		}
		if (s.contains("\\u00a7")) {
			s = s.replace("\\u00a7", "&");
		}
		return Text.of(strip(s));
	}

	public static String strip(String s) {
		if (s == null) {
			return "";
		}
		if (s.contains("\\u00a7")) {
			s = s.replace("\\u00a7", "&");
		}
		return TextSerializers.FORMATTING_CODE.stripCodes(s);
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

	public static Text.Builder apply(Text.Builder onto, Text.Builder from) {
		return apply(onto, from.getClickAction(), from.getHoverAction(), from.getShiftClickAction());
	}

	public static Text.Builder apply(Text.Builder b, Optional<ClickAction<?>> c, Optional<HoverAction<?>> h,
			Optional<ShiftClickAction<?>> s) {
		c.ifPresent(x -> b.onClick(x));
		h.ifPresent(x -> b.onHover(x));
		s.ifPresent(x -> b.onShiftClick(x));
		return b;
	}

	public static List<Text> split(Text t, char c, boolean skip) {
		return split(t, String.valueOf(c), skip);
	}

	public static List<Text> split(Text t, String c, boolean skip) {
		if (!(t instanceof LiteralText)) {
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

	public static Text[] split(Text t, String c, boolean skip, boolean arr) {
		return split(t, c, skip).stream().toArray(in -> new Text[in]);
	}

	public static Text[] split(Text t, char c, boolean skip, boolean arr) {
		return split(t, c, skip).stream().toArray(in -> new Text[in]);
	}

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
		Text t = tx.get(0);
		if (!(t instanceof LiteralText)) {
			return t;
		}
		String c = getContent(t, true);
		if (finish > c.length()) {
			if (tx.isEmpty()) {
				throw new IllegalArgumentException("Finish must be less than or equal to the length of the content!");
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

	public static Text substring(Text t, int start, int finish) {
		if (start < 0) {
			throw new IllegalArgumentException("Cannot start below 0!");
		}
		if (finish < start) {
			throw new IllegalArgumentException("End must be after start!");
		}
		if (finish == start) {
			return Text.of();
		}
		if (!(t instanceof LiteralText)) {
			return t;
		}
		String c = getContent(t, true);
		if (finish > c.length()) {
			if (t.getChildren().isEmpty()) {
				throw new IllegalArgumentException("Finish must be less than or equal to the length of the content!");
			}
			LiteralText.Builder b = (Builder) LiteralText.builder();
			b.format(t.getFormat());
			t.getClickAction().ifPresent((a) -> b.onClick(a));
			t.getHoverAction().ifPresent((a) -> b.onHover(a));
			t.getShiftClickAction().ifPresent((a) -> b.onShiftClick(a));
			int offset = c.substring(start).length();
			b.content(c.substring(start));
			b.append(s(b.getChildren(), 0, finish - offset));
			return b.build();
		} else {
			Text.Builder b = Text.of(c.substring(start, finish)).toBuilder().format(t.getFormat());
			t.getClickAction().ifPresent((a) -> b.onClick(a));
			t.getHoverAction().ifPresent((a) -> b.onHover(a));
			t.getShiftClickAction().ifPresent((a) -> b.onShiftClick(a));
			return b.build();
		}
	}

	public static String getContent(Text t, boolean strict) {
		if (t instanceof LiteralText) {
			return ((LiteralText) t).getContent();
		} else {
			return strict ? "" : t.toPlain();
		}
	}

	public static Text vars(Text t, ParsableData data) {
		if (!(t instanceof LiteralText)) {
			return t;
		}
		return parseForVariables((LiteralText) t, data);
	}

	public static Text urls(Text t) {
		if (!(t instanceof LiteralText)) {
			return t;
		}
		return makeURLClickable((LiteralText) t);
	}

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

	public static Text urlsIf(Text t) {
		return urls(t);
	}

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

	public static int length(Text t) {
		if (!(t instanceof LiteralText)) {
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

}
