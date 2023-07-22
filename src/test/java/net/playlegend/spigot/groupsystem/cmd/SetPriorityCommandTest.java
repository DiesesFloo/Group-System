package net.playlegend.spigot.groupsystem.cmd;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.playlegend.spigot.groupsystem.commands.cmd.SetPriorityCommand;
import net.playlegend.spigot.groupsystem.message.Message;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class SetPriorityCommandTest extends CommandTest {
    public SetPriorityCommandTest() {
        super(new SetPriorityCommand());
    }

    @Test
    public void sendsNoIntMessage() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        command.execute(player, "creategroup", new String[]{"newgroup123456", "test", "a", "test", "a"});

        Message msg = new Message("commands.setpriority.priority-number");

        assert Objects.equals(player.nextMessage(), msg.get());
    }
}
