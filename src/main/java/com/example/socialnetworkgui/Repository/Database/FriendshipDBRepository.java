package com.example.socialnetworkgui.Repository.Database;

import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.FriendshipStatus;
import com.example.socialnetworkgui.Domain.Tuple;
import com.example.socialnetworkgui.Domain.Validators.Validator;
import com.example.socialnetworkgui.Service.FriendshipService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FriendshipDBRepository extends AbstractDBRepository<Tuple<Long,Long>, Friendship> {

    private final String tableName;

    public FriendshipDBRepository(Validator<Friendship> validator, String url, String username, String password) {
        super(validator, url, username, password);
        this.tableName = "friendships";
    }

    @Override
    protected Friendship createEntityFromResultSet(ResultSet resultSet) throws SQLException
    {
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Long user1Id = resultSet.getLong("user1");
        Long user2ID = resultSet.getLong("user2");
        LocalDateTime timestamp = LocalDateTime.parse(resultSet.getString("friendsfrom"));
        FriendshipStatus status = FriendshipStatus.valueOf(resultSet.getString("status"));
        Friendship friend = new Friendship(new Tuple<>(user1Id,user2ID),timestamp,status);
        return friend;
    }

    @Override
    protected String getTableName() {
        return this.tableName;
    }

    @Override
    protected String getSQLIdForEntityId(Tuple<Long, Long> id) {
        return "(user1,user2) = (" + id.getLeft() + ", " + id.getRight() + ")";
    }


    @Override
    protected String getSQLValuesForEntity(Friendship entity) {
        return "("+entity.getFirst()+","+entity.getSecond()+", '"+entity.getDate().toString()+"', '"+entity.getStatus()+"')";
    }

    @Override
    protected String getSQLValuesForEntityUpdate(Friendship entity){
        return "user1="+entity.getFirst()+", user2="+entity.getSecond()+",friendsfrom='"+entity.getDate().toString()+"',status='"+entity.getStatus()+"'";
    }
}
