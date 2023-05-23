package managers;

import network.Request;
import network.Response;
import network.ResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.Pair;
import utility.RequestHandler;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.*;

public class ConnectionManager implements Runnable {
    private final CommandManager commandManager;
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
    private static DatagramSocket socket;
    private static SocketAddress clientAddr;
    private final DatabaseManager databaseManager;
    static final Logger connectionManagerLogger = LogManager.getLogger(ConnectionManager.class);

    public ConnectionManager(CommandManager commandManager, DatagramSocket socket, DatabaseManager databaseManager) {
        this.commandManager = commandManager;
        this.socket = socket;
        this.databaseManager = databaseManager;
    }

    public static SocketAddress getClientAddr() {
        return clientAddr;
    }

    @Override
    public void run() {
        Pair pair = null;
        try{
            pair = receiveData();
        } catch (IOException e){
            return;
        }

        byte[] dataFromClient = pair.getData();
        clientAddr = pair.getAddr();

        try {
            connectionManagerLogger.info("Соединение установлено. Адрес: " + clientAddr.toString());
        } catch (Exception e) {
            connectionManagerLogger.info("Ошибка подключения");
        }

        Request request = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(dataFromClient);
            ObjectInputStream ois = new ObjectInputStream(bais);
            request = (Request) ois.readObject();
            connectionManagerLogger.info("Получен реквест с командой " + request.getCommandName(), request);
        } catch (IOException e) {
//                continue;
        } catch (ClassNotFoundException e) {
            connectionManagerLogger.info("Произошла ошибка при чтении полученных данных!");
        }

        Response response;
        if(!databaseManager.confirmUser(request.getUser()) && !request.getCommandName().equals("register")){
            connectionManagerLogger.info("Юзер не одобрен");
            response = new Response(ResponseStatus.LOGIN_FAILED, "Неверный пользователь!");
            submitNewResponse(new ConnectionManagerPool(response));
        } else {
            FutureManager.addNewFixedThreadPoolFuture(fixedThreadPool.submit(new RequestHandler(commandManager, request)));
        }
    }

    public static void submitNewResponse(ConnectionManagerPool connectionManagerPool) {
        new Thread(() -> {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            try {
                ObjectOutput oo = new ObjectOutputStream(bStream);
                oo.writeObject(connectionManagerPool.response());
                oo.flush();
                oo.close();
            }catch (IOException e) {
                System.out.println(e);
            }
            byte[] data = bStream.toByteArray();

            try {
                sendData(data, getClientAddr());
                connectionManagerLogger.info("Отправлен ответ. IP: " + getClientAddr().toString());
            } catch (Exception e) {
            }
        }).start();
    }

    public Pair receiveData() throws IOException {
        byte[] res = new byte[0];
        SocketAddress addr = null;
        Pair pair = new Pair(res, addr);
        byte[] buffer = new byte[65507];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        addr = packet.getSocketAddress();
        res = buffer;

        pair.setDataAndAddr(res, addr);
        return  pair;
    }

    public static void sendData(byte[] data, SocketAddress addr) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, addr);
        socket.send(packet);
    }
}
