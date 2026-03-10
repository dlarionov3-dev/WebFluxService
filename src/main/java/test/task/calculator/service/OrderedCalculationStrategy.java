package test.task.calculator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * dmitrijlarionov
 * Класс, реализующий вычисления функций упорядоченно
 */
@Service
@Qualifier("ordered")
@RequiredArgsConstructor
public class OrderedCalculationStrategy implements CalculationStrategy{

    @Value("${app.calculation.max-concurrency:64}")
    private int maxConcurrency;

    private final CalculatorRegistryService registry;
    private final CalculatorExecutorService executor;

    /**
     * Генерирует количество итераций, которые будут обработанны асинхронно. Обрабатывает итерации,
     * сохраняя порядок в выводе, используя буферизацию данных, которые необходимо придержать для порядка.
     * @param count Количество итераций
     * @return Pезультаты в виде потокового CSV-ответа
     */
    public Flux<String> calculate(int count) {
        // Определяем переменные, отображающие сколько раз была выполнена каждая функция
        var completedTimesFunction1 = new AtomicInteger(0);
        var completedTimesFunction2 = new AtomicInteger(0);

        return Flux.range(1, count)
                .flatMapSequential(iteration -> {

                    Mono<FunctionResult> resultFunction1 = executeWithCounter("function1", completedTimesFunction1);
                    Mono<FunctionResult> resultFunction2 = executeWithCounter("function2", completedTimesFunction2);

                    return Mono.zip(resultFunction1, resultFunction2)
                            .map(tupleResultFunctions -> {
                                FunctionResult r1 = tupleResultFunctions.getT1();
                                FunctionResult r2 = tupleResultFunctions.getT2();

                                int buffered1 = Math.max(0, completedTimesFunction1.get() - iteration);
                                int buffered2 = Math.max(0, completedTimesFunction2.get() - iteration);

                                return String.format("%d,%s,%d,%d,%s,%d,%d",
                                        iteration,
                                        r1.value(), r1.timeMs(), buffered1,
                                        r2.value(), r2.timeMs(), buffered2);
                            });
                }, maxConcurrency)
                .delayElements(Duration.ofMillis(registry.getInterval()))
                .map(line -> line + "\n");
    }

    private Mono<FunctionResult> executeWithCounter(String functionName, AtomicInteger counter) {
        return Mono.fromCallable(() -> {
            long start = System.nanoTime();

            var result = executor.execute(functionName, 10);

            long timeNs = System.nanoTime() - start;
            long timeMs = timeNs / 1_000_000;

            counter.incrementAndGet();
            return new FunctionResult(result, timeMs);
        }).subscribeOn(Schedulers.parallel());
    }
}
