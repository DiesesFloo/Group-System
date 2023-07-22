package net.playlegend.spigot.groupsystem.cmd;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.playlegend.spigot.groupsystem.commands.cmd.CreateGroupCommand;
import net.playlegend.spigot.groupsystem.message.Message;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class CreateGroupCommandTest extends CommandTest{
    public CreateGroupCommandTest() {
        super(new CreateGroupCommand());
    }

    @Test
    public void sendsNoColorCodeMessage() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        command.execute(player, "creategroup", new String[]{"newgroup123456", "test", "&131", "5", "a"});

        Message msg = new Message("commands.creategroup.no-color-code");
        msg.setInput("&131");

        assert Objects.equals(player.nextMessage(), msg.get());
    }

    @Test
    public void sendsNoIntMessage() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        command.execute(player, "creategroup", new String[]{"newgroup123456", "test", "&a", "test", "a"});

        Message msg = new Message("commands.creategroup.priority-number");

        assert Objects.equals(player.nextMessage(), msg.get());
    }

}
