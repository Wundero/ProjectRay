package me.Wundero.ProjectRay.fanciful;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.Wundero.ProjectRay.framework.PlayerWrapper;
import me.Wundero.ProjectRay.framework.common.Color;
import me.Wundero.ProjectRay.utils.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

//Note - entirely abstracted from bukkit, not sure if it works

@SuppressWarnings({ "unchecked", "rawtypes" })
public class FancyMessage implements JsonRepresentedObject, Cloneable, Iterable<MessagePart> {
	// TODO ditch this in favour of custom system
	private List<MessagePart> messageParts;
	private String jsonString;
	private boolean dirty;
	private String lastColor;
	private String chatColor;

	public String getLastClick() {
		String lc = null;
		for (MessagePart p : messageParts) {
			if (p.clickActionData != null) {
				switch (p.clickActionName) {
				case "run_command":
					lc = "{exec}";
					break;
				case "open_url":
					lc = "{link}";
					break;
				default:
					lc = "";
					break;
				}
				lc += p.clickActionData;
			}
		}
		return lc;
	}

	// TODO move this to Bukkit side

	public String getLastHover() {
		String lc = null;
		for (MessagePart p : messageParts) {
			if (p.hoverActionData != null) {
				lc += ((JsonString) p.hoverActionData).getValue();
			}
		}
		return lc;
	}

	public void add(MessagePart p) {
		if (!p.hasText()) {
			p.text = TextualComponent.rawText("");
		}
		messageParts.add(p);
	}

	public ArrayList<MessagePart> getList() {
		return (ArrayList<MessagePart>) messageParts;
	}

	public void setList(List<MessagePart> l) {
		this.messageParts = l;
	}

	public void set(int ind, MessagePart p) {
		if (!p.hasText()) {
			p.text = TextualComponent.rawText("");
		}
		messageParts.set(ind, p);
	}

	public void send(PlayerWrapper<?>... p) {
		// TODO send
	}

	public boolean insert(MessagePart m, int index) {
		try {
			messageParts.add(index, m);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void send(Iterable<PlayerWrapper<?>> p) {
		for (PlayerWrapper<?> pl : p) {
			send(pl);
		}
	}

	public void fix() {
		for (MessagePart part : messageParts) {
			if (!part.hasText()) {
				part.text = TextualComponent.rawText("");
			}
		}
	}

	public void broadcast() {
		send();
	}

	public FancyMessage clone() throws CloneNotSupportedException {
		FancyMessage localFancyMessage = (FancyMessage) super.clone();
		localFancyMessage.messageParts = new ArrayList(this.messageParts.size());
		for (int i = 0; i < this.messageParts.size(); i++) {
			localFancyMessage.messageParts.add(i, ((MessagePart) this.messageParts.get(i)).clone());
		}
		localFancyMessage.dirty = false;
		localFancyMessage.jsonString = null;
		return localFancyMessage;
	}

	public FancyMessage insertIntoLast(String text) {
		if (text == null) {
			return this;
		}
		if (latest() == null || latest().text == null) {
			return this.text(text);
		}
		if (latest().color != null && latest().color != Color.RESET) {
			return this;
		}
		String copy = latest().text.getReadableString();
		copy = text + copy;
		if (latest().text instanceof TextualComponent.ArbitraryTextTypeComponent) {
			((TextualComponent.ArbitraryTextTypeComponent) latest().text).setValue(copy);
		}
		return this;
	}

	public FancyMessage(String paramString) {
		this(TextualComponent.rawText(paramString));
	}

	public FancyMessage concat(FancyMessage message) {
		if (message == null) {
			return this;
		}
		if (latest().text == null) {
			messageParts.remove(latest());
		}

		for (MessagePart m : message) {
			messageParts.add(m);
		}
		this.dirty = message.dirty;
		this.lastColor = message.lastColor;
		this.chatColor = message.chatColor;
		return this;
	}

	public FancyMessage(TextualComponent paramTextualComponent, boolean... irrelevant) {
		this.messageParts = new ArrayList();
		this.messageParts.add(new MessagePart(paramTextualComponent));
		this.jsonString = null;
		this.dirty = false;
		this.lastColor = "";
	}

	public String getLastColor() {
		if (this.lastColor == null) {
			return "";
		}
		return this.lastColor;
	}

	public void setLastColor(String paramString) {
		if (paramString != null) {
			this.lastColor = paramString;
		}
	}

	public FancyMessage() {
		this(null, true);
	}

	public FancyMessage text(String paramString) {
		MessagePart localMessagePart = latest();
		if (localMessagePart.hasText()) {
			throw new IllegalStateException("text for this message part is already set");
		}
		localMessagePart.text = TextualComponent.rawText(paramString);
		this.dirty = true;
		return this;
	}

	public FancyMessage text(TextualComponent paramTextualComponent) {
		MessagePart localMessagePart = latest();
		if (localMessagePart.hasText()) {
			throw new IllegalStateException("text for this message part is already set");
		}
		localMessagePart.text = paramTextualComponent;
		this.dirty = true;
		return this;
	}

	public FancyMessage color(Color paramChatColor) {
		if (!paramChatColor.isColor()) {
			throw new IllegalArgumentException(paramChatColor.name() + " is not a color");
		}
		latest().color = paramChatColor;
		this.dirty = true;
		return this;
	}

	public FancyMessage style(Color... paramVarArgs) {
		for (Color localChatColor : paramVarArgs) {
			if (!localChatColor.isFormat()) {
				throw new IllegalArgumentException(localChatColor.name() + " is not a style");
			}
		}
		latest().styles.addAll(Arrays.asList(paramVarArgs));
		this.dirty = true;
		return this;
	}

	public FancyMessage file(String paramString) {
		onClick("open_file", paramString);
		return this;
	}

	public FancyMessage link(String paramString) {
		onClick("open_url", paramString);
		return this;
	}

	public FancyMessage suggest(String paramString) {
		onClick("suggest_command", paramString);
		return this;
	}

	public FancyMessage command(String paramString) {
		onClick("run_command", paramString);
		return this;
	}

	// TODO items achievements and statistics as generals

	public String toString() {
		try {
			return this.clone().toJSONString();
		} catch (CloneNotSupportedException e) {
			return this.toJSONString();
		}
	}

	public FancyMessage tooltip(String paramString) {
		onHover("show_text", new JsonString(paramString));
		return this;
	}

	public FancyMessage tooltip(Iterable<String> paramIterable) {
		tooltip((String[]) ArrayWrapper.toArray(paramIterable, String.class));
		return this;
	}

	public FancyMessage tooltip(String... paramVarArgs) {
		StringBuilder localStringBuilder = new StringBuilder();
		for (int i = 0; i < paramVarArgs.length; i++) {
			localStringBuilder.append(paramVarArgs[i]);
			if (i != paramVarArgs.length - 1) {
				localStringBuilder.append('\n');
			}
		}
		tooltip(localStringBuilder.toString());
		return this;
	}

	public FancyMessage formattedTooltip(FancyMessage paramFancyMessage) {
		for (MessagePart localMessagePart : paramFancyMessage.messageParts) {
			if ((localMessagePart.clickActionData != null) && (localMessagePart.clickActionName != null)) {
				throw new IllegalArgumentException("The tooltip text cannot have click data.");
			}
			if ((localMessagePart.hoverActionData != null) && (localMessagePart.hoverActionName != null)) {
				throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
			}
		}
		onHover("show_text", paramFancyMessage);
		return this;
	}

	public FancyMessage formattedTooltip(FancyMessage... paramVarArgs) {
		if (paramVarArgs.length < 1) {
			onHover(null, null);
			return this;
		}
		FancyMessage localFancyMessage = new FancyMessage();
		localFancyMessage.messageParts.clear();
		for (int i = 0; i < paramVarArgs.length; i++) {
			try {
				for (MessagePart localMessagePart : paramVarArgs[i]) {
					if ((localMessagePart.clickActionData != null) && (localMessagePart.clickActionName != null)) {
						throw new IllegalArgumentException("The tooltip text cannot have click data.");
					}
					if ((localMessagePart.hoverActionData != null) && (localMessagePart.hoverActionName != null)) {
						throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
					}
					if (localMessagePart.hasText()) {
						localFancyMessage.messageParts.add(localMessagePart.clone());
					}
				}
				if (i != paramVarArgs.length - 1) {
					localFancyMessage.messageParts.add(new MessagePart(TextualComponent.rawText("\n")));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return this;
			}
		}
		return formattedTooltip(localFancyMessage.messageParts.isEmpty() ? null : localFancyMessage);
	}

	public FancyMessage formattedTooltip(Iterable<FancyMessage> paramIterable) {
		return formattedTooltip((FancyMessage[]) ArrayWrapper.toArray(paramIterable, FancyMessage.class));
	}

	public FancyMessage then(String paramString) {
		return then(TextualComponent.rawText(paramString));
	}

	public FancyMessage then(TextualComponent paramTextualComponent) {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}
		this.messageParts.add(new MessagePart(paramTextualComponent));
		this.dirty = true;
		return this;
	}

	public FancyMessage then() {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}
		this.messageParts.add(new MessagePart());
		this.dirty = true;
		return this;
	}

	public FancyMessage cont(String paramString) {
		return cont(TextualComponent.rawText(paramString));
	}

	public FancyMessage cont(TextualComponent paramTextualComponent) {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}

		MessagePart m;
		try {
			m = latest().clone();
		} catch (Exception e) {
			m = new MessagePart(paramTextualComponent);
		}
		m.text = paramTextualComponent;
		this.messageParts.add(m);
		this.dirty = true;
		return this;
	}

	public FancyMessage cont() {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}
		MessagePart m;
		try {
			m = latest().clone();
		} catch (Exception e) {
			m = new MessagePart();
		}
		m.text = null;
		this.messageParts.add(m);
		this.dirty = true;
		return this;
	}

	public void writeJson(JsonWriter paramJsonWriter) {
		if (this.messageParts.size() == 1) {
			latest().writeJson(paramJsonWriter);
		} else {
			try {
				paramJsonWriter.beginObject().name("text").value("").name("extra").beginArray();
			} catch (IOException e) {
			}
			for (MessagePart localMessagePart : this) {
				localMessagePart.writeJson(paramJsonWriter);
			}
			try {
				paramJsonWriter.endArray().endObject();
			} catch (IOException e) {
			}
		}
	}

	public String toJSONString() {
		if ((!this.dirty) && (this.jsonString != null)) {
			return this.jsonString;
		}
		this.setList(Utils.recompile(this).getList());
		StringWriter localStringWriter = new StringWriter();
		JsonWriter localJsonWriter = new JsonWriter(localStringWriter);
		try {
			writeJson(localJsonWriter);
			localJsonWriter.close();
		} catch (IOException localIOException) {
			throw new RuntimeException("invalid message");
		}
		this.jsonString = localStringWriter.toString();
		this.dirty = false;
		return this.jsonString;
	}

	public String toOldMessageFormat() {
		StringBuilder localStringBuilder = new StringBuilder();
		for (MessagePart localMessagePart : this) {
			localStringBuilder.append(localMessagePart.color == null ? "" : localMessagePart.color);
			for (Color localChatColor : localMessagePart.styles) {
				localStringBuilder.append(localChatColor);
			}
			localStringBuilder.append(localMessagePart.text);
		}
		return localStringBuilder.toString();
	}

	public MessagePart latest() {
		return (MessagePart) this.messageParts.get(this.messageParts.size() - 1);
	}

	private void onClick(String paramString1, String paramString2) {
		if (paramString2 == null) {
			return;
		}
		MessagePart localMessagePart = latest();
		localMessagePart.clickActionName = paramString1;
		localMessagePart.clickActionData = paramString2;
		this.dirty = true;
	}

	private void onHover(String paramString, JsonRepresentedObject paramJsonRepresentedObject) {
		MessagePart localMessagePart = latest();
		localMessagePart.hoverActionName = paramString;
		localMessagePart.hoverActionData = paramJsonRepresentedObject;
		this.dirty = true;
	}

	public Map<String, Object> serialize() {
		HashMap localHashMap = new HashMap();
		localHashMap.put("messageParts", this.messageParts);

		return localHashMap;
	}

	public static FancyMessage deserialize(Map<String, Object> paramMap) {
		FancyMessage localFancyMessage = new FancyMessage();
		localFancyMessage.messageParts = ((List) paramMap.get("messageParts"));
		localFancyMessage.jsonString = (paramMap.containsKey("JSON") ? paramMap.get("JSON").toString() : null);
		localFancyMessage.dirty = (!paramMap.containsKey("JSON"));
		return localFancyMessage;
	}

	public Iterator<MessagePart> iterator() {
		return this.messageParts.iterator();
	}

	private static JsonParser _stringParser = new JsonParser();

	public static FancyMessage deserialize(String paramString) {
		JsonObject localJsonObject1 = _stringParser.parse(paramString).getAsJsonObject();
		JsonArray localJsonArray = localJsonObject1.getAsJsonArray("extra");
		FancyMessage localFancyMessage = new FancyMessage();
		localFancyMessage.messageParts.clear();
		for (JsonElement localJsonElement : localJsonArray) {
			MessagePart localMessagePart = new MessagePart();
			JsonObject localJsonObject2 = localJsonElement.getAsJsonObject();
			for (Map.Entry localEntry1 : localJsonObject2.entrySet()) {
				Object localObject;
				if (TextualComponent.isTextKey((String) localEntry1.getKey())) {
					localObject = new HashMap();
					((Map) localObject).put("key", localEntry1.getKey());
					if (((JsonElement) localEntry1.getValue()).isJsonPrimitive()) {
						((Map) localObject).put("value", ((JsonElement) localEntry1.getValue()).getAsString());
					} else {
						for (Map.Entry localEntry2 : ((JsonElement) localEntry1.getValue()).getAsJsonObject()
								.entrySet()) {
							((Map) localObject).put("value." + (String) localEntry2.getKey(),
									((JsonElement) localEntry2.getValue()).getAsString());
						}
					}
					localMessagePart.text = TextualComponent.deserialize((Map) localObject);
				} else if (MessagePart.stylesToNames.inverse().containsKey(localEntry1.getKey())) {
					if (((JsonElement) localEntry1.getValue()).getAsBoolean()) {
						localMessagePart.styles
								.add((Color) MessagePart.stylesToNames.inverse().get(localEntry1.getKey()));
					}
				} else if (((String) localEntry1.getKey()).equals("color")) {
					localMessagePart.color = Color
							.valueOf(((JsonElement) localEntry1.getValue()).getAsString().toUpperCase());
				} else if (((String) localEntry1.getKey()).equals("clickEvent")) {
					localObject = ((JsonElement) localEntry1.getValue()).getAsJsonObject();
					localMessagePart.clickActionName = ((JsonObject) localObject).get("action").getAsString();
					localMessagePart.clickActionData = ((JsonObject) localObject).get("value").getAsString();
				} else if (((String) localEntry1.getKey()).equals("hoverEvent")) {
					localObject = ((JsonElement) localEntry1.getValue()).getAsJsonObject();
					localMessagePart.hoverActionName = ((JsonObject) localObject).get("action").getAsString();
					if (((JsonObject) localObject).get("value").isJsonPrimitive()) {
						localMessagePart.hoverActionData = new JsonString(
								((JsonObject) localObject).get("value").getAsString());
					} else {
						localMessagePart.hoverActionData = deserialize(
								((JsonObject) localObject).get("value").toString());
					}
				}
			}
			localFancyMessage.messageParts.add(localMessagePart);
		}
		return localFancyMessage;
	}

	public String getChatColor() {
		if (this.chatColor == null) {
			return "";
		}
		return this.chatColor;
	}

	public void setChatColor(String paramString) {
		this.chatColor = paramString;
	}

	public static FancyMessage builder() {
		return new FancyMessage();
	}
}
