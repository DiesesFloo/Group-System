package net.playlegend.spigot.groupsystem.tablist;

import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TablistHandler {

    ExecutorService pool = Executors.newCachedThreadPool();

    public CompletableFuture<Void> setTabPrefix(Player player) {
        return CompletableFuture.runAsync(() -> {
            DatabaseService service = DatabaseRegistry.getDatabase().getService();
            Scoreboard playerSb = player.getScoreboard();

            for (Player all : Bukkit.getOnlinePlayers()) {
                Optional<GroupGeneric> groupOptional;

                try {
                    groupOptional = service.getGroup(all.getUniqueId()).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    continue;
                }

                if (groupOptional.isEmpty()) {
                    continue;
                }

                GroupGeneric group = groupOptional.get();
                String teamName = group.getKey() + group.getKey().toUpperCase();
                Team team = playerSb.getTeam(teamName);

                if (team == null) {
                    team = playerSb.registerNewTeam(teamName);
                }

                team.setPrefix(ChatColor.translateAlternateColorCodes('&', group.getPrefix()));
                team.addEntry(all.getName());
                team.setColor(Objects.requireNonNull(ChatColor.getByChar(group.getColor())));
            }

            Optional<GroupGeneric> groupOptional;

            try {
                groupOptional = service.getGroup(player.getUniqueId()).get();
            } catch (InterruptedException | ExecutionException e) {
                return;
            }

            if (groupOptional.isEmpty()) {
                return;
            }

            GroupGeneric group = groupOptional.get();
            String teamName = group.getKey() + group.getKey().toUpperCase();

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all == player) continue;

                Scoreboard allSb = all.getScoreboard();

                Team team = allSb.getTeam(teamName);

                if (team == null) {
                    team = allSb.registerNewTeam(teamName);
                }

                team.setPrefix(ChatColor.translateAlternateColorCodes('&', group.getTablistPrefix()));
                team.setColor(Objects.requireNonNull(ChatColor.getByChar(group.getColor())));
                team.addEntry(player.getName());
            }

        }, pool);
    }

    public CompletableFuture<Void> updateTablistPrefixOf(Player player) {
        return CompletableFuture.runAsync(() -> {
            DatabaseService service = DatabaseRegistry.getDatabase().getService();
            Optional<GroupGeneric> groupOptional;

            try {
                groupOptional = service.getGroup(player.getUniqueId()).get();
            } catch (InterruptedException | ExecutionException e) {
                return;
            }

            if (groupOptional.isEmpty()) {
                return;
            }

            GroupGeneric group = groupOptional.get();
            String teamName = group.getKey() + group.getKey().toUpperCase();

            for (Player all : Bukkit.getOnlinePlayers()) {
                Scoreboard allSb = all.getScoreboard();

                Team team = allSb.getTeam(teamName);

                if (team == null) {
                    team = allSb.registerNewTeam(teamName);
                }

                team.setPrefix(ChatColor.translateAlternateColorCodes('&', group.getTablistPrefix()));
                team.setColor(Objects.requireNonNull(ChatColor.getByChar(group.getColor())));
                team.addEntry(player.getName());
            }
        }, pool);
    }

}
