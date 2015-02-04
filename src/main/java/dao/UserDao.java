package dao;

import table.User;

import java.util.List;

/**
 * Created by Александр on 27.10.14.
 */
public interface UserDao {

    public void addUser(User user);
    public void updateUser(User user);
    public void deleteUser(int id);
    public List<User> getAllUsers();

}
