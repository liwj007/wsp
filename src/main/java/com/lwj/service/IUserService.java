package com.lwj.service;

import com.lwj.entity.User;
import com.lwj.status.Rights;

import java.util.List;

/**
 * Created by liwj0 on 2017/7/17.
 */
public interface IUserService {
    User getUserById(Long id);

    void updateUser(User user);

    String addNewUser(User user, String token, int rights);

    User getUserByName(String userName);

    User getUserByToken(String token);

    void logout(User user);

    List<User> getAll();

}
