package org.eplight.medirc.server.user.factory;

import org.eplight.medirc.server.user.User;

/**
 * Created by EpLightning on 27.03.2016.
 */
public interface UserFactory {

    User create(int id);
}
