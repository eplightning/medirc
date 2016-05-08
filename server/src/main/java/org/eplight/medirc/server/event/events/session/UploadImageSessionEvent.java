package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.session.Session;

/**
 * Created by EpLightning on 08.05.2016.
 */
public class UploadImageSessionEvent extends AbstractSessionEvent {

    private Image img;

    public UploadImageSessionEvent(Session session, Object cause, Image img) {
        super(session, cause);
        this.img = img;
    }

    public UploadImageSessionEvent(Session session, Image img) {
        super(session);
        this.img = img;
    }

    public Image getImg() {
        return img;
    }
}
