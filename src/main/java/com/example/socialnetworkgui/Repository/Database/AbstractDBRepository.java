package com.example.socialnetworkgui.Repository.Database;

import com.example.socialnetworkgui.Domain.Entity;
import com.example.socialnetworkgui.Domain.Validators.Validator;
import com.example.socialnetworkgui.Repository.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.sql.*;
import java.util.Set;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements Repository<ID, E>{

    private final Validator<E> validator;
    protected final String url;
    protected final String username;
    protected final String password;


    public AbstractDBRepository(Validator<E> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    protected abstract E createEntityFromResultSet(ResultSet resultSet) throws SQLException;

    protected abstract String getTableName();

    protected abstract String getTableInsertValuesSQL(E Entity);

    protected abstract String getSQLIdForEntityId(ID id);

    protected abstract String getSQLValuesForEntity(E entity);

    protected abstract String getSQLValuesForEntityUpdate(E entity);
    /**
     * @param id - long, the id of a user to found
     * @return Optional<User> - the user with the given id
     *                        -Optional.empty() otherwise
     */

    @Override
    public Optional<E> findOne(ID id) {
        E entity;
        String query = "select * from "+ getTableName()+" where " + getSQLIdForEntityId(id);
        try(Connection connection = DriverManager.getConnection(url, username, password);
            ResultSet resultSet = connection.createStatement().executeQuery(String.format(query))) {
            if(resultSet.next()){
                entity = createEntityFromResultSet(resultSet);
                return Optional.ofNullable(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<E> findAll() {
        Set<E> entities = new HashSet<>();
        String query = "select * from "+ getTableName();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                E entity = createEntityFromResultSet(resultSet);
                entities.add(entity);
            }
            return entities;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITY must not be null");
        }
        validator.validate(entity);
        String query = "INSERT INTO " + getTableInsertValuesSQL(entity) + " VALUES " + getSQLValuesForEntity(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
             statement.executeUpdate();
        } catch (SQLException e) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        Optional<E> entity = findOne(id);
        String query = "DELETE FROM " + getTableName() + " WHERE " + getSQLIdForEntityId(id);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
             statement.executeUpdate();
        } catch (SQLException e) {
            return Optional.empty();
        }
        return entity;
    }

    @Override
    public Optional<E> update(E entity) {
            if (entity == null) {
                throw new IllegalArgumentException("ENTITY must not be null");
            }
            validator.validate(entity);
            String query = "UPDATE " + getTableName() + " SET " + getSQLValuesForEntityUpdate(entity) +
                    " WHERE " + getSQLIdForEntityId(entity.getId());
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                return Optional.of(entity);
            }
            return Optional.empty();

    }


}
