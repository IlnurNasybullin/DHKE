import keyGenerator.DHPublicKey;
import keyGenerator.SharedSecretKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        DHPublicKey publicKey = null;
        BigInteger parameter = null;

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            Socket clientSocket = serverSocket.accept();

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("К серверу подключился сокет: " + clientSocket.getRemoteSocketAddress());

            publicKey = (DHPublicKey) in.readObject();
            System.out.println("Ключ успешно принят");

            System.out.println("Генерация параметра y: ");
            parameter = publicKey.generateKeyParameter();
            System.out.println("Параметр успешно сгенерирован");

            System.out.println("Отправка параметра y");
            out.writeObject(parameter);
            System.out.println("Параметр y успешно отправлен");

            parameter = (BigInteger) in.readObject();
            System.out.println("Параметр x получен от сервера");

            System.out.println("Связь с сокетом прервана");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Генерация общего секретного ключа");
        SharedSecretKey secretKey = publicKey.generateSecretKey(parameter);
        System.out.printf("Секретный ключ успешно сгенерирован (%s)", secretKey);
    }
}
