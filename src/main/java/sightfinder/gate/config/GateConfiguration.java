package sightfinder.gate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource("classpath:gate/gate.properties")
public class GateConfiguration {

    private static String GATE_HOME_PROPERTY_NAME = "gate.home";

    @Value("${gate.home}")
    private String gateHomePath;

    @PostConstruct
    public void initializeConfiguration() {
        System.setProperty(GATE_HOME_PROPERTY_NAME, gateHomePath);
    }
}