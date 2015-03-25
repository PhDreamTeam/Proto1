package unl.fct.di.proto1.common.lib.core.worker;

import pt.unl.fct.di.proto1.services.photos.Photo;
import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.workerService.WorkerService;

import java.util.Arrays;

/**
 * Created by AT DR on 17/03/2015.
 *
 */
public class DDPartitionPhoto extends DDPartitionPhotoInternal{
    public DDPartitionPhoto(String DDUI, int partID, WorkerService ws) {
        super(DDUI, partID, ws);
    }

    /**
     * returns photoworkers
     */
    public Object[] getData() {
        return data;
    }

    /*
   * getData to client
   */
    public Object[] getDataToClient() {
        // get working photo object
        Object[] pws = this.getData();

        // array to be returned
        Photo[] ps = new Photo[pws.length];

        // number of photos in array
        int nPhotos = 0;

        // get all photoObjects
        for (int i = 0, size = pws.length; i < size; i++) {
            PhotoWorker pw = (PhotoWorker)pws[i];
            try {
                ps[nPhotos] = pw.getPhotoObject();
                nPhotos++;
            } catch (Exception e) {
                ws.getWorkerGui().println("Photo failed to load: " + pw.getPathFileName());
            }
        }

        // return only the part with photos
        return nPhotos == pws.length ? ps : Arrays.copyOf(ps, nPhotos);
    }
}
