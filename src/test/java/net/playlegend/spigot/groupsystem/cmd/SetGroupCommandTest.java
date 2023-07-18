package net.playlegend.spigot.groupsystem.cmd;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.playlegend.spigot.groupsystem.commands.cmd.SetDisplayNameCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SetGroupCommandTest extends CommandTest {

    public SetGroupCommandTest() {
        super(new SetDisplayNameCommand());
    }

    @Test
    public void sendsPlayerNotFoundMessage() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        Assertions.assertThrows(PlayerNotFoundException.class, () -> command.execute(player, "getgroup", new String[]{"x", "x"}));
    }
}
