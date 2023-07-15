package net.playlegend.spigot.groupsystem.groups;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserGeneric {

    private final UUID uuid;

    private final GroupGeneric group;

    private final Timestamp groupUntilTimeStamp;

    public Optional<Long> groupUntil() {
        if (groupUntilTimeStamp == null) {
            return Optional.empty();
        }

        return Optional.of(groupUntilTimeStamp.getTime());
    }

    public boolean groupIsPermanent() {
        return groupUntilTimeStamp == null;
    }


}
