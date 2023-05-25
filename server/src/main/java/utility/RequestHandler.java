package utility;

import exceptions.CommandRuntimeError;
import exceptions.IllegalArgument;
import exceptions.NoSuchCommand;
import managers.CommandManager;
import managers.ConnectionManagerPool;
import network.Request;
import network.Response;
import network.ResponseStatus;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;

public class RequestHandler implements Callable<ConnectionManagerPool> {
    private CommandManager commandManager;
    private Request request;
    private DatagramSocket socket;
    private SocketAddress socketAddress;

    public RequestHandler(CommandManager commandManager, Request request, DatagramSocket socket, SocketAddress socketAddress) {
        this.commandManager = commandManager;
        this.request = request;
        this.socket = socket;
        this.socketAddress = socketAddress;
    }

    @Override
    public ConnectionManagerPool call() {
        try {
            return new ConnectionManagerPool(commandManager.execute(request), socket, socketAddress);
        } catch (IllegalArgument e) {
            return new ConnectionManagerPool(new Response(ResponseStatus.WRONG_ARGUMENTS, "Неверное использование аргументов команды"), socket, socketAddress);
        } catch (CommandRuntimeError e) {
            return new ConnectionManagerPool(new Response(ResponseStatus.ERROR, "Ошибка при исполнении программы"), socket, socketAddress);
        } catch (NoSuchCommand e) {
            return new ConnectionManagerPool(new Response(ResponseStatus.ERROR, "Такой команды нет в списке"), socket, socketAddress);
        }
    }
}
