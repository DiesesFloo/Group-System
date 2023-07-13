package net.playlegend.spigot.groupsystem.database.groups;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserGeneric {

    private final UUID uuid;

    private final Set<GroupGeneric> groups;

    public final GroupGeneric getHighestGroup() {
        return groups.parallelStream()
                .min(Comparator.comparingInt(GroupGeneric::getPriority))
                .orElse(null);
    }

}
