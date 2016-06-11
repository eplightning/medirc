package org.eplight.medirc.server.image.fragments;

import com.sun.media.sound.InvalidFormatException;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.server.user.User;

public class ImageFragmentFactory {

    public static ImageFragment create(int id, User user, SessionBasic.ImageFragment msg) throws InvalidFormatException {
        switch (msg.getFragCase()) {
            case RECT:
                RectImageFragment frag = new RectImageFragment(id, user);
                frag.fromProtobuf(msg.getRect());

                return frag;
        }

        throw new InvalidFormatException("Unknown fragment type");
    }
}
