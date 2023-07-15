package net.playlegend.spigot.groupsystem.commands.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoPermissionException extends Exception {

    final String permission;

    public NoPermissionException() {
        super("");

        this.permission = "";
    }

    public NoPermissionException(String permission) {
        super("");

        this.permission = permission;
    }

    public NoPermissionException(String permission, String message) {
        super(message);

        this.permission = permission;
    }

}
