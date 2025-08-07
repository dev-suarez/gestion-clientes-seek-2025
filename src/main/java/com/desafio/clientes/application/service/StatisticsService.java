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
     * Calcula la media aritmética de una lista de números enteros.
     * 
     * @param values lista de valores numéricos
     * @return media aritmética o 0.0 si la lista está vacía
     */
    public double calculateMean(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        return values.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
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

    /**
     * Calcula la mediana de una lista de números enteros.
     * 
     * @param values lista de valores numéricos
     * @return mediana o 0.0 si la lista está vacía
     */
    public double calculateMedian(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        var sortedValues = values.stream()
                .sorted()
                .toList();

        int size = sortedValues.size();

        if (size % 2 == 0) {
            // Para listas de tamaño par, promedio de los dos valores centrales
            int mid1 = sortedValues.get(size / 2 - 1);
            int mid2 = sortedValues.get(size / 2);
            return (mid1 + mid2) / 2.0;
        } else {
            // Para listas de tamaño impar, el valor central
            return sortedValues.get(size / 2);
        }
    }

    /**
     * Calcula percentiles de una lista de valores.
     * 
     * @param values     lista de valores numéricos
     * @param percentile percentil a calcular (0-100)
     * @return valor del percentil especificado
     */
    public double calculatePercentile(List<Integer> values, int percentile) {
        if (values == null || values.isEmpty() || percentile < 0 || percentile > 100) {
            return 0.0;
        }

        var sortedValues = values.stream()
                .sorted()
                .toList();

        if (percentile == 0) {
            return sortedValues.get(0);
        }

        if (percentile == 100) {
            return sortedValues.get(sortedValues.size() - 1);
        }

        double index = (percentile / 100.0) * (sortedValues.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex) {
            return sortedValues.get(lowerIndex);
        }

        double weight = index - lowerIndex;
        return sortedValues.get(lowerIndex) * (1 - weight) + sortedValues.get(upperIndex) * weight;
    }

    /**
     * Verifica si una lista de valores está dentro de un rango normal.
     * Utiliza la regla empírica (68-95-99.7) para detectar outliers.
     * 
     * @param values lista de valores a analizar
     * @return true si los valores están dentro del rango normal
     */
    public boolean isWithinNormalRange(List<Integer> values) {
        if (values == null || values.size() < 3) {
            return true; // Muy pocos datos para determinar normalidad
        }

        double mean = calculateMean(values);
        double stdDev = calculateStandardDeviation(values);

        if (stdDev == 0) {
            return true; // Todos los valores son iguales
        }

        // Contar valores dentro de 2 desviaciones estándar (aproximadamente 95%)
        long valuesWithinTwoStdDev = values.stream()
                .mapToInt(Integer::intValue)
                .filter(value -> Math.abs(value - mean) <= 2 * stdDev)
                .count();

        double percentageWithinRange = (double) valuesWithinTwoStdDev / values.size();

        // Si al menos el 90% de los valores están dentro de 2 desviaciones estándar,
        // consideramos que la distribución es relativamente normal
        return percentageWithinRange >= 0.90;
    }
}