package net.playlegend.spigot.groupsystem.commands.exceptions;

public class NotEnoughArgumentsException extends Exception {
    public NotEnoughArgumentsException() {
        super("");
    }

    public NotEnoughArgumentsException(String message) {
        super(message);
    }
}
