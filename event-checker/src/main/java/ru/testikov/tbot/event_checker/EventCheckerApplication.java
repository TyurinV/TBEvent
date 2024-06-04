package ru.testikov.tbot.event_checker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EventCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventCheckerApplication.class, args);
	}

}
