package net.playlegend.spigot.groupsystem.groups;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.permission.Permission;
import org.apache.commons.lang3.StringUtils;

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

    public String getTablistPrefix() {
        String out = prefix;

        if (out.substring(out.length()-2).matches("&([0-f]|[k-o]|r)")) {
            out = out.substring(0, out.length()-2);
        }

        if(out.endsWith(" ")) {
            out = StringUtils.chop(out);
        }

        return out;
    }

}
