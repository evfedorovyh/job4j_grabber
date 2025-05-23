package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HabrCareerParse implements Parse {
    private static final Logger LOGGER = Logger.getLogger(HabrCareerParse.class);

    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGE_COUNT = 5;

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        final String sourceLink = link;
        var result = new ArrayList<Post>();
        try {
            for (int pageNumber = 1; pageNumber <= PAGE_COUNT; pageNumber++) {
                String fullLink = "%s%s%d%s".formatted(sourceLink, PREFIX, pageNumber, SUFFIX);
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    var linkElement = titleElement.child(0);
                    var dateElement = row.select(".vacancy-card__date").first();
                    String vacancyName = titleElement.text();
                    String linkName = String.format("%s%s", sourceLink,
                            linkElement.attr("href"));
                    String date = dateElement.child(0).attr("datetime");
                    Long dateTime = dateTimeParser.parse(date).toInstant(ZoneOffset.UTC).toEpochMilli();
                    var post = new Post();
                    post.setTitle(vacancyName);
                    post.setLink(linkName);
                    post.setDescription(retrieveDescription(linkName));
                    post.setTime(dateTime);
                    result.add(post);
                });
            }
        } catch (IOException e) {
            LOGGER.error("When load page", e);
        }
        return result;
    }

    private String retrieveDescription(String link) {
        StringJoiner result = new StringJoiner(System.lineSeparator());
        try {
            var connection = Jsoup.connect(link);
            var document = connection.get();
            var rows = document.select(".vacancy-description__text").first().children();
            for (Element element : rows) {
                result.add(element.text());
            }
        } catch (IOException e) {
            LOGGER.error("When load page", e);
        }
        return result.toString();
    }
}
