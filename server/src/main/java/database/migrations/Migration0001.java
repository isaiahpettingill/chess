package database.migrations;

import database.Migration;

public class Migration0001 extends Migration {
    @Override
    public String up() {
        return """
        create table if not exists users(
            userId integer not null auto_increment,
            username varchar(2048),
            passwordHash varchar(1024),
            emailAddress varchar(512),
            primary key(userId)
        );
        create unique index index_unique_user_username on users(emailAddress);

        create table if not exists authTokens(
            tokenId integer not null auto_increment,
            userId integer not null,
            token varchar(36) not null,
            created timestamp,
            primary key (tokenId),
            constraint auth_token_user
                foreign key (userId) references users (userId)
                on delete cascade,
        );
        create unique index index_unique_token on authTokens(token);

        create table if not exists games(
            gameId integer not null auto_increment,
            gameName varchar(4096) not null,
            whiteUserId integer null,
            blackUserId integer null,
            game text,
            primary key (gameId),
            constraint game_white_user 
                foreign key (whiteUserId) references users (userId)
                on delete set null,
            constraint game_black_user
                foreign key (blackUserId) references users (userId)
                on delete set null
        );
        """;
    }

    @Override
    public String down() {
        return """
        drop table games;
        drop index index_unique_token;
        drop table authTokens;
        drop index index_unique_user_username;
        drop table users;
        """;
    }
    
}
