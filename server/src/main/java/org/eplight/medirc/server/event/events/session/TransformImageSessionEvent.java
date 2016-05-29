package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.image.transformations.ImageTransformations;
import org.eplight.medirc.server.session.Session;

public class TransformImageSessionEvent extends AbstractSessionEvent {

    private Image img;

    public TransformImageSessionEvent(Session session, Object cause, Image img) {
        super(session, cause);
        this.img = img;
    }

    public TransformImageSessionEvent(Session session, Image img) {
        super(session);
        this.img = img;
    }

    public Image getImg() {
        return img;
    }
}
