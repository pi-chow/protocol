package com.cetiti.iotp.protocol.client.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootConfiguration
@ComponentScan({"com.cetiti.iotp.protocol.client.mqtt"})
public class MqttClientApp {

	private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
	private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "mqtt-client";


	public static void main(String[] args) {
		SpringApplication.run(MqttClientApp.class, updateArguments(args));
	}

	private static String[] updateArguments(String[] args) {

		if (Arrays.stream(args).noneMatch(arg -> arg.startsWith(SPRING_CONFIG_NAME_KEY))){

			String[] modifiedArgs = new String[args.length + 1];
			System.arraycopy(args, 0, modifiedArgs, 0, args.length);
			modifiedArgs[args.length] = DEFAULT_SPRING_CONFIG_PARAM;
			return modifiedArgs;
		}

		return args;



	}


}
