package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.user.User;

/**
 * Created by EpLightning on 11.06.2016.
 */
public class ClearImageFragmentsEvent extends AbstractSessionEvent {

    private Image img;
    private User user;

    public ClearImageFragmentsEvent(Session session, Object cause, Image img, User user) {
        super(session, cause);
        this.img = img;
        this.user = user;
    }

    public ClearImageFragmentsEvent(Session session, Image img, User user) {
        super(session);
        this.img = img;
        this.user = user;
    }

    public Image getImg() {
        return img;
    }

    public User getUser() {
        return user;
    }
}
