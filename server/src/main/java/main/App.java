package main;

import commands.*;
import managers.CollectionManager;
import managers.CommandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.DatabaseHandler;
import utility.DatagramServer;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class App {
    public static final String HASHING_ALGORITHM = "SHA-1";
    public static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/studs";
    public static final String DATABASE_URL_HELIOS = "jdbc:postgresql://pg:5432/studs";
    public static final String DATABASE_CONFIG_PATH = "C:\\Users\\ulian\\Рабочий стол\\study\\itmo\\programming\\lab7\\server\\db.cfg";



    public static int port = 6094;
    public static final int connection_timeout = 60 * 1000;
    public static Logger logger = LogManager.getLogger(App.class);
    public static void main(String[] args) {

        CollectionManager collectionManager = new CollectionManager();
        CommandManager commandManager = new CommandManager(DatabaseHandler.getDatabaseManager());

        commandManager.register(new Help(commandManager));
        commandManager.register(new Info(collectionManager));
        commandManager.register(new Show(collectionManager));
        commandManager.register(new AddElement(collectionManager));
        commandManager.register(new Update(collectionManager));
        commandManager.register(new RemoveById(collectionManager));
        commandManager.register(new Clear(collectionManager));
        commandManager.register(new ExecuteScript());
        commandManager.register(new Exit());
        commandManager.register(new AddIfMax(collectionManager));
        commandManager.register(new AddIfMin(collectionManager));
        commandManager.register(new RemoveGreater(collectionManager));
        commandManager.register(new RemoveAllByExpelledStudents(collectionManager));
        commandManager.register(new AverageOfExpelledStudents(collectionManager));
        commandManager.register(new PrintAscending(collectionManager));
        commandManager.register(new Register(DatabaseHandler.getDatabaseManager()));
        commandManager.register(new Ping());

        DatagramServer server = null;
        try {
            server = new DatagramServer(InetAddress.getLocalHost(), port, connection_timeout, commandManager, DatabaseHandler.getDatabaseManager());
        } catch (UnknownHostException e) {
            logger.fatal("Неизвестный хост");
        } catch (SocketException e) {
            logger.fatal("Случилась ошибка сокета");
        }
        server.run();
    }
}
