package net.playlegend.spigot.groupsystem.commands.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class PlayerNotFoundException extends Exception {

    final String input;

    public PlayerNotFoundException() {
        super("");

        this.input = "";
    }

    public PlayerNotFoundException(String input) {
        super("");

        this.input = input;
    }

    public PlayerNotFoundException(String input, String message) {
        super(message);

        this.input = input;
    }

}
