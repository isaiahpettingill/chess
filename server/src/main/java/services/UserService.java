package services;

import dataaccess.Repository;
import dto.RegisterPayload;
import models.User;

public final class UserService implements Service {
    private Repository<User, Long> _userRepository;

    public UserService(Repository<User, Long> userRepository) {
        _userRepository = userRepository;
    }

    public boolean isAlreadyTaken(String username) {
        return _userRepository.exists(x -> username.equals(x.username()));
    }

    public void saveUser(RegisterPayload user) {
        _userRepository.upsert(
                new User(null,
                        user.username(),
                        user.password().hashCode()+"",
                        user.email()));
    }
}
