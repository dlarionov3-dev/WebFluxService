package test.task.calculator.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CalculationStrategyFactory {

    private final Map<String, CalculationStrategy> strategies;

    public CalculationStrategyFactory(Map<String, CalculationStrategy> strategies) {
        this.strategies = strategies;
    }

    public CalculationStrategy getStrategy(boolean ordered) {
        return strategies.get(ordered ? "orderedCalculationStrategy" : "unorderedCalculationStrategy");
    }
}
