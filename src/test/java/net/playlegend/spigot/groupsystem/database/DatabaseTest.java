package net.playlegend.spigot.groupsystem.database;

import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class DatabaseTest {

    @Test
    public void databaseWorks() {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        GroupGeneric testGroup = new GroupGeneric(
                "testgroup345678", 10, "Testgroup",
                "&f&lTEST &8 | &f&l", Collections.emptySet(), 'f'
        );

        UserGeneric testUser = new UserGeneric(
                UUID.randomUUID(), testGroup, null
        );

        service.createGroup(testGroup);
        service.createUser(testUser);

        try {
            Assertions.assertTrue(service.groupExists(testGroup.getKey()).get());
            Assertions.assertTrue(service.userExists(testUser.getUuid()).get());
        } catch (InterruptedException | ExecutionException ignored) {}

        service.deleteGroup(testGroup.getKey());
        service.deleteUser(testUser.getUuid());

        try {
            Assertions.assertFalse(service.groupExists(testGroup.getKey()).get());
            Assertions.assertFalse(service.userExists(testUser.getUuid()).get());
        } catch (InterruptedException | ExecutionException ignored) {}
    }

}
