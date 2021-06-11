package ru.geekbrains.erpsystem.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.erpsystem.entities.Role;
import ru.geekbrains.erpsystem.entities.User;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserData implements Serializable {
    private Long id;
    private String name;
    private RoleData roleData;
    private String password;


    public UserData(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.roleData = new RoleData(user.getRole());
        this.password = user.getPassword();
    }

    public User getRoleEntity() {

        User newUser = new User();
        newUser.setId(this.id);
        newUser.setName(this.name);
        newUser.setRole( this.roleData.getRoleEntity() );

        return newUser;
    }

    public User getEntity() {
        Role role = this.getRoleData().getRoleEntity();
        User user = new User();
        user.setId(this.getId());
        user.setName(this.getName());
        user.setRole(role);
        user.setPassword(this.getPassword());
        return user;
    }

}
