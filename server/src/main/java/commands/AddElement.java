package commands;

import exceptions.IllegalArgument;
import managers.CollectionManager;
import models.StudyGroup;
import network.Request;
import network.Response;
import network.ResponseStatus;
import utility.DatabaseHandler;

import java.util.Objects;

/**
 * Команда 'add'
 * Добавляет новый элемент в коллекцию
 */
public class AddElement extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public AddElement(CollectionManager collectionManager) {
        super("add", " {element} : добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнить программу
     * @param request аргументы команды
     * @throws IllegalArgument неверные аргументы
     */
    @Override
    public Response execute(Request request) throws IllegalArgument {
        if (!request.getArgs().isBlank()) throw new IllegalArgument();
        if (Objects.isNull(request.getObject())){
            return new Response(ResponseStatus.ASK_OBJECT, "Для команды " + this.getName() + " требуется объект");
        } else{
            int new_id = DatabaseHandler.getDatabaseManager().addObject(request.getObject(), request.getUser());
            if(new_id == -1) return new Response(ResponseStatus.ERROR, "Объект добавить не удалось");
            request.getObject().setId(new_id);
            request.getObject().setUserLogin(request.getUser().name());
            collectionManager.addElement(request.getObject());
            return new Response(ResponseStatus.OK, "Объект успешно добавлен");
        }
    }
}
