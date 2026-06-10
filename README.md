# AuthMeC

A Minecraft authentication plugin with support for SQLite, MySQL, and YAML storage.

## Features

- ✅ User registration and login system
- ✅ Support for **SQLite** (default), **MySQL**, and **YAML** databases
- ✅ Secure password hashing with BCrypt
- ✅ Admin command: `/authmec forcelogin <player>`
- ✅ Configurable messages and settings
- ✅ Easy database switching

## Installation

1. Download the plugin JAR file
2. Place it in your `plugins/` folder
3. Start/restart your server
4. Configure `plugins/AuthMeC/config.yml`

## Commands

| Command | Description | Permission |
|---------|-------------|-----------|
| `/register <password> <password>` | Register a new account | - |
| `/login <password>` | Login to your account | - |
| `/logout` | Logout from your account | - |
| `/authmec forcelogin <player>` | Force login a player | `authmec.admin` |

## Configuration

Edit `plugins/AuthMeC/config.yml`:

```yaml
database:
  type: sqlite  # Options: sqlite, mysql, yaml
  
  sqlite:
    file: plugins/AuthMeC/database.db
  
  mysql:
    host: localhost
    port: 3306
    database: authsystem
    username: root
    password: ""
  
  yaml:
    file: plugins/AuthMeC/users.yml