import keyGenerator.DHKeyGenerator;
import keyGenerator.DHPublicKey;
import keyGenerator.SharedSecretKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static final int MIN_BIT_LENGTH = 8;

    public static void main(String[] args) {
        DHPublicKey publicKey = generateKey();
        BigInteger parameter = null;

        try(Socket socket = new Socket("localhost", 8080);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Подключение к серверу успешно установлено");

            System.out.println("Отправка ключа");
            out.writeObject(publicKey.getCopy());
            System.out.println("Ключ успешно отправлен");

            System.out.println("Генерация параметра x: ");
            parameter = publicKey.generateKeyParameter();
            System.out.println("Параметр успешно сгенерирован");

            System.out.println("Отправка параметра x");
            out.writeObject(parameter);
            System.out.println("Параметр x успешно отправлен");

            parameter = (BigInteger) in.readObject();
            System.out.println("Параметр y получен от сервера");

            System.out.println("Связь с сервером прервана");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Генерация общего секретного ключа");
        SharedSecretKey secretKey = publicKey.generateSecretKey(parameter);
        System.out.printf("Секретный ключ успешно сгенерирован (%s)", secretKey);
    }

    private static DHPublicKey generateKey() {
        System.out.printf("Введите длину ключа - количество бит (по умолчанию - %d бит). Минимальное допустимое количество бит - %d: ", DHKeyGenerator.STANDARD_KEY_LENGTH, MIN_BIT_LENGTH);
        int bitLength = getBitLength();

        DHKeyGenerator keyGenerator = new DHKeyGenerator();
        DHPublicKey key = keyGenerator.generateParameters(bitLength);

        System.out.println("Публичный ключ успешно сгенерирован");
        return key;
    }

    private static int getBitLength() {
        Scanner scanner = new Scanner(System.in);
        int bitLength;
        boolean correct;

        do {
            String line = scanner.nextLine();
            bitLength = line.equals("") ? DHKeyGenerator.STANDARD_KEY_LENGTH : Integer.parseInt(line);
            correct = bitLength >= MIN_BIT_LENGTH;

            if (!correct) {
                System.err.println("Ошибка! Введите длину ключа заново");
            }

        } while (!correct);

        return bitLength;
    }
}
