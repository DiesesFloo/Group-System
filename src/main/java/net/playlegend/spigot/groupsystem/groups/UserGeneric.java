package net.playlegend.spigot.groupsystem.groups;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserGeneric {

    final UUID uuid;

    GroupGeneric group;

    Timestamp groupUntilTimeStamp;

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
