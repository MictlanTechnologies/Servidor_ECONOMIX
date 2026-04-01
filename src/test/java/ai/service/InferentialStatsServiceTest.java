package ai.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InferentialStatsServiceTest {

    private final InferentialStatsService service = new InferentialStatsService();

    @Test
    void confidenceIntervalMeanShouldContainMean() {
        List<BigDecimal> sample = List.of(new BigDecimal("10"), new BigDecimal("12"), new BigDecimal("11"), new BigDecimal("13"),
                new BigDecimal("10"), new BigDecimal("14"), new BigDecimal("9"), new BigDecimal("12"), new BigDecimal("11"), new BigDecimal("10"));

        InferentialStatsService.CIResult ci = service.confidenceIntervalMean(sample, 0.95);
        assertTrue(ci.lower().compareTo(ci.mean()) <= 0);
        assertTrue(ci.upper().compareTo(ci.mean()) >= 0);
    }

    @Test
    void welchShouldProducePValue() {
        List<BigDecimal> a = List.of(new BigDecimal("10"), new BigDecimal("11"), new BigDecimal("12"), new BigDecimal("13"), new BigDecimal("11"));
        List<BigDecimal> b = List.of(new BigDecimal("7"), new BigDecimal("8"), new BigDecimal("7.5"), new BigDecimal("8.5"), new BigDecimal("8"));

        InferentialStatsService.WelchTestResult res = service.welchTTest(a, b, 0.05);
        assertTrue(res.p().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(res.p().compareTo(BigDecimal.ONE) <= 0);
    }
}
