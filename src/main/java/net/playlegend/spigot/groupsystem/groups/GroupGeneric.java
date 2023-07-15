package net.playlegend.spigot.groupsystem.groups;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.permission.Permission;

import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class GroupGeneric {

    final String key;

    final int priority;

    final String displayName;

    final String prefix;

    final Set<Permission> permissions;

    final char color;

}
