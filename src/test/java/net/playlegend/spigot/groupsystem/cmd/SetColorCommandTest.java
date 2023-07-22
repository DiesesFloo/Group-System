package net.playlegend.spigot.groupsystem.cmd;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.playlegend.spigot.groupsystem.commands.cmd.SetColorCommand;
import net.playlegend.spigot.groupsystem.message.Message;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class SetColorCommandTest extends CommandTest{
    public SetColorCommandTest() {
        super(new SetColorCommand());
    }

    @Test
    public void sendsNoIntMessage() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        command.execute(player, "setcolor", new String[]{"newgroup123456", "test", "&a", "test", "a"});

        Message msg = new Message("commands.creategroup.priority-number");

        assert Objects.equals(player.nextMessage(), msg.get());
    }
}
