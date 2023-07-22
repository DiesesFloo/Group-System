package net.playlegend.spigot.groupsystem.cmd;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.playlegend.spigot.groupsystem.commands.exceptions.NoPermissionException;
import net.playlegend.spigot.groupsystem.commands.exceptions.NotEnoughArgumentsException;
import org.bukkit.command.Command;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public abstract class CommandTest {

    protected final Command command;
    protected static ServerMock server;

    protected CommandTest(Command command) {
        this.command = command;
    }

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

    @Test
    public void sendsWrongUseMessage() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        Assertions.assertThrows(NotEnoughArgumentsException.class, () -> command.execute(player, command.getLabel(), new String[]{}));
    }

}
