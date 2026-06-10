package com.authsystem.manager;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DatabaseManager {

    private JavaPlugin plugin;
    private String databaseType;
    private Connection connection;
    private Map<String, Integer> userCache = new HashMap<>();

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        String type = plugin.getConfig().getString("database.type", "sqlite");
        this.databaseType = type.toLowerCase();

        switch (databaseType) {
            case "sqlite":
                initializeSQLite();
                break;
            case "mysql":
                initializeMySQL();
                break;
            case "yaml":
                initializeYAML();
                break;
            default:
                plugin.getLogger().warning("Unknown database type: " + type);
        }
    }

    private void initializeSQLite() {
        try {
            String filePath = plugin.getConfig().getString("database.sqlite.file");
            File dbFile = new File(filePath);
            dbFile.getParentFile().mkdirs();

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + filePath);

            createTable();
            plugin.getLogger().info("SQLite database initialized!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize SQLite: " + e.getMessage());
        }
    }

    private void initializeMySQL() {
        try {
            String host = plugin.getConfig().getString("database.mysql.host");
            int port = plugin.getConfig().getInt("database.mysql.port");
            String database = plugin.getConfig().getString("database.mysql.database");
            String username = plugin.getConfig().getString("database.mysql.username");
            String password = plugin.getConfig().getString("database.mysql.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database,
                    username, password
            );

            createTable();
            plugin.getLogger().info("MySQL database initialized!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize MySQL: " + e.getMessage());
        }
    }

    private void initializeYAML() {
        try {
            String filePath = plugin.getConfig().getString("database.yaml.file");
            File yamlFile = new File(filePath);
            yamlFile.getParentFile().mkdirs();

            if (!yamlFile.exists()) {
                yamlFile.createNewFile();
            }

            plugin.getLogger().info("YAML database initialized!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize YAML: " + e.getMessage());
        }
    }

    private void createTable() {
        if (databaseType.equals("yaml")) return;

        try {
            String sql;
            if (databaseType.equals("sqlite")) {
                sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username VARCHAR(16) UNIQUE NOT NULL," +
                        "password VARCHAR(255) NOT NULL," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";
            } else {
                sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(16) UNIQUE NOT NULL," +
                        "password VARCHAR(255) NOT NULL," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";
            }

            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create table: " + e.getMessage());
        }
    }

    public boolean userExists(String username) {
        if (databaseType.equals("yaml")) {
            return getYAMLUser(username) != null;
        }

        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();
            rs.close();
            stmt.close();
            return exists;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking user: " + e.getMessage());
            return false;
        }
    }

    public boolean registerUser(String username, String hashedPassword) {
        if (databaseType.equals("yaml")) {
            return saveYAMLUser(username, hashedPassword);
        }

        try {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public String getPassword(String username) {
        if (databaseType.equals("yaml")) {
            Map<String, Object> user = getYAMLUser(username);
            return user != null ? (String) user.get("password") : null;
        }

        try {
            String sql = "SELECT password FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            String password = null;
            if (rs.next()) {
                password = rs.getString("password");
            }

            rs.close();
            stmt.close();
            return password;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting password: " + e.getMessage());
            return null;
        }
    }

    // YAML Methods
    private Map<String, Object> getYAMLUser(String username) {
        try {
            String filePath = plugin.getConfig().getString("database.yaml.file");
            File yamlFile = new File(filePath);

            if (!yamlFile.exists()) return null;

            Yaml yaml = new Yaml();
            FileInputStream fis = new FileInputStream(yamlFile);
            Map<String, Map<String, Object>> data = yaml.load(fis);
            fis.close();

            return data != null ? data.get(username) : null;
        } catch (Exception e) {
            plugin.getLogger().warning("Error reading YAML: " + e.getMessage());
            return null;
        }
    }

    private boolean saveYAMLUser(String username, String hashedPassword) {
        try {
            String filePath = plugin.getConfig().getString("database.yaml.file");
            File yamlFile = new File(filePath);

            Yaml yaml = new Yaml();
            Map<String, Map<String, Object>> data = new HashMap<>();

            if (yamlFile.exists()) {
                FileInputStream fis = new FileInputStream(yamlFile);
                Map<String, Map<String, Object>> existingData = yaml.load(fis);
                fis.close();
                if (existingData != null) {
                    data.putAll(existingData);
                }
            }

            if (data.containsKey(username)) {
                return false;
            }

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("password", hashedPassword);
            userMap.put("created_at", System.currentTimeMillis());
            data.put(username, userMap);

            FileWriter fw = new FileWriter(yamlFile);
            yaml.dump(data, fw);
            fw.close();
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error saving YAML: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing connection: " + e.getMessage());
        }
    }
}