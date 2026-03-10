package test.task.calculator.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import test.task.calculator.service.CalculationService;

@RestController
@AllArgsConstructor
public class CalculatorController {

    private final CalculationService calculationService;

    @GetMapping(value = "/api/calculate", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> calculate(
            @RequestParam int count,
            @RequestParam(defaultValue = "false") boolean ordered) {

        return calculationService.calculate(count, ordered);
    }

}
