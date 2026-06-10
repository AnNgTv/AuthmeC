package com.authsystem.commands;

import com.authsystem.Main;
import com.authsystem.manager.DatabaseManager;
import com.authsystem.manager.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogoutCommand implements CommandExecutor {

    private Main plugin;
    private DatabaseManager databaseManager;
    private AuthManager authManager;

    public LogoutCommand(Main plugin, DatabaseManager databaseManager, AuthManager authManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!authManager.isLoggedIn(player)) {
            player.sendMessage(plugin.getConfig().getString("messages.not-logged-in"));
            return true;
        }

        authManager.logout(player);
        player.sendMessage(plugin.getConfig().getString("messages.logout-success"));

        return true;
    }
}