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
 * Команда 'add_if_min'
 * Добавляет элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
 */
public class AddIfMin extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public AddIfMin(CollectionManager collectionManager) {
        super("add_if_min", " {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
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
        }
        StudyGroup newElement = request.getObject();
        if (newElement.compareEl(collectionManager.getCollection().stream()
            .filter(Objects::nonNull)
            .min(StudyGroup::compareEl)
            .orElse(null)) >= 1)
        {
            int new_id = DatabaseHandler.getDatabaseManager().addObject(request.getObject(), request.getUser());
            if(new_id == -1) return new Response(ResponseStatus.ERROR, "Объект добавить не удалось");
            newElement.setId(new_id);
            newElement.setUserLogin(request.getUser().name());
            collectionManager.addElement(newElement);
            return new Response(ResponseStatus.OK, "Объект успешно добавлен");
        } else {
            return new Response(ResponseStatus.ERROR,"Элемент больше максимального");
        }
    }
}