package utility;

import exceptions.CommandRuntimeError;
import exceptions.IllegalArgument;
import exceptions.NoSuchCommand;
import managers.CommandManager;
import managers.ConnectionManagerPool;
import network.Request;
import network.Response;
import network.ResponseStatus;

import java.util.concurrent.Callable;

public class RequestHandler implements Callable<ConnectionManagerPool> {
    private CommandManager commandManager;
    private Request request;

    public RequestHandler(CommandManager commandManager, Request request) {
        this.commandManager = commandManager;
        this.request = request;
    }

    @Override
    public ConnectionManagerPool call() {
        try {
            return new ConnectionManagerPool(commandManager.execute(request));
        } catch (IllegalArgument e) {
            return new ConnectionManagerPool(new Response(ResponseStatus.WRONG_ARGUMENTS, "Неверное использование аргументов команды"));
        } catch (CommandRuntimeError e) {
            return new ConnectionManagerPool(new Response(ResponseStatus.ERROR, "Ошибка при исполнении программы"));
        } catch (NoSuchCommand e) {
            return new ConnectionManagerPool(new Response(ResponseStatus.ERROR, "Такой команды нет в списке"));
        }
    }
}
