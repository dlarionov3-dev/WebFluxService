package test.task.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import test.task.calculator.config.CalculatorDynamicConfig;

@SpringBootApplication
@EnableConfigurationProperties(CalculatorDynamicConfig.class)
public class CalculatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }

}