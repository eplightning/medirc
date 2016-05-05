package org.eplight.medirc.server.user.factory;

import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;

/**
 * Created by EpLightning on 27.03.2016.
 */
public interface UserRepository {

    User create(int id);
}
