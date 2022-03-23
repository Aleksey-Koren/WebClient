package com.example.calculator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@RestController
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
public class CalculatorWebClientController {

    private final WebClient webClient;

    @GetMapping("/multiplication")
    public CalculatorDto multiplication(@RequestParam(defaultValue = "0") Integer x, @RequestParam(defaultValue = "0") Integer y) {
        System.out.println("Multiplication: " + x + " x " + y);
        return calculate("/api/multiplication", x, y);
    }

    @GetMapping("/addition")
    public CalculatorDto produceMessage(@RequestParam(defaultValue = "0", name = "x") Integer x, @RequestParam(defaultValue = "0", name = "y") Integer y) {
        System.out.println("Addition: " + x + " x " + y);
        return calculate("/api/addition", x, y);
    }

    @GetMapping("/subtraction")
    public CalculatorDto subtraction(@RequestParam(defaultValue = "0") Integer x, @RequestParam(defaultValue = "0") Integer y) {
        System.out.println("Subtraction: " + x + " x " + y);
        return calculate("/api/subtractio", x, y);
    }

    @GetMapping("/division")
    public CalculatorDto division(@RequestParam(defaultValue = "0") Integer x, @RequestParam(defaultValue = "0") Integer y) {
        System.out.println("Division: " + x + " x " + y);
        return calculate("/api/division", x, y);
    }

    private CalculatorDto buildCalculatorDto(@RequestParam(defaultValue = "0") Integer x, @RequestParam(defaultValue = "0") Integer y) {
        return CalculatorDto.builder()
                .x(x)
                .y(y)
                .build();
    }

    private CalculatorDto calculate(String uri, Integer x, Integer y) {
        CalculatorDto dto = buildCalculatorDto(x, y);
        return webClient.patch()
                .uri(uri)
                .bodyValue(dto)
                .exchangeToMono(s -> {
                    if (s.statusCode().equals(HttpStatus.OK)) {
                        return s.bodyToMono(CalculatorDto.class);
                    } else {
                        return s.createException().flatMap(Mono::error);
                    }
                })
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(100)))
                .block();
    }
}