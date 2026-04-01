-- ═══════════════════════════════════════════════════════════════════════════
-- FOOTBALL TALKS SAMPLE DATA
-- ═══════════════════════════════════════════════════════════════════════════
USE football_talks;

-- ─────────────────────────────────────────────────────────────────────────
-- SAMPLE USERS (Password: password123 for all)
-- ─────────────────────────────────────────────────────────────────────────
INSERT INTO users (username, email, password, full_name, bio, favorite_team, profile_image_url, created_at, active) VALUES
('john_doe', 'john@example.com', '$2a$10$Z3zPuJ8M9YYN9x8XPyP4Huw5Lh5FQqH8LqJ5K5KdJ5J5J5J5J5J5J5', 'John Doe', 
 'Die-hard Manchester United fan. Love discussing tactics and transfers!', 'Manchester United',
 'https://ui-avatars.com/api/?name=John+Doe&background=b91c1c&color=ffffff&bold=true', DATE_SUB(NOW(), INTERVAL 30 DAY), TRUE),
 
('jane_smith', 'jane@example.com', '$2a$10$Z3zPuJ8M9YYN9x8XPyP4Huw5Lh5FQqH8LqJ5K5KdJ5J5J5J5J5J5J5', 'Jane Smith',
 'Barcelona supporter. Messi is the GOAT!', 'FC Barcelona',
 'https://ui-avatars.com/api/?name=Jane+Smith&background=1d4ed8&color=ffffff&bold=true', DATE_SUB(NOW(), INTERVAL 28 DAY), TRUE),
 
('mike_wilson', 'mike@example.com', '$2a$10$Z3zPuJ8M9YYN9x8XPyP4Huw5Lh5FQqH8LqJ5K5KdJ5J5J5J5J5J5J5', 'Mike Wilson',
 'Liverpool through and through. YNWA!', 'Liverpool FC',
 'https://ui-avatars.com/api/?name=Mike+Wilson&background=b91c1c&color=ffffff&bold=true', DATE_SUB(NOW(), INTERVAL 24 DAY), TRUE),
 
('sarah_jones', 'sarah@example.com', '$2a$10$Z3zPuJ8M9YYN9x8XPyP4Huw5Lh5FQqH8LqJ5K5KdJ5J5J5J5J5J5J5', 'Sarah Jones',
 'Real Madrid fan. Hala Madrid!', 'Real Madrid',
 'https://ui-avatars.com/api/?name=Sarah+Jones&background=1f2937&color=ffffff&bold=true', DATE_SUB(NOW(), INTERVAL 20 DAY), TRUE),
 
('alex_brown', 'alex@example.com', '$2a$10$Z3zPuJ8M9YYN9x8XPyP4Huw5Lh5FQqH8LqJ5K5KdJ5J5J5J5J5J5J5', 'Alex Brown',
 'Chelsea supporter. Keep the blue flag flying high!', 'Chelsea FC',
 'https://ui-avatars.com/api/?name=Alex+Brown&background=1e40af&color=ffffff&bold=true', DATE_SUB(NOW(), INTERVAL 18 DAY), TRUE),

('david_lee', 'david@example.com', '$2a$10$Z3zPuJ8M9YYN9x8XPyP4Huw5Lh5FQqH8LqJ5K5KdJ5J5J5J5J5J5J5', 'David Lee',
 'Arsenal fan focused on youth development and recruitment strategy.', 'Arsenal',
 'https://ui-avatars.com/api/?name=David+Lee&background=dc2626&color=ffffff&bold=true', DATE_SUB(NOW(), INTERVAL 14 DAY), TRUE),

('emma_clark', 'emma@example.com', '$2a$10$Z3zPuJ8M9YYN9x8XPyP4Huw5Lh5FQqH8LqJ5K5KdJ5J5J5J5J5J5J5', 'Emma Clark',
 'Serie A watcher who loves tactical analysis and smart defending.', 'Inter Milan',
 'https://ui-avatars.com/api/?name=Emma+Clark&background=0f766e&color=ffffff&bold=true', DATE_SUB(NOW(), INTERVAL 12 DAY), TRUE),

('carlos_mendes', 'carlos@example.com', '$2a$10$Z3zPuJ8M9YYN9x8XPyP4Huw5Lh5FQqH8LqJ5K5KdJ5J5J5J5J5J5J5', 'Carlos Mendes',
 'La Liga enthusiast tracking prospects, academies, and transfer value.', 'Atletico Madrid',
 'https://ui-avatars.com/api/?name=Carlos+Mendes&background=7c3aed&color=ffffff&bold=true', DATE_SUB(NOW(), INTERVAL 10 DAY), TRUE);

-- ─────────────────────────────────────────────────────────────────────────
-- SAMPLE POSTS
-- ─────────────────────────────────────────────────────────────────────────
INSERT INTO posts (title, content, author_id, created_at, likes_count, comments_count) VALUES
('Is Haaland the Best Striker in the World Right Now?', 
 'After watching his performance last night, I think Erling Haaland has proven he''s currently the best striker in world football. His positioning, finishing, and physical presence are unmatched. What do you all think? Is there anyone who comes close?',
 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), 5, 3),

('Messi vs Ronaldo: The Eternal Debate', 
 'I know this has been discussed a million times, but after the World Cup, can we finally settle this? For me, Messi''s triumph in Qatar sealed his legacy as the greatest of all time. What''s your take?',
 2, DATE_SUB(NOW(), INTERVAL 5 HOUR), 12, 8),

('Liverpool''s Midfield Needs Urgent Reinforcement', 
 'As a Liverpool fan, I''m worried about our midfield situation. We desperately need fresh legs and creativity in the middle of the park. Who should we target in the transfer market?',
 3, DATE_SUB(NOW(), INTERVAL 1 DAY), 8, 5),

('Real Madrid''s Champions League Dynasty', 
 '15 European Cups! Real Madrid''s success in the Champions League is simply unparalleled. What makes them so special in this competition? Is it the history, the mentality, or just sheer quality?',
 4, DATE_SUB(NOW(), INTERVAL 2 DAY), 15, 6),

('The Rise of Saudi Arabian Football', 
 'With all the big names moving to Saudi Pro League, do you think this league can actually compete with European football in the next 10 years? Or is it just a money grab?',
 1, DATE_SUB(NOW(), INTERVAL 3 DAY), 7, 4),

('Best Young Talent to Watch This Season', 
 'Who are the upcoming stars you''re most excited about? For me, Jude Bellingham has been absolutely phenomenal. Who''s on your radar?',
 5, DATE_SUB(NOW(), INTERVAL 4 DAY), 10, 7),

('VAR: Ruining Football or Necessary Evolution?', 
 'Controversial opinion: VAR is killing the spontaneity and joy of football. The delays, the overturned goals, the confusion - is it worth it for accuracy?',
 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 18, 12),

('Premier League vs La Liga: Which is Better?', 
 'The age-old question! Premier League fans claim it''s more competitive. La Liga fans point to technical quality. Where do you stand?',
 3, DATE_SUB(NOW(), INTERVAL 6 DAY), 14, 9);

-- ─────────────────────────────────────────────────────────────────────────
-- SAMPLE COMMENTS
-- ─────────────────────────────────────────────────────────────────────────
INSERT INTO comments (content, post_id, user_id, created_at) VALUES
-- Comments on Post 1 (Haaland)
('Agreed! His numbers are insane. 50+ goals in his first season at City!', 1, 2, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('Benzema would like a word 😏', 1, 4, DATE_SUB(NOW(), INTERVAL 90 MINUTE)),
('He''s incredible but let''s see him do it in the Champions League knockout stages consistently', 1, 3, DATE_SUB(NOW(), INTERVAL 45 MINUTE)),

-- Comments on Post 2 (Messi vs Ronaldo)
('World Cup was the missing piece. Messi is clear now!', 2, 1, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
('Ronaldo has more international trophies. Don''t forget Euro 2016!', 2, 5, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('Can we just appreciate both? We''ll never see two players like this again', 2, 3, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('Stats don''t lie - Messi has more assists, dribbles, chances created', 2, 4, DATE_SUB(NOW(), INTERVAL 2 HOUR)),

-- Comments on Post 3 (Liverpool midfield)
('Bellingham would have been perfect but Real Madrid got him', 3, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
('We need someone like Tchouameni or Camavinga', 3, 5, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
('The Henderson and Fabinho era is over. Time for fresh blood', 3, 2, DATE_SUB(NOW(), INTERVAL 15 HOUR)),

-- Comments on Post 4 (Real Madrid CL)
('It''s the mentality! They never give up even when losing', 4, 1, DATE_SUB(NOW(), INTERVAL 40 HOUR)),
('Having players like Modric, Kroos, Benzema who''ve won it multiple times helps', 4, 3, DATE_SUB(NOW(), INTERVAL 36 HOUR)),
('The Bernabéu atmosphere in CL nights is something else', 4, 5, DATE_SUB(NOW(), INTERVAL 30 HOUR)),

-- Comments on Post 5 (Saudi league)
('Money alone is not enough. Coaching standards and academies will decide the future.', 5, 6, DATE_SUB(NOW(), INTERVAL 60 HOUR)),
('It can grow fast, but UEFA competitions still have the strongest sporting pull.', 5, 7, DATE_SUB(NOW(), INTERVAL 55 HOUR)),

-- Comments on Post 6 (Young talents)
('Lamine Yamal is unreal for his age. Decision-making is already top level.', 6, 2, DATE_SUB(NOW(), INTERVAL 80 HOUR)),
('Musiala and Wirtz are the two I would build around long term.', 6, 8, DATE_SUB(NOW(), INTERVAL 78 HOUR)),

-- Comments on Post 7 (VAR)
('VAR itself is fine, the slow communication and inconsistency are the issue.', 7, 4, DATE_SUB(NOW(), INTERVAL 100 HOUR)),
('Semi-automated offside improved things, but fans still need clearer explanations.', 7, 6, DATE_SUB(NOW(), INTERVAL 95 HOUR)),

-- Comments on Post 8 (Premier League vs La Liga)
('Premier League has depth, but La Liga remains elite in technical and tactical play.', 8, 5, DATE_SUB(NOW(), INTERVAL 120 HOUR)),
('Both are top in different ways: PL intensity and La Liga buildup quality.', 8, 7, DATE_SUB(NOW(), INTERVAL 118 HOUR));

-- ─────────────────────────────────────────────────────────────────────────
-- SAMPLE LIKES
-- ─────────────────────────────────────────────────────────────────────────
INSERT INTO post_likes (post_id, user_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),
(3, 1), (3, 2), (3, 4), (3, 5),
(4, 1), (4, 2), (4, 3), (4, 5),
(5, 2), (5, 3), (5, 4), (5, 5),
(6, 1), (6, 3), (6, 4), (6, 6),
(7, 1), (7, 2), (7, 5), (7, 8),
(8, 2), (8, 3), (8, 4), (8, 7);

-- ─────────────────────────────────────────────────────────────────────────
-- FOOTBALL DATA (Based on footy (1).sql)
-- ─────────────────────────────────────────────────────────────────────────
INSERT INTO competitions (name, country, logo_url) VALUES
('Bundesliga', 'Germany', 'https://ui-avatars.com/api/?name=Bundesliga&background=111827&color=ffffff&bold=true'),
('Brasileirao', 'Brazil', 'https://ui-avatars.com/api/?name=Brasileirao&background=111827&color=ffffff&bold=true'),
('EPL', 'England', 'https://ui-avatars.com/api/?name=Premier+League&background=111827&color=ffffff&bold=true'),
('Eredivisie', 'Netherlands', 'https://ui-avatars.com/api/?name=Eredivisie&background=111827&color=ffffff&bold=true'),
('Ekstraklasa', 'Poland', 'https://ui-avatars.com/api/?name=Ekstraklasa&background=111827&color=ffffff&bold=true'),
('LaLiga', 'Spain', 'https://ui-avatars.com/api/?name=LaLiga&background=111827&color=ffffff&bold=true'),
('Liga Portugal', 'Portugal', 'https://ui-avatars.com/api/?name=Liga+Portugal&background=111827&color=ffffff&bold=true'),
('Ligue 1', 'France', 'https://ui-avatars.com/api/?name=Ligue+1&background=111827&color=ffffff&bold=true'),
('MLS', 'USA', 'https://ui-avatars.com/api/?name=MLS&background=111827&color=ffffff&bold=true'),
('Saudi Pro League', 'Saudi Arabia', 'https://ui-avatars.com/api/?name=Saudi+Pro+League&background=111827&color=ffffff&bold=true'),
('Serie A', 'Italy', 'https://ui-avatars.com/api/?name=Serie+A&background=111827&color=ffffff&bold=true'),
('Super Lig', 'Turkey', 'https://ui-avatars.com/api/?name=Super+Lig&background=111827&color=ffffff&bold=true');

INSERT INTO teams (name, country, league) VALUES
('AC Milan', 'Italy', 'Serie A'),
('Ajax', 'Netherlands', 'Eredivisie'),
('Al Nassr', 'Saudi Arabia', 'Saudi Pro League'),
('Arsenal', 'England', 'EPL'),
('AS Roma', 'Italy', 'Serie A'),
('Aston Villa', 'England', 'EPL'),
('Atletico Madrid', 'Spain', 'LaLiga'),
('Barcelona', 'Spain', 'LaLiga'),
('Bayer Leverkusen', 'Germany', 'Bundesliga'),
('Bayern Munich', 'Germany', 'Bundesliga'),
('Benfica', 'Portugal', 'Liga Portugal'),
('Borussia Dortmund', 'Germany', 'Bundesliga'),
('Chelsea', 'England', 'EPL'),
('Fenerbahce', 'Turkey', 'Super Lig'),
('Galatasaray', 'Turkey', 'Super Lig'),
('Gornik Zabrze', 'Poland', 'Ekstraklasa'),
('Inter Miami', 'USA', 'MLS'),
('Inter Milan', 'Italy', 'Serie A'),
('Juventus', 'Italy', 'Serie A'),
('Lazio', 'Italy', 'Serie A'),
('Liverpool', 'England', 'EPL'),
('Lyon', 'France', 'Ligue 1'),
('Manchester City', 'England', 'EPL'),
('Manchester United', 'England', 'EPL'),
('Marseille', 'France', 'Ligue 1'),
('Monaco', 'France', 'Ligue 1'),
('Napoli', 'Italy', 'Serie A'),
('Porto', 'Portugal', 'Liga Portugal'),
('PSG', 'France', 'Ligue 1'),
('RB Leipzig', 'Germany', 'Bundesliga'),
('Real Madrid', 'Spain', 'LaLiga'),
('Santos', 'Brazil', 'Brasileirao'),
('Sevilla', 'Spain', 'LaLiga'),
('Sporting CP', 'Portugal', 'Liga Portugal'),
('Tottenham Hotspur', 'England', 'EPL');

UPDATE teams t
SET
    competition_id = (
        SELECT c.competition_id
        FROM competitions c
        WHERE c.name = t.league
        LIMIT 1
    ),
    logo_url = CONCAT(
        'https://ui-avatars.com/api/?name=',
        REPLACE(t.name, ' ', '+'),
        '&background=0f172a&color=ffffff&bold=true'
    );

INSERT INTO players (name, age, nationality, position, team_id, market_value) VALUES
('Jude Bellingham', 22, 'England', 'Midfielder', (SELECT team_id FROM teams WHERE name='Real Madrid'), 120000000),
('Erling Haaland', 25, 'Norway', 'Forward', (SELECT team_id FROM teams WHERE name='Manchester City'), 94500000),
('Lionel Messi', 38, 'Argentina', 'Forward', (SELECT team_id FROM teams WHERE name='Inter Miami'), 47250000),
('Cristiano Ronaldo', 41, 'Portugal', 'Forward', (SELECT team_id FROM teams WHERE name='Al Nassr'), 47250000),
('Kylian Mbappe', 27, 'France', 'Forward', (SELECT team_id FROM teams WHERE name='Real Madrid'), 73500000),
('Kevin De Bruyne', 35, 'Belgium', 'Midfielder', (SELECT team_id FROM teams WHERE name='Manchester City'), 45000000),
('Vinicius Junior', 26, 'Brazil', 'Forward', (SELECT team_id FROM teams WHERE name='Real Madrid'), 94500000),
('Rodri', 30, 'Spain', 'Midfielder', (SELECT team_id FROM teams WHERE name='Manchester City'), 70000000),
('Mohamed Salah', 34, 'Egypt', 'Forward', (SELECT team_id FROM teams WHERE name='Liverpool'), 47250000),
('Harry Kane', 33, 'England', 'Forward', (SELECT team_id FROM teams WHERE name='Bayern Munich'), 47250000),
('Robert Lewandowski', 37, 'Poland', 'Forward', (SELECT team_id FROM teams WHERE name='Barcelona'), 47250000),
('Lamine Yamal', 19, 'Spain', 'Forward', (SELECT team_id FROM teams WHERE name='Barcelona'), 126000000),
('Bukayo Saka', 25, 'England', 'Forward', (SELECT team_id FROM teams WHERE name='Arsenal'), 94500000),
('Phil Foden', 26, 'England', 'Midfielder', (SELECT team_id FROM teams WHERE name='Manchester City'), 90000000),
('Jamal Musiala', 23, 'Germany', 'Midfielder', (SELECT team_id FROM teams WHERE name='Bayern Munich'), 90000000),
('Florian Wirtz', 23, 'Germany', 'Midfielder', (SELECT team_id FROM teams WHERE name='Bayer Leverkusen'), 90000000),
('Pedri', 23, 'Spain', 'Midfielder', (SELECT team_id FROM teams WHERE name='Barcelona'), 90000000),
('Gavi', 22, 'Spain', 'Midfielder', (SELECT team_id FROM teams WHERE name='Barcelona'), 120000000),
('Martin Odegaard', 27, 'Norway', 'Midfielder', (SELECT team_id FROM teams WHERE name='Arsenal'), 70000000),
('Bruno Fernandes', 32, 'Portugal', 'Midfielder', (SELECT team_id FROM teams WHERE name='Manchester United'), 45000000),
('Declan Rice', 27, 'England', 'Midfielder', (SELECT team_id FROM teams WHERE name='Arsenal'), 70000000),
('Federico Valverde', 28, 'Uruguay', 'Midfielder', (SELECT team_id FROM teams WHERE name='Real Madrid'), 70000000),
('Luka Modric', 40, 'Croatia', 'Midfielder', (SELECT team_id FROM teams WHERE name='Real Madrid'), 45000000),
('Toni Kroos', 36, 'Germany', 'Midfielder', NULL, 45000000),
('Eduardo Camavinga', 24, 'France', 'Midfielder', (SELECT team_id FROM teams WHERE name='Real Madrid'), 90000000),
('Aurelien Tchouameni', 26, 'France', 'Midfielder', (SELECT team_id FROM teams WHERE name='Real Madrid'), 90000000),
('Bernardo Silva', 32, 'Portugal', 'Midfielder', (SELECT team_id FROM teams WHERE name='Manchester City'), 45000000),
('Julian Alvarez', 26, 'Argentina', 'Forward', (SELECT team_id FROM teams WHERE name='Atletico Madrid'), 94500000),
('Lautaro Martinez', 29, 'Argentina', 'Forward', (SELECT team_id FROM teams WHERE name='Inter Milan'), 73500000),
('Victor Osimhen', 28, 'Nigeria', 'Forward', (SELECT team_id FROM teams WHERE name='Napoli'), 73500000),
('Khvicha Kvaratskhelia', 25, 'Georgia', 'Forward', (SELECT team_id FROM teams WHERE name='PSG'), 94500000),
('Rafael Leao', 27, 'Portugal', 'Forward', (SELECT team_id FROM teams WHERE name='AC Milan'), 73500000),
('Antoine Griezmann', 35, 'France', 'Forward', (SELECT team_id FROM teams WHERE name='Atletico Madrid'), 47250000),
('Ousmane Dembele', 29, 'France', 'Forward', (SELECT team_id FROM teams WHERE name='PSG'), 73500000),
('Achraf Hakimi', 28, 'Morocco', 'Defender', (SELECT team_id FROM teams WHERE name='PSG'), 63000000),
('Alphonso Davies', 25, 'Canada', 'Defender', (SELECT team_id FROM teams WHERE name='Bayern Munich'), 81000000),
('Trent Alexander-Arnold', 28, 'England', 'Defender', (SELECT team_id FROM teams WHERE name='Liverpool'), 63000000),
('Reece James', 27, 'England', 'Defender', (SELECT team_id FROM teams WHERE name='Chelsea'), 63000000),
('Virgil van Dijk', 35, 'Netherlands', 'Defender', (SELECT team_id FROM teams WHERE name='Liverpool'), 40500000),
('Ruben Dias', 29, 'Portugal', 'Defender', (SELECT team_id FROM teams WHERE name='Manchester City'), 63000000),
('William Saliba', 25, 'France', 'Defender', (SELECT team_id FROM teams WHERE name='Arsenal'), 81000000),
('Ronald Araujo', 27, 'Uruguay', 'Defender', (SELECT team_id FROM teams WHERE name='Barcelona'), 63000000),
('Josko Gvardiol', 25, 'Croatia', 'Defender', (SELECT team_id FROM teams WHERE name='Manchester City'), 81000000),
('Marc-Andre ter Stegen', 34, 'Germany', 'Goalkeeper', (SELECT team_id FROM teams WHERE name='Barcelona'), 38250000),
('Alisson Becker', 34, 'Brazil', 'Goalkeeper', (SELECT team_id FROM teams WHERE name='Liverpool'), 38250000),
('Ederson', 33, 'Brazil', 'Goalkeeper', (SELECT team_id FROM teams WHERE name='Manchester City'), 38250000),
('Jan Oblak', 33, 'Slovenia', 'Goalkeeper', (SELECT team_id FROM teams WHERE name='Atletico Madrid'), 38250000),
('Gianluigi Donnarumma', 27, 'Italy', 'Goalkeeper', (SELECT team_id FROM teams WHERE name='PSG'), 59500000),
('Mike Maignan', 31, 'France', 'Goalkeeper', (SELECT team_id FROM teams WHERE name='AC Milan'), 38250000),
('Emiliano Martinez', 34, 'Argentina', 'Goalkeeper', (SELECT team_id FROM teams WHERE name='Aston Villa'), 38250000),
('Neymar Jr', 34, 'Brazil', 'Forward', (SELECT team_id FROM teams WHERE name='Santos'), 47250000),
('Paulo Dybala', 33, 'Argentina', 'Forward', (SELECT team_id FROM teams WHERE name='AS Roma'), 47250000),
('Edin Dzeko', 40, 'Bosnia and Herzegovina', 'Forward', (SELECT team_id FROM teams WHERE name='Fenerbahce'), 47250000),
('Lukas Podolski', 41, 'Germany', 'Forward', (SELECT team_id FROM teams WHERE name='Gornik Zabrze'), 47250000);

-- Add realistic performance stats for players
UPDATE players
SET
    appearances = 18 + (player_id % 20),
    goals_scored = CASE
        WHEN position = 'Forward' THEN 7 + (player_id % 19)
        WHEN position = 'Midfielder' THEN 2 + (player_id % 10)
        WHEN position = 'Defender' THEN player_id % 5
        ELSE 0
    END,
    assists = CASE
        WHEN position = 'Forward' THEN 3 + (player_id % 8)
        WHEN position = 'Midfielder' THEN 4 + (player_id % 9)
        WHEN position = 'Defender' THEN 1 + (player_id % 4)
        ELSE player_id % 2
    END,
    special_ability = CASE
        WHEN position = 'Forward' THEN 'Clinical Finishing'
        WHEN position = 'Midfielder' THEN 'Elite Playmaking'
        WHEN position = 'Defender' THEN 'Defensive Awareness'
        ELSE 'Shot Stopping'
    END,
    minutes_played = (18 + (player_id % 20)) * 84,
    yellow_cards = CASE
        WHEN position = 'Defender' THEN 2 + (player_id % 7)
        WHEN position = 'Midfielder' THEN 1 + (player_id % 5)
        WHEN position = 'Forward' THEN player_id % 4
        ELSE player_id % 3
    END,
    red_cards = CASE WHEN player_id % 17 = 0 THEN 1 ELSE 0 END,
    rating = CASE
        WHEN position = 'Forward' THEN ROUND(6.8 + ((player_id % 19) / 10), 1)
        WHEN position = 'Midfielder' THEN ROUND(6.9 + ((player_id % 16) / 10), 1)
        WHEN position = 'Defender' THEN ROUND(6.7 + ((player_id % 14) / 10), 1)
        ELSE ROUND(6.8 + ((player_id % 12) / 10), 1)
    END;

-- Override key player stats for better realism
UPDATE players SET appearances = 34, goals_scored = 30, assists = 7, special_ability = 'Power Finishing', minutes_played = 2890, yellow_cards = 3, red_cards = 0, rating = 8.6 WHERE name = 'Erling Haaland';
UPDATE players SET appearances = 33, goals_scored = 28, assists = 10, special_ability = 'Explosive Pace', minutes_played = 2815, yellow_cards = 4, red_cards = 0, rating = 8.7 WHERE name = 'Kylian Mbappe';
UPDATE players SET appearances = 31, goals_scored = 23, assists = 16, special_ability = 'Vision and Dribbling', minutes_played = 2620, yellow_cards = 2, red_cards = 0, rating = 8.8 WHERE name = 'Lionel Messi';
UPDATE players SET appearances = 35, goals_scored = 27, assists = 9, special_ability = 'Aerial Finishing', minutes_played = 3010, yellow_cards = 3, red_cards = 0, rating = 8.9 WHERE name = 'Cristiano Ronaldo';
UPDATE players SET appearances = 36, goals_scored = 22, assists = 11, special_ability = 'Inside Cut and Finish', minutes_played = 3055, yellow_cards = 2, red_cards = 0, rating = 8.4 WHERE name = 'Mohamed Salah';
UPDATE players SET appearances = 34, goals_scored = 11, assists = 12, special_ability = 'Box-to-Box Control', minutes_played = 2930, yellow_cards = 6, red_cards = 0, rating = 8.3 WHERE name = 'Jude Bellingham';
UPDATE players SET appearances = 37, goals_scored = 0, assists = 2, special_ability = 'Reflex Shot Stopping', minutes_played = 3330, yellow_cards = 1, red_cards = 0, rating = 8.2 WHERE name = 'Alisson Becker';
UPDATE players SET appearances = 36, goals_scored = 0, assists = 2, special_ability = 'Distribution Passing', minutes_played = 3240, yellow_cards = 2, red_cards = 0, rating = 8.1 WHERE name = 'Ederson';

UPDATE players p
SET
    firstname = SUBSTRING_INDEX(p.name, ' ', 1),
    lastname = CASE
        WHEN LOCATE(' ', p.name) > 0 THEN TRIM(SUBSTRING(p.name, LOCATE(' ', p.name) + 1))
        ELSE ''
    END,
    photo_url = CONCAT(
        'https://ui-avatars.com/api/?name=',
        REPLACE(p.name, ' ', '+'),
        '&background=1f2937&color=ffffff&bold=true'
    ),
    description = CONCAT(
        p.name,
        ' is a featured ',
        LOWER(p.position),
        ' playing for ',
        COALESCE((SELECT t.name FROM teams t WHERE t.team_id = p.team_id), 'club football'),
        '.'
    );

INSERT INTO player_stats (player_id, team_id, competition_id, season, appearances, goals, assists, minutes, rating)
SELECT
    p.player_id,
    p.team_id,
    t.competition_id,
    '2025/26',
    p.appearances,
    p.goals_scored,
    p.assists,
    p.minutes_played,
    p.rating
FROM players p
LEFT JOIN teams t ON t.team_id = p.team_id
WHERE p.team_id IS NOT NULL;

INSERT INTO transfers (player_id, from_team_id, to_team_id, transfer_fee, transfer_date, season) VALUES
((SELECT player_id FROM players WHERE name='Kylian Mbappe'), (SELECT team_id FROM teams WHERE name='PSG'), (SELECT team_id FROM teams WHERE name='Real Madrid'), 180000000, '2025-07-01', '2025/26'),
((SELECT player_id FROM players WHERE name='Neymar Jr'), (SELECT team_id FROM teams WHERE name='PSG'), (SELECT team_id FROM teams WHERE name='Santos'), 0, '2025-01-15', '2024/25'),
((SELECT player_id FROM players WHERE name='Cristiano Ronaldo'), NULL, (SELECT team_id FROM teams WHERE name='Al Nassr'), 0, '2023-01-01', '2022/23'),
((SELECT player_id FROM players WHERE name='Lionel Messi'), NULL, (SELECT team_id FROM teams WHERE name='Inter Miami'), 0, '2023-07-15', '2023/24');

SELECT CONCAT('Teams: ', COUNT(*)) AS count FROM teams;
SELECT CONCAT('Players: ', COUNT(*)) AS count FROM players;
SELECT CONCAT('Transfers: ', COUNT(*)) AS count FROM transfers;
SELECT name, appearances, goals_scored, assists, special_ability, rating
FROM players
ORDER BY goals_scored DESC, assists DESC
LIMIT 10;

-- ─────────────────────────────────────────────────────────────────────────
-- DATA CLEANUP (Avoid Null-looking Profiles + Sync Engagement Counters)
-- ─────────────────────────────────────────────────────────────────────────
UPDATE users
SET
    full_name = COALESCE(NULLIF(TRIM(full_name), ''), REPLACE(username, '_', ' ')),
    bio = COALESCE(NULLIF(TRIM(bio), ''), 'Passionate football fan.'),
    favorite_team = COALESCE(NULLIF(TRIM(favorite_team), ''), 'Football Club'),
    profile_image_url = COALESCE(
        NULLIF(TRIM(profile_image_url), ''),
        CONCAT('https://ui-avatars.com/api/?name=', REPLACE(username, '_', '+'), '&background=0f172a&color=ffffff&bold=true')
    );

UPDATE posts p
SET
    likes_count = (SELECT COUNT(*) FROM post_likes pl WHERE pl.post_id = p.id),
    comments_count = (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id);

-- ═══════════════════════════════════════════════════════════════════════════
-- SAMPLE DATA INSERTED SUCCESSFULLY
-- ═══════════════════════════════════════════════════════════════════════════

SELECT 'Sample data inserted successfully!' AS message;
SELECT CONCAT('Users: ', COUNT(*)) AS count FROM users;
SELECT CONCAT('Posts: ', COUNT(*)) AS count FROM posts;
SELECT CONCAT('Comments: ', COUNT(*)) AS count FROM comments;
SELECT CONCAT('Likes: ', COUNT(*)) AS count FROM post_likes;
