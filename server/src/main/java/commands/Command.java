package commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Абстрактный класс для команд
 */
public abstract class Command implements Executable {
    private final String name;
    private final String description;
    protected Logger commandLogger = LogManager.getLogger(this.getClass());

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return Имя команды
     */
    public String getName() {
        return name;
    }

    /**
     * @return Описание команды
     */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name) && Objects.equals(description, command.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return name + ':' + description;
    }
}