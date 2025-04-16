package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.stores.Store;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;
import java.util.List;

public class SuperJobGrab implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var store = (Store) context.getJobDetail().getJobDataMap().get("store");
        HabrCareerParse parser = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> listParse = parser.list("https://career.habr.com");
        for (Post post : listParse) {
            store.save(post);
        }
/*        var config = new Config();
        config.load("application.properties");
        try (var connection = DriverManager.getConnection(config.get("db.url"),
                config.get("db.username"),
                config.get("db.password"))) {
            Store store = new JdbcStore(connection);
            HabrCareerParse parser = new HabrCareerParse(new HabrCareerDateTimeParser());
            List<Post> listParse = parser.list("https://career.habr.com");
            for (Post post : listParse) {
                store.save(post);
            }
        } catch (SQLException e) {
            LOGGER.error("When creating a connection", e);
        }*/
    }
}
