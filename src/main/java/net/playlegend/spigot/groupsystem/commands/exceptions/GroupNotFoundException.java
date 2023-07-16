package net.playlegend.spigot.groupsystem.commands.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class GroupNotFoundException extends Exception {

    final String input;

    public GroupNotFoundException() {
        super("");

        this.input = "";
    }

    public GroupNotFoundException(String input) {
        super("");

        this.input = input;
    }

    public GroupNotFoundException(String input, String message) {
        super(message);

        this.input = input;
    }

}
