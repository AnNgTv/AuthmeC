package com.authsystem.commands;

import com.authsystem.Main;
import com.authsystem.manager.DatabaseManager;
import com.authsystem.manager.AuthManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuthMeCCommand implements CommandExecutor {

    private Main plugin;
    private DatabaseManager databaseManager;
    private AuthManager authManager;

    public AuthMeCCommand(Main plugin, DatabaseManager databaseManager, AuthManager authManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("authmec.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§c/authmec forcelogin <player>");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        if (subcommand.equals("forcelogin")) {
            if (args.length != 2) {
                sender.sendMessage("§cUsage: /authmec forcelogin <player>");
                return true;
            }

            String playerName = args[1];
            Player target = Bukkit.getPlayer(playerName);

            if (target == null) {
                String message = plugin.getConfig().getString("messages.force-login-not-found")
                        .replace("{player}", playerName);
                sender.sendMessage(message);
                return true;
            }

            authManager.forceLogin(target.getName());
            String message = plugin.getConfig().getString("messages.force-login-success")
                    .replace("{player}", target.getName());
            sender.sendMessage(message);
            target.sendMessage("§a[AuthMeC] §fYou have been force logged in!");

            return true;
        }

        sender.sendMessage("§cUnknown subcommand: " + subcommand);
        return true;
    }
}