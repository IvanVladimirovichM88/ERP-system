package ru.geekbrains.erpsystem.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.geekbrains.erpsystem.data.UserData;

public interface UserService extends CrudService<UserData, Long>, UserDetailsService {
    UserData getByUserName(String userName);
}
