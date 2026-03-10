package test.task.calculator.service;

import reactor.core.publisher.Flux;


public interface CalculationStrategy {
    Flux<String> calculate(int count);
}
