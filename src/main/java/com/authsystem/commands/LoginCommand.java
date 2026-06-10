package com.authsystem.commands;

import com.authsystem.Main;
import com.authsystem.manager.DatabaseManager;
import com.authsystem.manager.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    private Main plugin;
    private DatabaseManager databaseManager;
    private AuthManager authManager;

    public LoginCommand(Main plugin, DatabaseManager databaseManager, AuthManager authManager) {
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

        if (args.length != 1) {
            player.sendMessage("§cUsage: /login <password>");
            return true;
        }

        if (authManager.isLoggedIn(player)) {
            player.sendMessage(plugin.getConfig().getString("messages.already-logged-in"));
            return true;
        }

        String password = args[0];
        String hashedPassword = databaseManager.getPassword(player.getName());

        if (hashedPassword == null) {
            player.sendMessage("§cAccount not found! Use /register <password> <password>");
            return true;
        }

        if (authManager.verifyPassword(password, hashedPassword)) {
            authManager.login(player);
            player.sendMessage(plugin.getConfig().getString("messages.login-success"));
        } else {
            player.sendMessage(plugin.getConfig().getString("messages.login-failed"));
        }

        return true;
    }
}