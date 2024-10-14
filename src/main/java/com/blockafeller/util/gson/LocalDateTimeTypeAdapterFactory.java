package com.blockafeller.util.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;

public class LocalDateTimeTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {

        final Class<T> clazz = (Class<T>) typeToken.getRawType();

        if (LocalDateTime.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new LocalDateTimeAdapter(gson);
        }

        return null;
    }
}
