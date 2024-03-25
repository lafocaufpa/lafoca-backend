package com.ufpa.lafocabackend.core.jackson;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * Para customizar a serialização de objetos Page
 * para reduzir as informações desnecessárias padrão de um Page
 *
 */
//@JsonComponent
public class PageJsonSerializer extends JsonSerializer<Page<?>> {
    @Override
    public void serialize(Page<?> objects, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeObject("");

    }
}
