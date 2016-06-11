package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.image.fragments.ImageFragment;
import org.eplight.medirc.server.session.Session;

public class AddImageFragmentSessionEvent extends AbstractSessionEvent {

    private Image img;
    private ImageFragment imgFragment;

    public AddImageFragmentSessionEvent(Session session, Object cause, Image img, ImageFragment imgFragment) {
        super(session, cause);
        this.img = img;
        this.imgFragment = imgFragment;
    }

    public AddImageFragmentSessionEvent(Session session, Image img, ImageFragment imgFragment) {
        super(session);
        this.img = img;
        this.imgFragment = imgFragment;
    }

    public Image getImg() {
        return img;
    }

    public ImageFragment getImgFragment() {
        return imgFragment;
    }
}
