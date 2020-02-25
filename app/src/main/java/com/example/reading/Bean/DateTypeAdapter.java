package com.example.reading.Bean;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

public class DateTypeAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {

    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek()== JsonToken.NULL){
            in.nextNull();
            return new Date();
        }
        long value=in.nextLong();
        return new Date(value);
    }
}
