package com.authsystem.manager;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class AuthManager {

    private Set<String> loggedInPlayers = new HashSet<>();

    public String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }

    public void login(Player player) {
        loggedInPlayers.add(player.getName());
    }

    public void logout(Player player) {
        loggedInPlayers.remove(player.getName());
    }

    public boolean isLoggedIn(Player player) {
        return loggedInPlayers.contains(player.getName());
    }

    public void forceLogin(String playerName) {
        loggedInPlayers.add(playerName);
    }
}