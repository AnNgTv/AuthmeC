package com.authsystem;

import com.authsystem.commands.RegisterCommand;
import com.authsystem.commands.LoginCommand;
import com.authsystem.commands.LogoutCommand;
import com.authsystem.commands.AuthMeCCommand;
import com.authsystem.manager.DatabaseManager;
import com.authsystem.manager.AuthManager;
import com.authsystem.listener.AuthListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private DatabaseManager databaseManager;
    private AuthManager authManager;

    @Override
    public void onEnable() {
        // Create default config
        saveDefaultConfig();

        // Initialize managers
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        authManager = new AuthManager();

        // Register listener
        getServer().getPluginManager().registerEvents(new AuthListener(this, authManager, databaseManager), this);

        // Register commands
        getCommand("register").setExecutor(new RegisterCommand(this, databaseManager, authManager));
        getCommand("login").setExecutor(new LoginCommand(this, databaseManager, authManager));
        getCommand("logout").setExecutor(new LogoutCommand(this, databaseManager, authManager));
        getCommand("authmec").setExecutor(new AuthMeCCommand(this, databaseManager, authManager));

        getLogger().info("AuthMeC enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
        getLogger().info("AuthMeC disabled!");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }
}