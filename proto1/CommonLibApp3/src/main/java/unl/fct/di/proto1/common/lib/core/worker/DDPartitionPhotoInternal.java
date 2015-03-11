package unl.fct.di.proto1.common.lib.core.worker;

import pt.unl.fct.di.proto1.services.photos.Photo;
import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.workerService.WorkerService;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by AT DR on 11-03-2015.
 *
 */
public class DDPartitionPhotoInternal extends DDPartitionObject {
    WorkerService ws;

    public DDPartitionPhotoInternal(String DDUI, WorkerService ws) {
        super(DDUI, 0, null);
        this.ws = ws;
    }

    public PhotoWorker[] getDataPhotoWorkerArray() {
        // get data (photoWorker) from information on file
        return ws.getPhotoManager().getAllPhotoWorkerFromDD(DDUI);
    }

    /*
     * getData to client
     */
    public Object[] getData() {
        // get working photo object
        PhotoWorker[] pws = getDataPhotoWorkerArray();

        // array to be returned
        Photo[] ps = new Photo[pws.length];

        // number of photos in array
        int nPhotos = 0;

        // get all photoObjects
        for (int i = 0, size = pws.length; i < size; i++) {
            try {
                ps[nPhotos] = pws[i].getPhotoObject();
                nPhotos++;
            } catch (IOException e) {
                ws.getWorkerGui().println("Photo failed to load: " + pws[i].getPathFileName());
            }
        }

        // return only the part with photos
        return nPhotos == pws.length ? ps : Arrays.copyOf(ps, nPhotos);
    }

    @Override
    public DDPartitionObject clone() {
        // TODO check if we should return an DDPartitionObject or DDPartitionPhoto

        // first get data
        PhotoWorker[] pws = getDataPhotoWorkerArray();

        // we should return a DDPartitionObject to operate on it
        return new DDPartitionObject(DDUI, partId, pws);
    }


}