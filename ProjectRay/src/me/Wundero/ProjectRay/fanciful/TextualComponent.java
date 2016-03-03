package me.Wundero.ProjectRay.fanciful;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.stream.JsonWriter;

public abstract class TextualComponent implements Cloneable {
	static {
		ConfigurationSerialization
				.registerClass(ArbitraryTextTypeComponent.class);
		ConfigurationSerialization
				.registerClass(ComplexTextTypeComponent.class);
	}

	public String toString() {
		return getReadableString();
	}

	public abstract String getKey();

	public abstract String getReadableString();

	public abstract TextualComponent clone();

	public abstract void writeJson(JsonWriter paramJsonWriter);

	static TextualComponent deserialize(Map<String, Object> paramMap) {
		if ((paramMap.containsKey("key")) && (paramMap.size() == 2)
				&& (paramMap.containsKey("value"))) {
			return ArbitraryTextTypeComponent.deserialize(paramMap);
		}
		if ((paramMap.size() >= 2) && (paramMap.containsKey("key"))
				&& (!paramMap.containsKey("value"))) {
			return ComplexTextTypeComponent.deserialize(paramMap);
		}
		return null;
	}

	static boolean isTextKey(String paramString) {
		return (paramString.equals("translate"))
				|| (paramString.equals("text"))
				|| (paramString.equals("score"))
				|| (paramString.equals("selector"));
	}

	public static final class ArbitraryTextTypeComponent extends
			TextualComponent implements ConfigurationSerializable {
		private String _key;
		private String _value;

		public ArbitraryTextTypeComponent(String paramString1,
				String paramString2) {
			setKey(paramString1);
			setValue(paramString2);
		}

		public String getKey() {
			return this._key;
		}

		public void setKey(String paramString) {
			Preconditions.checkArgument(
					(paramString != null) && (!paramString.isEmpty()),
					"The key must be specified.");
			this._key = paramString;
		}

		public String getValue() {
			return this._value;
		}

		public void setValue(String paramString) {
			Preconditions.checkArgument(paramString != null,
					"The value must be specified.");
			this._value = paramString;
		}

		public TextualComponent clone() {
			return new ArbitraryTextTypeComponent(getKey(), getValue());
		}

		public void writeJson(JsonWriter paramJsonWriter) {
			try {
				paramJsonWriter.name(getKey()).value(getValue());
			} catch (IOException e) {
			}
		}

		@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
		public Map<String, Object> serialize() {
			return new HashMap() {
			};
		}

		public static ArbitraryTextTypeComponent deserialize(
				Map<String, Object> paramMap) {
			if (paramMap == null || paramMap.get("key") == null
					|| paramMap.get("value") == null) {
				return null;
			}

			return new ArbitraryTextTypeComponent(paramMap.get("key")
					.toString(), paramMap.get("value").toString());
		}

		public String getReadableString() {
			return getValue();
		}
	}

	public static final class ComplexTextTypeComponent extends TextualComponent
			implements ConfigurationSerializable {
		private String _key;
		private Map<String, String> _value;

		public ComplexTextTypeComponent(String paramString,
				Map<String, String> paramMap) {
			setKey(paramString);
			setValue(paramMap);
		}

		public String getKey() {
			return this._key;
		}

		public void setKey(String paramString) {
			Preconditions.checkArgument(
					(paramString != null) && (!paramString.isEmpty()),
					"The key must be specified.");
			this._key = paramString;
		}

		public Map<String, String> getValue() {
			return this._value;
		}

		public void setValue(Map<String, String> paramMap) {
			Preconditions.checkArgument(paramMap != null,
					"The value must be specified.");
			this._value = paramMap;
		}

		public TextualComponent clone() {
			return new ComplexTextTypeComponent(getKey(), getValue());
		}

		@SuppressWarnings("rawtypes")
		public void writeJson(JsonWriter paramJsonWriter) {
			try {
				paramJsonWriter.name(getKey());
			} catch (IOException e) {
			}
			try {
				paramJsonWriter.beginObject();
			} catch (IOException e) {
			}
			for (Map.Entry localEntry : this._value.entrySet()) {
				try {
					paramJsonWriter.name((String) localEntry.getKey()).value(
							(String) localEntry.getValue());
				} catch (IOException e) {
				}
			}
			try {
				paramJsonWriter.endObject();
			} catch (IOException e) {
			}
		}

		@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
		public Map<String, Object> serialize() {
			return new HashMap() {
			};
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public static ComplexTextTypeComponent deserialize(
				Map<String, Object> paramMap) {
			String str = null;
			HashMap localHashMap = new HashMap();
			for (Map.Entry localEntry : paramMap.entrySet()) {
				if (((String) localEntry.getKey()).equals("key")) {
					str = (String) localEntry.getValue();
				} else if (((String) localEntry.getKey()).startsWith("value.")) {
					localHashMap.put(
							((String) localEntry.getKey()).substring(6),
							localEntry.getValue().toString());
				}
			}
			return new ComplexTextTypeComponent(str, localHashMap);
		}

		public String getReadableString() {
			return getKey();
		}
	}

	public static TextualComponent rawText(String paramString) {
		return new ArbitraryTextTypeComponent("text", paramString);
	}

	public static TextualComponent localizedText(String paramString) {
		return new ArbitraryTextTypeComponent("translate", paramString);
	}

	private static void throwUnsupportedSnapshot() {
		throw new UnsupportedOperationException(
				"This feature is only supported in snapshot releases.");
	}

	public static TextualComponent objectiveScore(String paramString) {
		return objectiveScore("*", paramString);
	}

	@SuppressWarnings("unchecked")
	public static TextualComponent objectiveScore(String paramString1,
			String paramString2) {
		throwUnsupportedSnapshot();

		Map<?, ?> m = ImmutableMap.builder().put("name", (String) paramString1)
				.put("objective", (String) paramString2).build();
		Map<String, String> m2 = (Map<String, String>) m;
		return new ComplexTextTypeComponent("score", m2);
	}

	public static TextualComponent selector(String paramString) {
		throwUnsupportedSnapshot();

		return new ArbitraryTextTypeComponent("selector", paramString);
	}
}
