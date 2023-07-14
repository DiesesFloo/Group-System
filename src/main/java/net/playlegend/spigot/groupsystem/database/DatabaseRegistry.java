package net.playlegend.spigot.groupsystem.database;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.database.util.Database;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseRegistry {

    @Getter
    @Setter
    static Database database;

}
