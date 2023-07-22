package net.playlegend.spigot.groupsystem.cmd;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.playlegend.spigot.groupsystem.commands.cmd.GetGroupCommand;
import net.playlegend.spigot.groupsystem.commands.cmd.ReloadGroupMessagesCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.NoPermissionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ReloadGroupMessagesCommandTest {

    private final ReloadGroupMessagesCommand command = new ReloadGroupMessagesCommand();
    private static ServerMock server;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void sendsNoPermMessage() {
        PlayerMock player = server.addPlayer();

        Assertions.assertThrows(NoPermissionException.class, () -> command.execute(player, command.getLabel(), new String[]{}));
    }

}
