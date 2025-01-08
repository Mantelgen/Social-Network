package com.example.socialnetworkgui.Repository.Database.Paging;

import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.FriendshipStatus;
import com.example.socialnetworkgui.Domain.Tuple;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Domain.Validators.Validator;
import com.example.socialnetworkgui.Utils.paging.Page;
import com.example.socialnetworkgui.Utils.paging.Pageable;
import javafx.util.Pair;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FriendshipDBPagingRepository implements FriendshipRepository{

    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDBPagingRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    public FriendshipDBPagingRepository(Validator<Friendship> validator, String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
        validator = validator;
    }
    @Override
    public List<Integer> getYears() {
        return List.of();
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable, FriendshipDTO friendshipFilter) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int totalNumberOffriendships = count(connection, friendshipFilter);
            List<Friendship> friendshipsOnPage;
            if (totalNumberOffriendships > 0) {
                friendshipsOnPage = findAllOnPage(connection, pageable, friendshipFilter);
            } else {
                friendshipsOnPage = new ArrayList<>();
            }
            return new Page<>(friendshipsOnPage, totalNumberOffriendships);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private List<Friendship> findAllOnPage(Connection connection, Pageable pageable, FriendshipDTO filter) throws SQLException {
        List<Friendship> friendshipsOnPage = new ArrayList<>();
        // Using StringBuilder rather than "+" operator for concatenating Strings is more performant
        // since Strings are immutable, so every operation applied on a String will create a new String
        String sql = "select * from friendships";
        Tuple<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getLeft().isEmpty()) {
            sql += " where " + sqlFilter.getLeft();
        }
        sql += " limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getRight()) {
                statement.setObject(++paramIndex, param);
            }
            statement.setInt(++paramIndex, pageable.getPageSize());
            statement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long user1Id = resultSet.getLong("user1");
                    Long user2ID = resultSet.getLong("user2");
                    LocalDateTime timestamp = resultSet.getTimestamp("friends_from").toLocalDateTime();
                    FriendshipStatus status = FriendshipStatus.valueOf(resultSet.getString("status"));
                    Friendship friend = new Friendship(new Tuple<>(user1Id,user2ID),timestamp,status);
                    friendshipsOnPage.add(friend);
                }
            }
        }
        return friendshipsOnPage;
    }
    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        return findAllOnPage(pageable, null);
    }
    private Optional<Friendship> findOne(Connection connection, Tuple<Long, Long> id) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement("select * from friendships where (user1, user2) =(?, ?)")) {
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long user1Id = resultSet.getLong("user1");
                Long user2ID = resultSet.getLong("user2");
                LocalDateTime timestamp = resultSet.getTimestamp("friends_from").toLocalDateTime();
                FriendshipStatus status = FriendshipStatus.valueOf(resultSet.getString("status"));
                Friendship friend = new Friendship(new Tuple<>(user1Id,user2ID),timestamp,status);
                return Optional.of(friend);
            }
            return Optional.empty();
        }
    }
    @Override
    public Optional<Friendship> findOne( Tuple<Long, Long> longLongTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            return findOne(connection, longLongTuple);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long user1Id = resultSet.getLong("user1");
                Long user2ID = resultSet.getLong("user2");
                LocalDateTime timestamp = resultSet.getTimestamp("friends_from").toLocalDateTime();
                FriendshipStatus status = FriendshipStatus.valueOf(resultSet.getString("status"));
                Friendship friend = new Friendship(new Tuple<>(user1Id,user2ID),timestamp,status);
                friendships.add(friend);
            }
            return friendships;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        String insertSQL = "insert into friendships (user1, user2, friends_from , status) values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setLong(1, entity.getFirst());
            statement.setLong(2, entity.getSecond());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getStatus().toString());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> longLongTuple) {
        if (longLongTuple == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        String deleteSQL = "delete from friendships where (user1, user2) = (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setLong(1, longLongTuple.getLeft());
            statement.setLong(2, longLongTuple.getRight());
            Optional<Friendship> foundUser = findOne(connection, longLongTuple);
            int response = 0;
            if (foundUser.isPresent()) {
                response = statement.executeUpdate();
            }
            return response == 0 ? Optional.empty() : foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        String updateSQL = "update friendships set  friends_from = ? , status = ? where (user1, user2) = (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(updateSQL);) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setString(2, entity.getStatus().toString());
            statement.setLong(3, entity.getFirst());
            statement.setLong(4, entity.getSecond());

            int response = statement.executeUpdate();
            return response == 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private Tuple<String, List<Object>> toSql(FriendshipDTO filter) {
        if (filter == null) {
            return new Tuple<>("", Collections.emptyList());
        }
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if(filter.getUser().isPresent() && filter.getUser2().isPresent() && filter.getUser().get().equals(filter.getUser2().get())) {
            conditions.add("user1 =? or user2 = ?");
            params.add(filter.getUser().get());
            params.add(filter.getUser2().get());
        }
        else {
            filter.getUser().ifPresent(filtr->{
                conditions.add("user1= ?");
                params.add(filtr);

            });
            filter.getUser2().ifPresent(filtr->{
                conditions.add("user2= ?");
                params.add(filtr);
            });
        }
        filter.getStatus().ifPresent(filtr->{
            conditions.add("status= ?");
            params.add(filtr.toString());
        });
        String sql = String.join(" and ",conditions);
        return new Tuple<>(sql, params);
    }

    private int count(Connection connection, FriendshipDTO filter) throws SQLException {
        String sql = "select count(*) as count from friendships";
        Tuple<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getLeft().isEmpty()) {
            sql += " where " + sqlFilter.getLeft();
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getRight()) {
                statement.setObject(++paramIndex, param);
            }
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOffriendships = 0;
                if (result.next()) {
                    totalNumberOffriendships = result.getInt("count");
                }
                return totalNumberOffriendships;
            }
        }
    }
}
