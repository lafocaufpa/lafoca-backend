package com.ufpa.lafocabackend.core.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * Para customizar a serialização de objetos Page
 * para reduzir as informações desnecessárias padrão de um Page
 *
 */
@JsonComponent
public class PageJsonSerializer extends JsonSerializer<Page<?>> {
    

    @Override
    public void serialize(Page<?> objects, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeObjectField("content", objects.getContent());
        jsonGenerator.writeNumberField("size", objects.getSize());
        jsonGenerator.writeNumberField("totalElements", objects.getTotalElements());
        jsonGenerator.writeNumberField("totalPages", objects.getTotalPages());
        jsonGenerator.writeNumberField("currentPage", objects.getNumber());
        jsonGenerator.writeBooleanField("firstPage", objects.isFirst());
        jsonGenerator.writeBooleanField("lastPage", objects.isLast());

        jsonGenerator.writeEndObject();
    }
}
