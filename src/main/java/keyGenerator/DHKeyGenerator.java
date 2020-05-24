package keyGenerator;

import utils.BigIntegerGenerator;
import utils.QuickExpMod;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import static utils.MillerRabinTest.testOnPrimality;
import static utils.QuickExpMod.quickExpMod;

public class DHKeyGenerator implements Serializable {

    public static final int STANDARD_KEY_LENGTH = 128;

    public DHKey generateParameters(int bitLength) {
        BigInteger primeNumber;
        BigInteger safePrimeNumber;


        do {
            primeNumber = generateSpecialPrimeNumber(bitLength);
            safePrimeNumber = primeNumber.shiftRight(1);
        } while (!testOnPrimality(safePrimeNumber));

        BigInteger generator = getPrimeGenerator(primeNumber, safePrimeNumber);
        DHKey dhKey = new DHKey(primeNumber, generator);

        return dhKey;
    }

    private BigInteger generateSpecialPrimeNumber(int bitLength) {
        if (bitLength < 2) {
            throw new IllegalArgumentException("Нельзя сгенерировать простоу число с заданным количеством бит");
        }

        BigInteger primeNumber;
        Random random = new SecureRandom();

        do {
            primeNumber = new BigInteger(bitLength, random).setBit(bitLength - 1).setBit(0).setBit(1);
        } while (!testOnPrimality(primeNumber));

        return primeNumber;
    }

    private BigInteger getPrimeGenerator(BigInteger primeNumber, BigInteger safePrimeNumber) {
        boolean isFound = false;
        BigInteger primeGenerator = BigInteger.TWO;

        while (!isFound && (primeGenerator.compareTo(primeNumber) < 0)) {
            isFound = (!quickExpMod(primeGenerator, BigInteger.TWO, primeNumber).equals(BigInteger.ONE)) &&
                      (!quickExpMod(primeGenerator, safePrimeNumber, primeNumber).equals(BigInteger.ONE));
            if (!isFound) {
                primeGenerator = primeGenerator.add(BigInteger.ONE);
            }
        }

        return primeGenerator;
    }

    private class DHKey implements DHPublicKey {
        private BigInteger primeNumber;
        private BigInteger generator;
        private BigInteger closeExponent;

        private DHKey(BigInteger primeNumber, BigInteger generator) {
            this.primeNumber = primeNumber;
            this.generator = generator;
        }

        @Override
        public BigInteger getPrimeNumber() {
            return primeNumber;
        }

        @Override
        public BigInteger getGenerator() {
            return generator;
        }

        @Override
        public BigInteger generateKeyParameter() {
            this.closeExponent = BigIntegerGenerator.getRandomBigNumber(BigInteger.TWO, primeNumber.subtract(BigInteger.ONE));
            return quickExpMod(generator, closeExponent, primeNumber);
        }

        @Override
        public DHPublicKey getCopy() {
            return new DHKey(primeNumber, generator);
        }

        @Override
        public SharedSecretKey generateSecretKey(BigInteger parameter) {
            return new DHSecretKey(quickExpMod(parameter, closeExponent, primeNumber));
        }
    }

    private class DHSecretKey implements SharedSecretKey {
        private BigInteger secretKey;

        public DHSecretKey(BigInteger secretKey) {
            this.secretKey = secretKey;
        }

        @Override
        public String toString() {
            return secretKey.toString();
        }
    }
}
