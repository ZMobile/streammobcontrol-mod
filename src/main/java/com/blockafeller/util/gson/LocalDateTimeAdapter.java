package com.blockafeller.util.gson;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final Gson gson;

    public LocalDateTimeAdapter(final Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(final JsonWriter writer, final LocalDateTime value) throws IOException {

        if (value == null) {
            writer.nullValue();
            return;
        }

        LocalDateTimeGsonResource localDateTimeGsonResource = new LocalDateTimeGsonResource(value.getYear(), value.getMonthValue(), value.getDayOfMonth(), value.getHour(), value.getMinute(), value.getSecond(), value.getNano());

        writer.value(gson.toJson(localDateTimeGsonResource));
    }

    @Override
public LocalDateTime read(final JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        if (reader.peek() == JsonToken.STRING) {
            String timestamp = reader.nextString();
            try {
                JsonReader newReader = new JsonReader(new StringReader(timestamp));
                return read(newReader); // Recursive call with new reader
            } catch (IllegalStateException e) {
                throw new JsonSyntaxException("Invalid JSON string: " + timestamp);
            }
        }

        LocalDateTimeGsonResource localDateTimeGsonResource = gson.fromJson(reader, LocalDateTimeGsonResource.class);

        return LocalDateTime.of(
                localDateTimeGsonResource.getDate().getYear(),
                localDateTimeGsonResource.getDate().getMonth(),
                localDateTimeGsonResource.getDate().getDay(),
                localDateTimeGsonResource.getTime().getHour(),
                localDateTimeGsonResource.getTime().getMinute(),
                localDateTimeGsonResource.getTime().getSecond(),
                localDateTimeGsonResource.getTime().getNano());
    }
}
