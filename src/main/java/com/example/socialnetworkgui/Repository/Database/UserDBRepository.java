package com.example.socialnetworkgui.Repository.Database;

import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Domain.Validators.Validator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserDBRepository extends AbstractDBRepository<Long, User> {

    private final String tableName;

    public UserDBRepository(Validator<User> validator, String url, String username, String password) {
        super(validator, url, username, password);
        this.tableName = "users";
    }

    @Override
    protected User createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Long id = resultSet.getLong("id");
        String firstName = resultSet.getString("firstName");
        String lastName = resultSet.getString("lastName");
        String email = resultSet.getString("email");
        LocalDate birthDate = LocalDate.parse(resultSet.getString("birthday"),formatter);
        String password = resultSet.getString("password");

        User user = new User(firstName,lastName,birthDate,email,password);
        user.setId(id);

        return user;
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
    protected String getSQLValuesForEntity(User entity) {
        return "("+ entity.getId()+", '"+ entity.getFirstName()+"', '"+ entity.getLastName()+
                "', '"+entity.getBirthDate().toString()+"', '"+entity.getEmail()+"', '"+entity.getPassword()+"')";
    }

    @Override
    protected String getSQLValuesForEntityUpdate(User entity) {
        return "";
    }
}
