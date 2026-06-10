package com.authsystem.commands;

import com.authsystem.Main;
import com.authsystem.manager.DatabaseManager;
import com.authsystem.manager.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    private Main plugin;
    private DatabaseManager databaseManager;
    private AuthManager authManager;

    public RegisterCommand(Main plugin, DatabaseManager databaseManager, AuthManager authManager) {
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

        if (args.length != 2) {
            player.sendMessage("§cUsage: /register <password> <password>");
            return true;
        }

        String password1 = args[0];
        String password2 = args[1];

        int minLength = plugin.getConfig().getInt("security.password-min-length", 6);
        if (password1.length() < minLength) {
            player.sendMessage("§cPassword must be at least " + minLength + " characters long!");
            return true;
        }

        if (!password1.equals(password2)) {
            player.sendMessage(plugin.getConfig().getString("messages.register-password-mismatch"));
            return true;
        }

        if (databaseManager.userExists(player.getName())) {
            player.sendMessage(plugin.getConfig().getString("messages.register-already-exists"));
            return true;
        }

        String hashedPassword = authManager.hashPassword(password1);
        if (databaseManager.registerUser(player.getName(), hashedPassword)) {
            player.sendMessage(plugin.getConfig().getString("messages.register-success"));
            authManager.login(player);
        } else {
            player.sendMessage("§cError registering account!");
        }

        return true;
    }
}