package service.user;

import config.Config;
import model.Role;
import model.RoleName;
import model.User;
import service.role.IRoleService;
import service.role.RoleNameService;

import java.util.*;

public class UserService implements IUser {
    public static List<User> users = new ArrayList<>();

    List<User> userList = new Config<User>().readFromFile(Config.FILE_USER_PATH);
    List<User> loginAccount = new Config<User>().readFromFile(Config.FILE_LOGIN_PATH);
    IRoleService roleService = new RoleNameService();


    @Override
    public List findAll() {
        return userList;
    }

    @Override
    public void save(User account) {
        if (findById(account.getId()) != null) {
            userList.set(userList.indexOf(findById(account.getId())), account);
        } else {
            if (userList == null) {
                userList = new ArrayList<>();
                userList.add(account);
            } else {
                userList.add(account);
            }
        }
        new Config<User>().writeToFile(Config.FILE_USER_PATH, userList);
    }

    @Override
    public User findById(int id) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId() == id) {
                return userList.get(i);
            }
        }
        return null;
    }

    @Override
    public void delete(int id) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId() == id) {
                userList.remove(i);
            }
        }
        new Config<User>().writeToFile(Config.FILE_USER_PATH, userList);
    }

    @Override
    public boolean existedByUserName(String userName) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existedByEmail(String email) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean checkLogin(String userName, String password) {
        List<User> userLogin = new Config<User>().readFromFile(Config.FILE_LOGIN_PATH);

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserName().equals(userName) && userList.get(i).getPassword().equals(password)) {
                userLogin.add(userList.get(i));
                new Config<User>().writeToFile(Config.FILE_LOGIN_PATH, userLogin);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkAccountActiveStatus(String userName, String password) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserName().equals(userName) && userList.get(i).getPassword().equals(password)) {
                if (userList.get(i).isActiveStatus() == true) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public User getCurrentUser() {
        if (new Config<User>().readFromFile(Config.FILE_LOGIN_PATH).size() != 0) {
            return new Config<User>().readFromFile(Config.FILE_LOGIN_PATH).get(0);
        }
        return null;
    }

    @Override
    public List<User> getLoverList() {
        List<User> loverList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            Set<Role> roleSet = userList.get(i).getRoles();
            List<Role> roles = new ArrayList<>(roleSet);
            if (roles.get(0).getName() == RoleName.LOVER) {
                loverList.add(userList.get(i));
            }
        }
        return loverList;
    }

    @Override
    public List<User> findTopFiveLover() {
        List<User> topFiveLover = new ArrayList<>(getLoverList());
        for (int i = 0; i < topFiveLover.size() - 1; i++) {
            User currentMinValue = topFiveLover.get(i);
            int currentMinIndex = i;
            for (int j = i + 1; j < topFiveLover.size(); j++) {
                if (currentMinValue.getRentCount() > topFiveLover.get(j).getRentCount()) {
                    currentMinValue = topFiveLover.get(j);
                    currentMinIndex = j;
                }
            }
            if (currentMinIndex != i) {
                topFiveLover.set(currentMinIndex, topFiveLover.get(i));
                topFiveLover.set(i, currentMinValue);
            }
        }
        return topFiveLover;
    }

    @Override
    public boolean checkSelectedUser(int choice) {
        User pickedLover = findById(choice);
        for (int i = 0; i < pickedLover.getUserList().size(); i++) {
            if (pickedLover.getUserList().get(i).getId() == loginAccount.get(0).getId()) {
                return false;
            }
        }
        return true;
    }
}


