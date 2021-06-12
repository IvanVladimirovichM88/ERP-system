package ru.geekbrains.erpsystem.services.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.geekbrains.erpsystem.data.UserData;
import ru.geekbrains.erpsystem.entities.User;
import ru.geekbrains.erpsystem.repositories.RoleRepository;
import ru.geekbrains.erpsystem.repositories.UserRepository;
import ru.geekbrains.erpsystem.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserData insert(UserData userData) {
        if (userData.getId() != null){
            throw new RuntimeException("User with id - "+userData.getId() + "is not empty");
        }

        User user = userData.getEntity();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return new UserData( userRepository.save(user) );
    }

    @Override
    public UserData update(UserData userData) {
        User existingUser = userRepository.findById( userData.getId()).orElse(null);
        if (existingUser == null){
            throw new RuntimeException("User with id - "+userData.getId() + "is empty");
        }

        User user  = userData.getEntity();
        user.setPassword(existingUser.getPassword());

        return new UserData( userRepository.save(user) );
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("no User with id - "+id)));
    }

    @Override
    public Optional<UserData> getById(Long id) {
        return userRepository.findById(id).map(UserData::new);
    }

    @Override
    public List<UserData> getAll() {
        return userRepository.findAll().stream()
                .map(UserData::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByName(name).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username %s not found", name));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }

    @Override
    public Optional<UserData> getByName(String name) {
        return userRepository.findByName(name).map(UserData::new);
    }
}
