package utility;

import exceptions.ConnectionErrorException;
import exceptions.OpeningServerException;
import managers.*;
import managers.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ForkJoinPool;

public class DatagramServer {//extends Server {
    private final DatagramSocket socket;
    private final InetSocketAddress addr;
    private int soTimeout;
    private FileManager fileManager;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();
    static final Logger datagramServerLogger = LogManager.getLogger(DatagramServer.class);
    public DatagramServer(InetAddress address, int port, int soTimeout, CommandManager commandManager, DatabaseManager databaseManager) throws SocketException {
        //super(new InetSocketAddress(address, port), soTimeout, requestHandler, fileManager, collectionManager);
        this.addr = new InetSocketAddress(address, port);
        this.socket = new DatagramSocket(getAddr());
//        this.socket.setReuseAddress(true);
        this.socket.setSoTimeout(1);
        this.soTimeout = soTimeout;
        this.commandManager = commandManager;
        this.databaseManager = databaseManager;
    }


    public InetSocketAddress getAddr() {
        return addr;
    }

    public void run(){
        while (true){
            FutureManager.checkAllFutures();
            forkJoinPool.submit(new ConnectionManager(commandManager, socket, databaseManager));
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
//        close();
    }

    public Pair receiveData() throws IOException {
        byte[] res = new byte[0];
        SocketAddress addr = null;
        Pair pair = new Pair(res, addr);
        byte[] buffer = new byte[102400000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);
        addr = packet.getSocketAddress();
        res = buffer;

        pair.setDataAndAddr(res, addr);
        return  pair;
    }


    public void sendData(byte[] data, SocketAddress addr) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, addr);
        socket.send(packet);
    }



    public void close() {
        socket.close();
    }
}
