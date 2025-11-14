package com.example.jeopardy.service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

/**
 * Lightweight natural-language matching utility that mimics an
 * "AI" style evaluation by combining several fuzzy techniques to
 * judge whether a free-form answer matches the stored solution.
 */
public class AiAnswerEvaluator {

    private static final double COSINE_THRESHOLD = 0.80;
    private static final double JARO_THRESHOLD = 0.90;

    private final CosineSimilarity cosineSimilarity = new CosineSimilarity();
    private final JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();

    public boolean isCorrect(String expected, String provided) {
        if (expected == null || provided == null) {
            return false;
        }

        String cleanedExpected = expected.trim();
        String cleanedProvided = provided.trim();
        if (cleanedExpected.isEmpty() || cleanedProvided.isEmpty()) {
            return false;
        }

        String normalizedExpected = normalizeStrict(cleanedExpected);
        String normalizedProvided = normalizeStrict(cleanedProvided);

        if (normalizedExpected.equals(normalizedProvided)
                || normalizedExpected.contains(normalizedProvided)
                || normalizedProvided.contains(normalizedExpected)) {
            return true;
        }

        double jaroScore = jaroWinkler.apply(cleanedExpected.toLowerCase(Locale.ROOT),
                cleanedProvided.toLowerCase(Locale.ROOT));
        if (jaroScore >= JARO_THRESHOLD) {
            return true;
        }

        double cosineScore = cosineSimilarity.apply(toVector(cleanedExpected), toVector(cleanedProvided));
        if (Double.isFinite(cosineScore) && cosineScore >= COSINE_THRESHOLD) {
            return true;
        }

        Set<String> expectedTokens = tokenize(cleanedExpected);
        Set<String> providedTokens = tokenize(cleanedProvided);
        if (!expectedTokens.isEmpty()) {
            long overlapping = providedTokens.stream().filter(expectedTokens::contains).count();
            double coverage = (double) overlapping / expectedTokens.size();
            if (coverage >= 0.6d) {
                return true;
            }
        }

        return false;
    }

    private Map<CharSequence, Integer> toVector(String input) {
        Map<CharSequence, Integer> vector = new HashMap<>();
        for (String token : tokenize(input)) {
            vector.merge(token, 1, Integer::sum);
        }
        return vector;
    }

    private Set<String> tokenize(String input) {
        if (input == null) {
            return Set.of();
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKD)
                .replaceAll("[^\p{Alnum} ]+", " ")
                .toLowerCase(Locale.ROOT);
        String[] parts = normalized.split("\\s+");
        Set<String> tokens = new HashSet<>();
        for (String part : parts) {
            if (!part.isBlank()) {
                tokens.add(part);
            }
        }
        return tokens;
    }

    private String normalizeStrict(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKD)
                .replaceAll("[^\p{Alnum}]+", "")
                .toLowerCase(Locale.ROOT);
        return normalized;
    }
}
