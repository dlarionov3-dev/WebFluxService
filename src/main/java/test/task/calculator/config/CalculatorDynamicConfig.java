package test.task.calculator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * dmitrijlarionov
 * Иммутабельный класс отвечает за получение пути к файлу json, содержащую пользовательские функции
 * @param configFile инициализируется из application.yaml
 */
@ConfigurationProperties(prefix = "dynamic")
public record CalculatorDynamicConfig(String configFile) {

    public CalculatorDynamicConfig {
        if (configFile == null) {
            configFile = "/config/app/json/user-functions.json";
        }
    }

}
