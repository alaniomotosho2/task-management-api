package org.niyo.shared;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Provider
public class LocalDateTimeConverterProvider implements ParamConverterProvider {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(LocalDateTime.class)) {
            return new ParamConverter<T>() {
                @Override
                public T fromString(String value) {
                    return rawType.cast(LocalDateTime.parse(value, formatter));
                }

                @Override
                public String toString(T value) {
                    return ((LocalDateTime) value).format(formatter);
                }
            };
        }
        return null;
    }
}