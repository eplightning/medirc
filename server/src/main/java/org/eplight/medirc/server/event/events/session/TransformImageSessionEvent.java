package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.image.ImageTransformations;
import org.eplight.medirc.server.session.Session;

public class TransformImageSessionEvent extends AbstractSessionEvent {

    private Image img;
    private ImageTransformations transformations;

    public TransformImageSessionEvent(Session session, Object cause, Image img, ImageTransformations transformations) {
        super(session, cause);
        this.img = img;
        this.transformations = transformations;
    }

    public TransformImageSessionEvent(Session session, Image img, ImageTransformations transformations) {
        super(session);
        this.img = img;
        this.transformations = transformations;
    }

    public Image getImg() {
        return img;
    }

    public ImageTransformations getTransformations() {
        return transformations;
    }
}
