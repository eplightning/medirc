package org.eplight.medirc.server.user.factory;

import org.eplight.medirc.server.user.AbstractUser;
import org.eplight.medirc.server.user.User;

public class HardcodedUserRepository implements UserRepository {

    @Override
    public User findById(int id) {
        if (id == 1)
            return new AbstractUser(1, "Testowy1");

        if (id == 2)
            return new AbstractUser(2, "Testowy2");

        return null;
    }

    @Override
    public User findByName(String name) {
        if (name.equals("Testowy1")) {
            return new AbstractUser(1, "Testowy1");
        } else if (name.equals("Testowy2")) {
            return new AbstractUser(2, "Testowy2");
        }

        return null;
    }
}
