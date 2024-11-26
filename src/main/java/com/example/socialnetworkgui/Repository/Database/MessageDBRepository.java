package com.example.socialnetworkgui.Repository.Database;

import com.example.socialnetworkgui.Domain.Entity;
import com.example.socialnetworkgui.Domain.Message;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Domain.Validators.Validator;
import com.example.socialnetworkgui.Repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepository extends AbstractDBRepository<Long, Message> {
    private String tableName;
    private Repository<Long, User> userDBRepository;


    public MessageDBRepository(Validator<Message> validator, String url, String username, String password, Repository<Long, User> userDBRepository) {
        super(validator, url, username, password);
        this.tableName = "messages";
        this.userDBRepository = userDBRepository;
    }

    @Override
    protected Message createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.parse(resultSet.getString("created_at"));
        Message m = new Message(
                userDBRepository.findOne(resultSet.getLong("sender_id")).get(),
                userDBRepository.findOne(resultSet.getLong("receiver_id")).get(),
                resultSet.getString("message_string"),
                time,
                resultSet.getInt("replied_at"));
        m.setId(resultSet.getLong("id"));
        return m;
    }

    @Override
    protected String getTableName() {
        return this.tableName;
    }

    @Override
    protected String getSQLIdForEntityId(Long aLong) {
        return "id =" + aLong;
    }

    @Override
    protected String getSQLValuesForEntity(Message entity) {
        List<User> users = entity.getTo();
        return "("+ entity.getId()+", "+ entity.getFrom().getId()+", "+users.get(0).getId()+", '"+entity.getMessage()+"', '"+entity.getDate().toString()+"', "+ entity.getReplay()+")";
    }

    @Override
    protected String getSQLValuesForEntityUpdate(Message entity) {
        return "";
    }


}
