package com.example.socialnetworkgui.Domain.Validators;

import com.example.socialnetworkgui.Domain.Exceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
