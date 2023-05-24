package commands;

import exceptions.IllegalArgument;
import managers.CollectionManager;
import models.StudyGroup;
import network.Request;
import network.Response;
import network.ResponseStatus;
import utility.DatabaseHandler;

import java.util.List;

/**
 * Команда 'clear'
 * Очищает коллекцию
 */
public class Clear extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        super("clear", ": очистить коллекцию");
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
        List<Integer> deletedIds = collectionManager.getCollection().stream()
                .filter(studyGroup -> studyGroup.getUserLogin().equals(request.getUser().name()))
                .map(StudyGroup::getId)
                .toList();
        if(DatabaseHandler.getDatabaseManager().deleteAllObjects(request.getUser(), deletedIds)) {
            collectionManager.removeElements(deletedIds);
            return new Response(ResponseStatus.OK, "Ваши элементы удалены");
        }
        return new Response(ResponseStatus.ERROR, "Элементы коллекции удалить не удалось");
    }
}
