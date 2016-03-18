package me.Wundero.ProjectRay.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import me.Wundero.ProjectRay.fanciful.FancyMessage;
import me.Wundero.ProjectRay.fanciful.JsonString;
import me.Wundero.ProjectRay.fanciful.MessagePart;
import me.Wundero.ProjectRay.fanciful.NullMessagePart;
import me.Wundero.ProjectRay.fanciful.TextualComponent;

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

public class BukkitUtils {
	public static boolean isFormat(ChatColor paramChatColor) {
		return paramChatColor.isFormat();
	}
	// TODO move to BukkitUtils
	/**
	 * @param actionData
	 *            The data to replace the parts with the matching regex with.
	 *            Text will not be applied.
	 * 
	 */
	public static FancyMessage applyRegex(final FancyMessage message,
			final Pattern regex, final MessagePart actionData,
			final Modifier<String, String> clickDataModifier,
			final Modifier<JsonString, String> hoverDataModifier) {
		FancyMessage message1 = recompile(message);
		String m = "";
		int i = 0;
		HashMap<Integer, MessagePart> indices = Maps.newHashMap();
		HashMap<MessagePart, ArrayList<Integer>> reversedIndices = Maps
				.newHashMap();
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
				indexClone -= m.substring(0, m.indexOf(partOne.getText()))
						.length();
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
				allMessageParts = Utils.sub(allMessageParts.size() - 1,
						allMessageParts);
			} else {
				MessagePart partOne = Utils.getLast(allMessageParts);
				int indexClone = endex;
				indexClone -= m.substring(0, m.indexOf(partOne.getText()))
						.length();
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
				m1.clickActionData = clickDataModifier.modify(
						actionData.clickActionData, g);
				m1.clickActionName = actionData.clickActionName;
				m1.hoverActionData = hoverDataModifier.modify(
						(JsonString) actionData.hoverActionData, g);
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
	public static FancyMessage replaceRegex(final FancyMessage message,
			final Pattern regex, final FancyMessage replaceWith) {
		FancyMessage message1 = recompile(message);
		FancyMessage message2 = recompile(replaceWith);
		String m = "";
		int i = 0;
		HashMap<Integer, MessagePart> indices = Maps.newHashMap();
		HashMap<MessagePart, ArrayList<Integer>> reversedIndices = Maps
				.newHashMap();
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
				indexClone -= m.substring(0, m.indexOf(partOne.getText()))
						.length();
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
				allMessageParts = Utils.sub(allMessageParts.size() - 1,
						allMessageParts);
			} else {
				MessagePart partOne = Utils.getLast(allMessageParts);
				int indexClone = endex;
				indexClone -= m.substring(0, m.indexOf(partOne.getText()))
						.length();
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
						if (last != null && last.styles != null
								&& !last.styles.isEmpty()) {
							if (part.styles == null) {
								part.styles = last.styles;
							} else {
								for (ChatColor style : last.styles) {
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
				if (text.contains("" + ChatColor.COLOR_CHAR)) {
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
						if (last != null && last.styles != null
								&& !last.styles.isEmpty()) {
							if (part.styles == null) {
								part.styles = last.styles;
							} else {
								for (ChatColor style : last.styles) {
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
				if (text.contains("" + ChatColor.COLOR_CHAR)) {
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

		ChatColor localObject1 = null;

		ChatColor localObject2 = null;
		for (String str1 : arrayOfString1) {
			if (!str1.isEmpty()) {
				ChatColor localChatColor = ChatColor.getByChar(str1.charAt(0));

				String str2 = !str1.startsWith(" ") ? (str1.substring(1))
						: str1;
				if ((str2.isEmpty()) && (localChatColor != null)
						&& (!localChatColor.equals(ChatColor.RESET))) {
					if (Utils.isFormat(localChatColor)) {
						localObject2 = localChatColor;
					} else {
						localObject1 = localChatColor;
					}
				} else if (localChatColor == null) {
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
						localFancyMessage
								.style(new ChatColor[] { localObject2 });
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
						localFancyMessage
								.style(new ChatColor[] { localObject2 });
						localObject2 = null;
					}
					if (!localChatColor.equals(ChatColor.RESET)) {
						if (Utils.isFormat(localChatColor)) {
							localFancyMessage
									.style(new ChatColor[] { localChatColor });
						} else {
							localFancyMessage.color(localChatColor);
						}
					}
				} else {
					localFancyMessage.then(str2);
					if (localObject1 != null) {
						localFancyMessage.color(localObject1);
						localObject1 = null;
					}
					if (localObject2 != null) {
						localFancyMessage
								.style(new ChatColor[] { localObject2 });
						localObject2 = null;
					}
					if (!localChatColor.equals(ChatColor.RESET)) {
						if (Utils.isFormat(localChatColor)) {
							localFancyMessage
									.style(new ChatColor[] { localChatColor });
						} else {
							localFancyMessage.color(localChatColor);
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
		part.hoverActionData = new JsonString("&7Click top open &9&n%s&7!"/*
																		 * C.get(
																		 * "open_url"
																		 * )
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
				if (!site.startsWith("www.") && !site.startsWith("http://")
						&& !site.startsWith("https://")) {
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
				if (!site.startsWith("www.") && !site.startsWith("http://")
						&& !site.startsWith("https://")) {
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

	public static FancyMessage makeUrlsClickable(FancyMessage m)
			throws Exception {
		return applyUrlsNew(m);
	}

}
