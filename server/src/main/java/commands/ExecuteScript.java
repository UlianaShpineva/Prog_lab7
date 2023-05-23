package commands;

import exceptions.IllegalArgument;
import managers.FileManager;
import network.Request;
import network.Response;
import network.ResponseStatus;
import utility.RuntimeManager;


/**
 * Команда 'execute_script'
 * Считатывает и исполняет скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
 */
public class ExecuteScript extends Command {

    public ExecuteScript(){
        super("execute_script", " file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
    }

    /**
     * Исполнить программу
     * @param request аргументы команды
     * @throws IllegalArgument неверные аргументы
     */

    @Override
    public Response execute(Request request) throws IllegalArgument {
//        if (request.getArgs() == null || request.getArgs().isEmpty()) {
//            return new Response(ResponseStatus.ERROR, "Файл не найден");
//        }
//        String fileName = (request.getArgs().trim());
//        return new Response(ResponseStatus.EXECUTE_SCRIPT, fileName);
        if (request.getArgs().isBlank()) throw new IllegalArgument();
        return new Response(ResponseStatus.EXECUTE_SCRIPT, request.getArgs());
    }
}
