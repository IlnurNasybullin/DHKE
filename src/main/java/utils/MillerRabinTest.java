package utils;

import java.math.BigInteger;

import static utils.BigIntegerGenerator.getRandomBigNumber;
import static utils.QuickExpMod.quickExpMod;

public class MillerRabinTest {
    //Максимальное число, до которого выполняется метод пробных делителей (для выявления простоты числа)
    private static final BigInteger MAX_NUMBER_FOR_SIMPLE_TEST = BigInteger.valueOf(256);

    /**
     * Вероятностный тест Миллера-Рабина для выявления простоты числа. Количество раундов теста -
     * log<sub>2</>number. Реализацию см. в методе {@link MillerRabinTest#testOnPrimality(BigInteger, int)}
     * @param number - число, проверямое на простоту
     * @return результат теста: вероятностно простое (true) или составное (false)
     */
    public static boolean testOnPrimality(BigInteger number) {
        return testOnPrimality(number, number.bitLength() - 1);
    }

    /**
     * Вероятностный тест Миллера-Рабина для выявления простоты числа с заданным количеством раундов. Вероятность
     * ошибки не превышает 4<sup>-rounds</sup>.
     * @param number - число, проверямое на простоту
     * @param rounds - количество раундов в тесте
     * @return результат теста: вероятностно простое (true) или составное (или 1) (false)
     */
    public static boolean testOnPrimality(BigInteger number, int rounds) {
        if (!isPositive(number)) {
            throw new IllegalArgumentException("number isn't positive");
        }

        if (equals(number, BigInteger.TWO)) {
            return true;
        }

        if (!number.testBit(0) || number.bitLength() < 2) {
            return false;
        }

        if (number.compareTo(MAX_NUMBER_FOR_SIMPLE_TEST) < 0) {
            return isPrimeTrialDivision(number.intValue());
        }

        int s = 0;
        BigInteger lastNumber = number.subtract(BigInteger.ONE);
        while (!lastNumber.testBit(s)) {
            s++;
        }

        BigInteger d = lastNumber.shiftRight(s);

        int round_index = 0;
        boolean isPrime = true;
        while (isPrime && round_index < rounds) {
            isPrime = MillerRabinRound(number, d, s);
            round_index++;
        }

        return isPrime;
    }

    private static boolean isPrimeTrialDivision(int number) {
        int maxDivider = (int)Math.sqrt(number);

        int i = 3;
        boolean isPrime = true;
        while (isPrime && (i <= maxDivider)) {
            isPrime = (number % i) != 0;
            i += 2;
        }

        return isPrime;
    }

    //простое ли число (с точки зрения раунда)
    private static boolean MillerRabinRound(BigInteger number, BigInteger d, int s) {
        BigInteger randomNumber = getRandomBigNumber(BigInteger.TWO, number);
        BigInteger lastNumber = number.subtract(BigInteger.ONE);

        BigInteger x = quickExpMod(randomNumber, d, number);

        if (equals(x, BigInteger.ONE) || equals(x, lastNumber)) {
            return true;
        }

        boolean isPrime = false;
        int count = 0;

        while (!isPrime && count < s) {
            x = quickExpMod(x, BigInteger.TWO, number);
            isPrime = equals(x, lastNumber);
            count++;
        }

        return isPrime;
    }

    public static boolean isPositive(BigInteger number) {
        return number.compareTo(BigInteger.ZERO) > 0;
    }
    public static boolean equals(BigInteger one, BigInteger two) {
        return one.compareTo(two) == 0;
    }
}
