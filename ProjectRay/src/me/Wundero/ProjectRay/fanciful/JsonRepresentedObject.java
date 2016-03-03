package me.Wundero.ProjectRay.fanciful;

import com.google.gson.stream.JsonWriter;

public abstract interface JsonRepresentedObject
{
  public abstract void writeJson(JsonWriter paramJsonWriter);
}
