package org.eplight.medirc.server.image.transformations;

import org.eplight.medirc.protocol.SessionBasic;

public interface ImageFragment {

    int getId();

    SessionBasic.ImageFragment toProtobuf();
}
