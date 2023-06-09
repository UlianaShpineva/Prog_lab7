package managers;

import exceptions.DisconnectClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.DatagramServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class FutureManager {
    private static final Collection<Future<ConnectionManagerPool>> fixedThreadPoolFutures = new ArrayList<>();
    private static final Logger futureManagerLogger = LogManager.getLogger(FutureManager.class);

    public static void addNewFixedThreadPoolFuture(Future<ConnectionManagerPool> future){
        fixedThreadPoolFutures.add(future);
    }

    public static void checkAllFutures() {

        if(!fixedThreadPoolFutures.isEmpty()) {
            fixedThreadPoolFutures.forEach(s -> futureManagerLogger.debug(s.toString()));
        }
        fixedThreadPoolFutures.stream()
                .filter(Future::isDone)
                .forEach(f -> {
                    try {
                        ConnectionManager.submitNewResponse(f.get());
//                        futureManagerLogger.info( + " обработан");
//                        ConnectionManager.close();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });
        fixedThreadPoolFutures.removeIf(Future::isDone);
    }
}
