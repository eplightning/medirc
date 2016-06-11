package org.eplight.medirc.server.image.fragments;

import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.server.user.User;

public interface ImageFragment {

    int getId();
    User getUser();

    SessionBasic.ImageFragment toProtobuf();
}
