package ru.job4j.grabber.stores;

import org.apache.log4j.Logger;
import ru.job4j.grabber.model.Post;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcStore implements Store {
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(JdbcStore.class);

    public JdbcStore(Connection connection) {
        this.connection = connection;
        init();
    }

    private void init() {
        try (BufferedReader reader = new BufferedReader(new FileReader("db/init.sql"))) {
            String sql = reader.lines().collect(Collectors.joining());
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            } catch (SQLException se) {
                LOGGER.error("When init SQL execute: ", se);
            }
        } catch (IOException io) {
            LOGGER.error("When load file: \"db/init.sql\"", io);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     connection.prepareStatement("INSERT INTO post(title, link, description, time) VALUES (?, ?, ?, ?)"
                             + "ON CONFLICT (link) DO NOTHING")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, new Timestamp(post.getTime()));
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        try (Statement statement =
                     connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM post");
            while (resultSet.next()) {
                result.add(doNewPost(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Optional<Post> findById(Long id) {
        Post result = null;
        try (PreparedStatement statement =
                     connection.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = doNewPost(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(result);
    }

    private Post doNewPost(ResultSet resultSet) throws SQLException {
        return new Post(resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getTimestamp(5).getTime());
    }

    @Override
    public void clearAll() {
        try (Statement statement =
                     connection.createStatement()) {
            statement.executeQuery("DELETE FROM post");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
