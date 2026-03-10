package test.task.calculator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * dmitrijlarionov
 * Класс, реализующий вычисления функций неупорядоченно
 */
@Service
@Qualifier("unordered")
@RequiredArgsConstructor
public class UnorderedCalculationStrategy implements CalculationStrategy{

    @Value("${app.calculation.max-concurrency:64}")
    private int maxConcurrency;

    private final CalculatorRegistryService registry;
    private final CalculatorExecutorService executor;

    public Flux<String> calculate(int count) {
        return Flux.fromIterable(registry.getFunctions().keySet())
                .flatMap(name -> runFunctionCountTimes(name, count), maxConcurrency)
                .map(line -> line + "\n");
    }

    private Flux<String> runFunctionCountTimes(String functionName, int count) {
        return Flux.range(1, count)
                .flatMap(iteration -> Mono.fromCallable(() -> {
                    long start = System.nanoTime();

                    var result = executor.execute(functionName, 10);

                    long timeNs = System.nanoTime() - start;
                    long timeMs = timeNs / 1_000_000;

                    return iteration + "," + functionName + "," + result + "," + timeMs;
                }), maxConcurrency)
                .subscribeOn(Schedulers.parallel());
    }

}
