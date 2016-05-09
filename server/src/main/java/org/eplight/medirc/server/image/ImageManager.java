package org.eplight.medirc.server.image;

import org.eplight.medirc.server.image.repo.ImageRepository;
import org.eplight.medirc.server.session.Session;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class ImageManager {

    @Inject
    private ImageRepository repository;

    private HashMap<Integer, Set<Image>> sessionImages = new HashMap<>();
    private HashMap<Integer, Image> images = new HashMap<>();

    public void addImage(Image img) {
        Set<Image> set = getSessionImages(img.getSessionId());

        set.add(img);

        images.put(img.getId(), img);
    }

    public Set<Image> getSessionImages(Session session) {
        return getSessionImages(session.getId());
    }

    public Set<Image> getSessionImages(int id) {
        if (sessionImages.containsKey(id))
            return sessionImages.get(id);

        Set<Image> img = repository.findSessionImages(id);

        img.forEach(i -> images.put(i.getId(), i));

        sessionImages.put(id, img);

        return img;
    }

    public Image getImage(int id) {
        Image img = images.get(id);

        return img != null ? img : repository.findById(id);
    }

    public void removeImage(Image img) {
        Set<Image> images = getSessionImages(img.getSessionId());

        images.remove(img);
    }
}
