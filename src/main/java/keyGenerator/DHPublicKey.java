package keyGenerator;

import java.io.Serializable;
import java.math.BigInteger;

public interface DHPublicKey extends Serializable {
    BigInteger getPrimeNumber();
    BigInteger getGenerator();
    BigInteger generateKeyParameter();
    DHPublicKey getCopy();

    SharedSecretKey generateSecretKey(BigInteger parameter);
}
