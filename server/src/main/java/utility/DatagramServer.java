package utility;

import managers.CommandManager;
import managers.ConnectionManager;
import managers.DatabaseManager;
import managers.FutureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ForkJoinPool;

public class DatagramServer {
    private final DatagramSocket socket;
    private final InetSocketAddress addr;
    private int soTimeout;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();
    static final Logger datagramServerLogger = LogManager.getLogger(DatagramServer.class);
    public DatagramServer(InetAddress address, int port, int soTimeout, CommandManager commandManager, DatabaseManager databaseManager) throws SocketException {
        this.addr = new InetSocketAddress(address, port);
        this.socket = new DatagramSocket(getAddr());
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
    }

    public void close() {
        socket.close();
    }
}
