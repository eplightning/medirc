package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.session.Session;

public class FocusImageSessionEvent extends AbstractSessionEvent {

    private Image img;

    public FocusImageSessionEvent(Session session, Object cause, Image img) {
        super(session, cause);
        this.img = img;
    }

    public FocusImageSessionEvent(Session session, Image img) {
        super(session);
        this.img = img;
    }

    public Image getImg() {
        return img;
    }
}
