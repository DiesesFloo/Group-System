package net.playlegend.spigot.groupsystem.cmd;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.playlegend.spigot.groupsystem.commands.cmd.GetGroupCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.NoPermissionException;
import net.playlegend.spigot.groupsystem.commands.exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GetGroupCommandTest {
    private final GetGroupCommand command = new GetGroupCommand();
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

        Assertions.assertThrows(NoPermissionException.class, () -> command.execute(player, "getgroup", new String[]{"x"}));
    }

    @Test
    public void sendsPlayerNotFoundMessage() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        Assertions.assertThrows(PlayerNotFoundException.class, () -> command.execute(player, "getgroup", new String[]{"x"}));
    }


}
