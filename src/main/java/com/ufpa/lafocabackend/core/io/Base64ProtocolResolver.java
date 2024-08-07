package com.ufpa.lafocabackend.core.io;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64ProtocolResolver implements ProtocolResolver,
		ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(@NonNull ConfigurableApplicationContext configurableApplicationContext) {
		configurableApplicationContext.addProtocolResolver(this);
	}

	@Override
	public Resource resolve(@NonNull String location, @NonNull ResourceLoader resourceLoader) {
		if (location.startsWith("base64:")) {
			byte[] decodedResource = Base64.getDecoder().decode(location.substring(7));
			return new ByteArrayResource(decodedResource);
		}

		return null;
	}

}