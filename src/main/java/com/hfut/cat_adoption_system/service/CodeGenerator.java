package com.hfut.cat_adoption_system.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CodeGenerator {
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public String nextUserId() {
        return "U" + LocalDate.now().format(LONG_DATE) + next("U", 2);
    }

    public String nextUserIdAfter(int currentMax) {
        String prefix = "U" + LocalDate.now().format(LONG_DATE);
        AtomicInteger counter = counters.computeIfAbsent(prefix, ignored -> new AtomicInteger(0));
        counter.updateAndGet(value -> Math.max(value, currentMax));
        return prefix + String.format("%02d", counter.incrementAndGet());
    }

    public String next(String prefix) {
        return prefix + LocalDate.now().format(SHORT_DATE) + next(prefix, 3);
    }

    public String nextAfter(String prefix, int currentMax) {
        String key = prefix + LocalDate.now();
        AtomicInteger counter = counters.computeIfAbsent(key, ignored -> new AtomicInteger(0));
        counter.updateAndGet(value -> Math.max(value, currentMax));
        return prefix + LocalDate.now().format(SHORT_DATE) + next(prefix, 3);
    }

    private String next(String prefix, int width) {
        int value = counters.computeIfAbsent(prefix + LocalDate.now(), key -> new AtomicInteger(seed(width))).incrementAndGet();
        return String.format("%0" + width + "d", value);
    }

    private int seed(int width) {
        return 0;
    }
}
