package ru.geekbrains.erpsystem.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.geekbrains.erpsystem.data.UserData;

import java.util.Optional;

public interface UserService extends CrudService<UserData, Long>, UserDetailsService {
    Optional<UserData> getByName(String name);
}
