package com.desafio.clientes.application.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio especializado en cálculos estadísticos.
 * 
 * Implementa el patrón Strategy para diferentes tipos de cálculos
 * y separa la lógica estadística del servicio principal de clientes.
 * Esto mejora la modularidad y reutilización del código.
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@Service
public class StatisticsService {

    /**
     * Calcula la desviación estándar de una lista de números enteros.
     * 
     * Implementa la fórmula de desviación estándar poblacional:
     * σ = √(Σ(xi - μ)² / N)
     * 
     * @param values lista de valores numéricos
     * @return desviación estándar o 0.0 si la lista está vacía
     */
    public double calculateStandardDeviation(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        if (values.size() == 1) {
            return 0.0;
        }

        // Calcular la media
        double mean = values.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        // Calcular la suma de las diferencias cuadradas
        double sumSquaredDifferences = values.stream()
                .mapToDouble(value -> Math.pow(value - mean, 2))
                .sum();

        // Calcular la desviación estándar
        double variance = sumSquaredDifferences / values.size();
        return Math.sqrt(variance);
    }

    /**
     * Encuentra el valor mínimo en una lista de números enteros.
     * 
     * @param values lista de valores numéricos
     * @return valor mínimo o null si la lista está vacía
     */
    public Integer findMinimum(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        return values.stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElse(0);
    }

    /**
     * Encuentra el valor máximo en una lista de números enteros.
     * 
     * @param values lista de valores numéricos
     * @return valor máximo o null si la lista está vacía
     */
    public Integer findMaximum(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        return values.stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }
}