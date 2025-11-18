package database.migrations;

import database.Migration;

public class Migration0002 extends Migration {

    @Override
    public String up() {
        return """
                    set @column_exists := (
                        select count(1)
                        from information_schema.columns
                        where table_schema = database()
                          and table_name = 'games'
                          and column_name = 'isOver'
                      );

                      set @sql := if(@column_exists = 0,
                                     'alter table games add column isOver tinyint(1) default 0',
                                     'select 1');

                      prepare statement from @sql;
                      execute statement;
                      deallocate prepare statement;
                """;
    }

}
