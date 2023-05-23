package commands;

import exceptions.IllegalArgument;
import managers.DatabaseManager;
import network.Request;
import network.Response;
import network.ResponseStatus;

import java.sql.SQLException;

public class Register extends Command{
    DatabaseManager databaseManager;

    public Register(DatabaseManager databaseManager) {
        super("register", ": Зарегистрировать пользователя");
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) throws IllegalArgument {
        this.commandLogger.info("получен юзер: " + request.getUser());
        try {
            databaseManager.addUser(request.getUser());
        } catch (SQLException e) {
            commandLogger.fatal("Невозможно добавить пользователя");
            return new Response(ResponseStatus.LOGIN_FAILED, "Введен невалидный пароль!");
        }
        return new Response(ResponseStatus.OK,"Вы успешно зарегистрированы");
    }
}
