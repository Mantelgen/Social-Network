package com.example.socialnetworkgui.Domain.Validators;

import com.example.socialnetworkgui.Domain.Exceptions.ValidationException;
import com.example.socialnetworkgui.Domain.User;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        if(entity==null)
            throw new ValidationException("User is null");
        validateFirstName(entity.getFirstName());
        validateLastName(entity.getLastName());
        validateEmail(entity.getEmail());
        validatePassword(entity.getPassword());
    }

    private void validatePassword(String password) {
        if(password==null)
            throw new ValidationException("Password cannot be null");
        if(password.isEmpty())
            throw new ValidationException("Password cannot be empty");
    }



    private void validateEmail(String email) {
        if(email == null)
            throw new ValidationException("Email must not be null!");
        else if(email.length() >= 100)
            throw new ValidationException("Email too long");
        else if(email.isEmpty())
            throw new ValidationException("Email must not be empty");
        else if(email.chars().filter(ch -> ch == '@').count() != 1)
            throw new ValidationException("Wrong email");
    }

    private void validateLastName(String lastName) {
        if(lastName == null)
            throw new ValidationException("First Name must not be null!");
        else if(lastName.length() >= 100)
            throw new ValidationException("First Name too long");
        else if(lastName.isEmpty())
            throw new ValidationException("First Name must not be empty");
        else if(! Character.isAlphabetic(lastName.charAt(0)))
            throw new ValidationException("First letter of the last name must be a letter");
    }

    private void validateFirstName(String firstName) {
        if(firstName == null)
            throw new ValidationException("First Name must not be null!");
        else if(firstName.length() >= 100)
            throw new ValidationException("First Name too long");
        else if(firstName.isEmpty())
            throw new ValidationException("First Name must not be empty");
        else if(! Character.isAlphabetic(firstName.charAt(0)))
            throw new ValidationException("First letter of the first name must be a letter");
    }
}
