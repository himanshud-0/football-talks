-- ═══════════════════════════════════════════════════════════════════════════
-- FOOTBALL TALKS DATABASE SCHEMA
-- ═══════════════════════════════════════════════════════════════════════════

-- Create database
CREATE DATABASE IF NOT EXISTS football_talks CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE football_talks;

-- Drop existing tables (for clean install)
DROP TABLE IF EXISTS player_stats;
DROP TABLE IF EXISTS transfers;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS teams;
DROP TABLE IF EXISTS competitions;
DROP TABLE IF EXISTS post_likes;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

-- ─────────────────────────────────────────────────────────────────────────
-- USERS TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) DEFAULT '',
    bio VARCHAR(500) DEFAULT '',
    favorite_team VARCHAR(100) DEFAULT '',
    profile_image_url VARCHAR(255) DEFAULT 'https://ui-avatars.com/api/?name=Football+Fan&background=0f172a&color=ffffff&bold=true',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────
-- POSTS TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_author (author_id),
    INDEX idx_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────
-- COMMENTS TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_post (post_id),
    INDEX idx_user (user_id),
    INDEX idx_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────
-- POST LIKES TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE post_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_post_user_like (post_id, user_id),
    INDEX idx_post (post_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────
-- COMPETITIONS TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE competitions (
    competition_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    external_api_id BIGINT UNIQUE,
    country VARCHAR(60),
    logo_url VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_competition_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────
-- TEAMS TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE teams (
    team_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    external_api_id BIGINT UNIQUE,
    country VARCHAR(60),
    league VARCHAR(60),
    logo_url VARCHAR(500),
    competition_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_team_name (name),
    FOREIGN KEY (competition_id) REFERENCES competitions(competition_id) ON DELETE SET NULL,
    INDEX idx_team_competition (competition_id),
    INDEX idx_team_league (league),
    INDEX idx_team_country (country)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────
-- PLAYERS TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE players (
    player_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    external_api_id BIGINT UNIQUE,
    age INT,
    nationality VARCHAR(60),
    position VARCHAR(40),
    team_id BIGINT,
    firstname VARCHAR(120),
    lastname VARCHAR(120),
    photo_url VARCHAR(500),
    description VARCHAR(500),
    market_value BIGINT DEFAULT 0,
    appearances INT DEFAULT 0,
    goals_scored INT DEFAULT 0,
    assists INT DEFAULT 0,
    special_ability VARCHAR(120) DEFAULT '',
    minutes_played INT DEFAULT 0,
    yellow_cards INT DEFAULT 0,
    red_cards INT DEFAULT 0,
    rating DECIMAL(3,1) DEFAULT 0.0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE SET NULL,
    INDEX idx_player_name (name),
    INDEX idx_player_team (team_id),
    INDEX idx_player_position (position),
    INDEX idx_player_market_value (market_value),
    INDEX idx_player_goals (goals_scored),
    INDEX idx_player_assists (assists)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────
-- PLAYER STATS TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE player_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    team_id BIGINT,
    competition_id BIGINT,
    season VARCHAR(10) NOT NULL,
    appearances INT DEFAULT 0,
    goals INT DEFAULT 0,
    assists INT DEFAULT 0,
    minutes INT DEFAULT 0,
    rating DECIMAL(3,1) DEFAULT 0.0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE SET NULL,
    FOREIGN KEY (competition_id) REFERENCES competitions(competition_id) ON DELETE SET NULL,
    UNIQUE KEY unique_player_stats_row (player_id, team_id, competition_id, season),
    INDEX idx_player_stats_player (player_id),
    INDEX idx_player_stats_team (team_id),
    INDEX idx_player_stats_competition (competition_id),
    INDEX idx_player_stats_season (season)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────
-- TRANSFERS TABLE
-- ─────────────────────────────────────────────────────────────────────────
CREATE TABLE transfers (
    transfer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    from_team_id BIGINT,
    to_team_id BIGINT NOT NULL,
    transfer_fee BIGINT,
    transfer_type VARCHAR(40),
    transfer_date DATE,
    season VARCHAR(10),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (from_team_id) REFERENCES teams(team_id) ON DELETE SET NULL,
    FOREIGN KEY (to_team_id) REFERENCES teams(team_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_player_transfer_event (player_id, from_team_id, to_team_id, transfer_date),
    INDEX idx_transfer_date (transfer_date),
    INDEX idx_transfer_player (player_id),
    INDEX idx_transfer_to_team (to_team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ═══════════════════════════════════════════════════════════════════════════
-- SCHEMA CREATED SUCCESSFULLY
-- ═══════════════════════════════════════════════════════════════════════════

SELECT 'Database schema created successfully!' AS message;
