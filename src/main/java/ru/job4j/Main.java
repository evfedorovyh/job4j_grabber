package ru.job4j;

import org.apache.log4j.Logger;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.HabrCareerParse;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        var config = new Config();
        config.load("application.properties");
        Class.forName(config.get("db.driver-class-name"));
        try (var connection = DriverManager.getConnection(config.get("db.url"),
                config.get("db.username"),
                config.get("db.password"))) {
            var store = new JdbcStore(connection);
            HabrCareerParse parser = new HabrCareerParse();
            List<Post> listParse = parser.fetch();
            for (Post post : listParse) {
                store.save(post);
            }
/*            var post = new Post();
            post.setTitle("Super Java Job");
            post.setTime(System.currentTimeMillis());
            store.save(post);*/

            var scheduler = new SchedulerManager();
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store);
        } catch (SQLException e) {
            LOGGER.error("When create a connection", e);
        }
    }
}
