package utils;

import java.math.BigInteger;

public class QuickExpMod {
    public static BigInteger quickExpMod(BigInteger base, BigInteger power, BigInteger mod) {
        if (power.compareTo(BigInteger.ZERO) < 0) {
            throw new ArithmeticException("power is negative number!");
        }

        if (mod.compareTo(BigInteger.ZERO) <= 0) {
            throw new ArithmeticException("mod isn't positive number");
        }

        BigInteger res = BigInteger.ONE;
        BigInteger a = base;

        for (int i = 0; i < power.bitLength(); i++) {
            if (power.testBit(i)) {
                res = res.multiply(a).mod(mod);
            }
            a = a.multiply(a).mod(mod);
        }

        return res;
    }
}
