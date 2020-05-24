package utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class BigIntegerGenerator {
    /**
     * Возвращает случайное число {@link BigInteger} от [min, max)
     * @param min - нижняя граница для генерации случайного числа
     * @param max - верхняя граница для генерации случайного числа
     * @return случайное число
     */
    public static BigInteger getRandomBigNumber(BigInteger min, BigInteger max) {
        if (max.compareTo(min) <= 0) {
            throw new IllegalArgumentException("upper bound number mist be more than lower bound!");
        }

        BigInteger number = max.subtract(min);

        int n = number.bitLength();
        Random random = new SecureRandom();
        BigInteger randomNumber = new BigInteger(n, random).mod(number);
        return randomNumber.add(min);
    }
}
