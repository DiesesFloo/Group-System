package net.playlegend.spigot.groupsystem.database;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.database.util.Database;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class DatabaseRegistry {

    static Database database;

}
