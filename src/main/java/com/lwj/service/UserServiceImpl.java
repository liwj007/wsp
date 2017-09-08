package com.lwj.service;

import com.lwj.entity.User;
import com.lwj.repository.UserRepository;
import com.lwj.status.CommonVariable;
import com.lwj.status.Rights;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by liwj0 on 2017/7/17.
 */
@Service
public class UserServiceImpl implements IUserService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;

    public User getUserByName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User getUserByToken(String token) {
        return userRepository.findByToken(token);
    }

    @Override
    public void logout(User user) {
        user.setToken(null);
        user.setExpire(null);
        user.setApiToken(null);
        userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }


    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    private String generateToken(User user) {
        String token = user.getUserName() + UUID.randomUUID().toString().replace("-", "");
        return token;
    }


    public String addNewUser(User user, String token, int right) {
        User tmp = userRepository.findByUserName(user.getUserName());
        if (tmp == null) {
            Date date = new Date();
            user.setApiToken(token);
            user.setToken(generateToken(user));
            user.setExpire(date.getTime() + CommonVariable.tokenOutPeriod);
            user.setUserRight(right);
            userRepository.save(user);
        } else {
            Date date = new Date();
            user.setId(tmp.getId());
            if (tmp.getExpire() == null || date.getTime() > tmp.getExpire())
                user.setToken(generateToken(user));
            else{
                user.setToken(tmp.getToken());
            }
            user.setApiToken(token);
            user.setUserRight(right);
            user.setUnit(tmp.getUnit());
            user.setExpire(date.getTime() + CommonVariable.tokenOutPeriod);
            userRepository.save(user);
        }
        return user.getToken();
    }

}
