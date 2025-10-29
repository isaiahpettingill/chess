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
        create unique index index_unique_user_username on users(username);

        create table if not exists authTokens(
            tokenId integer not null auto_increment,
            username varchar(2048) not null,
            token char(36) not null,
            primary key (tokenId)
        );
        
        create unique index index_unique_token on authTokens(token);

        create table if not exists games(
            gameId integer not null auto_increment,
            gameName varchar(4096) not null,
            whiteUsername varchar(4096) null,
            blackUsername varchar(4096) null,
            game text,
            primary key (gameId)
        );
        """;
    }
    
}
