package commands;

import exceptions.IllegalArgument;
import managers.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseStatus;
import utility.DatabaseHandler;

public class RemoveById extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public RemoveById(CollectionManager collectionManager) {
        super("remove_by_id", " id : удалить элемент из коллекции по его id");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) throws IllegalArgument {
        if (request.getArgs().isBlank()) throw new IllegalArgument();
        class NoSuchId extends RuntimeException {
        }
        try {
            int id = Integer.parseInt(request.getArgs().trim());
            if (!collectionManager.checkExist(id)) throw new NoSuchId();
            if (DatabaseHandler.getDatabaseManager().deleteObject(id, request.getUser())) {
                collectionManager.removeElement(collectionManager.getById(id));
                return new Response(ResponseStatus.OK, "Объект удален");
            } else {
                return new Response(ResponseStatus.ERROR, "Выбранный объект не удален");
            }
        } catch (NoSuchId err) {
            return new Response(ResponseStatus.ERROR, "В коллекции нет элемента с таким id");
        } catch (NumberFormatException exception) {
            return new Response(ResponseStatus.ERROR, "id должно быть числом типа int");
        }
    }
}
