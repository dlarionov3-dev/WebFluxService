package test.task.calculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.graalvm.polyglot.Value;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorExecutorService {

    private final ReentrantLock lock = new ReentrantLock();
    private final CalculatorRegistryService registry;

    public Object execute(String functionName, Integer argument) {
        Map<String, Value> functions = registry.getFunctions();
        Value fn = functions.get(functionName);

        if (fn == null) {
            throw new IllegalArgumentException("Функция '" + functionName + "' не найдена");
        }

        lock.lock();
        try {
            return fn.execute(argument).as(Float.class);
        } catch (Exception e) {
            String errorMsg = e.getClass().getSimpleName() + ": " + e.getMessage();
            log.error("JS-ошибка | функция={} | аргумент={} | ошибка={}",
                    functionName, argument, errorMsg, e);
            return "error: " + errorMsg;
        } finally {
            lock.unlock();
        }
    }

}
