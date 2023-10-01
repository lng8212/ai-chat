package com.longkd.chatgpt_openai.base.model;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String dirty = value.string().trim();
        try {
            T result = null;
            try {
                result = adapter.fromJson(dirty);
            } catch (Exception e) {
                JsonReader jsonReader = gson.newJsonReader(value.charStream());
                result = adapter.read(jsonReader);
            }
            return result;
        } finally {
            value.close();
        }
    }
}