package org.eplight.medirc.server.image.fragments;

import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.server.user.User;

public class ImageFragmentFactory {

    public static ImageFragment create(int id, User user, SessionBasic.ImageFragment msg) throws UnsupportedOperationException {
        switch (msg.getFragCase()) {
            case RECT:
                RectImageFragment frag = new RectImageFragment(id, user);
                frag.fromProtobuf(msg.getRect());

                return frag;
        }

        throw new UnsupportedOperationException("Unknown fragment type");
    }
}
