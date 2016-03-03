package me.Wundero.ProjectRay.fanciful;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import me.Wundero.ProjectRay.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MessagePart implements JsonRepresentedObject,
		ConfigurationSerializable, Cloneable {

	public ChatColor color = null;
	public ArrayList<ChatColor> styles = Lists.newArrayList();
	public String clickActionName = null;
	public String clickActionData = null;
	public String hoverActionName = null;
	public JsonRepresentedObject hoverActionData = null;
	public TextualComponent text = null;
	static BiMap<ChatColor, String> stylesToNames;
	static boolean built = false;

	@Override
	public String toString() {
		return getText();
	}

	public void checkColor() {
		if (getText().startsWith(ChatColor.COLOR_CHAR + "")) {
			color = ChatColor.getByChar(getText().charAt(1));
			text = TextualComponent.rawText(getText().substring(2));
		}
	}

	public MessagePart(TextualComponent paramTextualComponent) {
		this.text = paramTextualComponent;
		if (!built) {
			build();
		}
	}

	public MessagePart() {
		this.text = null;
		if (!built) {
			build();
		}
	}

	boolean hasText() {
		return this.text != null;
	}

	public String getText(boolean i) {
		String out = "";
		if (color != null) {
			out += color + "";
		}
		for (ChatColor c : styles) {
			out += c + "";
		}
		return out + text.getReadableString();
	}

	void fix() {
		if (text == null) {
			text = TextualComponent.rawText("");
		}
	}

	public void applyCols() {
		if (hoverActionData != null) {
			String s = ((JsonString) hoverActionData).getValue();
			s = Utils.trans(s);
			hoverActionData = new JsonString(s);
		}

		if (clickActionData == null) {
			return;
		}

		clickActionData = Utils.strip(clickActionData);
	}

	public String getText() {
		String out = "";
		fix();
		return out + text.getReadableString();
	}

	public MessagePart clone() {
		MessagePart localMessagePart;
		try {
			localMessagePart = (MessagePart) super.clone();
		} catch (CloneNotSupportedException e) {
			try {
				localMessagePart = (MessagePart) super.clone();
			} catch (CloneNotSupportedException e1) {
				localMessagePart = new MessagePart();
				localMessagePart.text = text.clone();
			}
		}
		localMessagePart.styles = ((ArrayList) this.styles.clone());
		if ((this.hoverActionData instanceof JsonString)) {
			localMessagePart.hoverActionData = new JsonString(
					((JsonString) this.hoverActionData).getValue());
		} else if ((this.hoverActionData instanceof FancyMessage)) {
			try {
				localMessagePart.hoverActionData = ((FancyMessage) this.hoverActionData)
						.clone();
			} catch (CloneNotSupportedException e) {
			}
		}
		return localMessagePart;
	}

	public void writeJson(JsonWriter paramJsonWriter) {
		try {
			paramJsonWriter.beginObject();
			this.text.writeJson(paramJsonWriter);
			if (color != null) {
				paramJsonWriter.name("color").value(
						this.color.name().toLowerCase());
			}
			for (ChatColor localChatColor : this.styles) {
				paramJsonWriter
						.name((String) stylesToNames.get(localChatColor))
						.value(true);
			}
			if ((this.clickActionName != null)
					&& (this.clickActionData != null)) {
				paramJsonWriter.name("clickEvent").beginObject().name("action")
						.value(this.clickActionName).name("value")
						.value(this.clickActionData).endObject();
			}
			if ((this.hoverActionName != null)
					&& (this.hoverActionData != null)) {
				paramJsonWriter.name("hoverEvent").beginObject().name("action")
						.value(this.hoverActionName).name("value");
				this.hoverActionData.writeJson(paramJsonWriter);
				paramJsonWriter.endObject();
			}
			paramJsonWriter.endObject();
		} catch (IOException localIOException) {
			Bukkit.getLogger().log(Level.WARNING,
					"A problem occured during writing of JSON string",
					localIOException);
		}
	}

	public Map<String, Object> serialize() {
		HashMap localHashMap = new HashMap();
		localHashMap.put("text", this.text);
		localHashMap.put("styles", this.styles);
		if (color != null) {
			localHashMap.put("color", Character.valueOf(this.color.getChar()));
		} else {
			localHashMap.put("color", "");
		}
		localHashMap.put("hoverActionName", this.hoverActionName);
		localHashMap.put("hoverActionData", this.hoverActionData);
		localHashMap.put("clickActionName", this.clickActionName);
		localHashMap.put("clickActionData", this.clickActionData);
		return localHashMap;
	}

	public static MessagePart deserialize(Map<String, Object> paramMap) {
		MessagePart localMessagePart = new MessagePart(
				(TextualComponent) paramMap.get("text"));
		localMessagePart.styles = ((ArrayList) paramMap.get("styles"));
		localMessagePart.color = ChatColor.getByChar(paramMap.get("color")
				.toString());
		localMessagePart.hoverActionName = paramMap.get("hoverActionName")
				.toString();
		localMessagePart.hoverActionData = ((JsonRepresentedObject) paramMap
				.get("hoverActionData"));
		localMessagePart.clickActionName = paramMap.get("clickActionName")
				.toString();
		localMessagePart.clickActionData = paramMap.get("clickActionData")
				.toString();
		return localMessagePart;
	}

	static void build() {
		ImmutableBiMap.Builder localBuilder = ImmutableBiMap.builder();
		for (ChatColor localChatColor : ChatColor.values()) {
			if (localChatColor.isFormat()) {
				String str;
				switch (localChatColor) {
				case MAGIC:
					str = "obfuscated";
					break;
				case UNDERLINE:
					str = "underlined";
					break;
				case RESET:
				case STRIKETHROUGH:
				default:
					str = localChatColor.name().toLowerCase();
				}
				localBuilder.put(localChatColor, str);
			}
		}
		stylesToNames = localBuilder.build();

		ConfigurationSerialization.registerClass(MessagePart.class);
		built = true;
	}
}
