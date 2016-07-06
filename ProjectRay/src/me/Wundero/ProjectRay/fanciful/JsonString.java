package me.Wundero.ProjectRay.fanciful;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.stream.JsonWriter;

public final class JsonString implements JsonRepresentedObject {
	private String _value;

	public JsonString(String paramString) {
		this._value = paramString;
	}

	public void writeJson(JsonWriter paramJsonWriter) {
		try {
			paramJsonWriter.value(getValue());
		} catch (IOException e) {
		}
	}

	public String getValue() {
		return this._value;
	}

	public void setValue(String k) {
		this._value = k;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> serialize() {
		HashMap localHashMap = new HashMap();
		localHashMap.put("stringValue", this._value);
		return localHashMap;
	}

	public static JsonString deserialize(Map<String, Object> paramMap) {
		return new JsonString(paramMap.get("stringValue").toString());
	}

	public String toString() {
		return this._value;
	}
}
