package test.task.calculator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private final CalculationStrategyFactory strategyFactory;

    public Flux<String> calculate(int count, boolean ordered) {
        var strategy =  strategyFactory.getStrategy(ordered);
        return strategy.calculate(count);
    }
}
