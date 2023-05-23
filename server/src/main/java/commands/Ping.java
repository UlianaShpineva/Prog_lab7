package commands;

import exceptions.IllegalArgument;
import network.Request;
import network.Response;
import network.ResponseStatus;

public class Ping extends Command{
    public Ping() {
        super("ping", ": пингануть сервер");
    }

    @Override
    public Response execute(Request request) throws IllegalArgument {
        return new Response(ResponseStatus.OK, "pong");
    }
}
