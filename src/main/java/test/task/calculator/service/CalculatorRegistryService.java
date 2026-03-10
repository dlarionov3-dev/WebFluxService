package test.task.calculator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import test.task.calculator.config.CalculatorDynamicConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CalculatorRegistryService {

    private static final String PERMITTED_LANGUAGES = "js";
    public static final String ENGINE_WARN_INTERPRETER_ONLY = "engine.WarnInterpreterOnly";

    private final Context jsContext;
    private final CalculatorDynamicConfig props;
    private final ObjectMapper objectMapper;
    @Getter
    private Integer interval;
    private final Map<String, Value> functions = new ConcurrentHashMap<>();

    public CalculatorRegistryService(CalculatorDynamicConfig props, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.props = props;
        this.jsContext = Context
                .newBuilder(PERMITTED_LANGUAGES)
                //TODO Убирает предупреждение про JVMCI. Есть вариант перейти GraalVM JDK 25, тогда функции js будут работать еще быстрее и без варнинга в логах
                .option(ENGINE_WARN_INTERPRETER_ONLY, String.valueOf(Boolean.FALSE))
                .build();
    }

    @PostConstruct
    private void loadFromFile() {
        try {
            Resource resource = new FileSystemResource(props.configFile());

            Map<String, Object> data = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<>() {}
            );

            data.forEach((key, value) -> {
                if (value instanceof String code && code.contains("function")) {
                    Value fn = jsContext.eval(PERMITTED_LANGUAGES, "(" + code + ")");
                    functions.put(key, fn);
                    log.info("Загружена функция: " + key);
                } else if ("interval".equals(key) && value instanceof Number n) {
                    this.interval = n.intValue();
                    log.info("Загружен интервал: " + value);
                }
            });
            log.info("Все функции с интервалом успешно загружены из json");
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить функции с внешнего файла " + props.configFile(), e);
        }
    }

    /*
    Геттер возвращает имутабельную мапу
     */
    public Map<String, Value> getFunctions() {
        return Map.copyOf(functions);
    }

}
