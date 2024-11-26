package com.example.socialnetworkgui.Domain.Validators;

import com.example.socialnetworkgui.Domain.Exceptions.ValidationException;
import com.example.socialnetworkgui.Domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity==null)
            throw new ValidationException("We don't have this friendship");
        if(entity.getFirst()==null)
            throw new ValidationException("User 1 is null");
        if(entity.getSecond()==null)
            throw new ValidationException("User 2 is null");
        if(entity.getFirst()==entity.getSecond()){
            throw new ValidationException("You can't be your own friend!");
        }
    }
}
