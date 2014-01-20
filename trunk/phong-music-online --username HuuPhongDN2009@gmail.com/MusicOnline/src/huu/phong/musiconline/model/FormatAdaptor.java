package huu.phong.musiconline.model;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class FormatAdaptor implements JsonDeserializer<Map<Format, String>>{

	@Override
	public Map<Format, String> deserialize(JsonElement element, Type type,
			JsonDeserializationContext ctx) throws JsonParseException {
		Map<Format, String> directLinks = new HashMap<Format, String>();
		for (Entry<String, JsonElement> format : element.getAsJsonObject().entrySet()){
			String link = format.getValue().getAsString().trim();
			if (!link.equals("")) directLinks.put(Format.getFormat(format.getKey()), link);
		}
		return directLinks;
	}
}
