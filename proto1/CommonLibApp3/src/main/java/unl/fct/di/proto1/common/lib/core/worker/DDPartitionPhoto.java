package unl.fct.di.proto1.common.lib.core.worker;

import pt.unl.fct.di.proto1.services.photos.Photo;
import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.workerService.WorkerService;

import java.util.Arrays;

/**
 * Created by AT DR on 17/03/2015.
 *
 */
public class DDPartitionPhoto extends DDPartitionPhotoInternal {
    public DDPartitionPhoto(String DDUI, int partID, WorkerService ws) {
        super(DDUI, partID, ws);
    }

    /**
     * returns photoworkers
     */
    public IPhotoRemote[] getData() {
        return data;
    }

    /*
   * getData to client
   */
    public IPhotoRemote[] getDataToClient() {
        // get working photo object
        IPhotoRemote[] pws = this.getData();

        // array to be returned
        Photo[] ps = new Photo[pws.length];

        // get all photoObjects
        int nPhotos = 0;
        for (IPhotoRemote pw : pws) {
            try {
                ps[nPhotos] = ((PhotoWorker)pw).getPhotoObject();
                nPhotos++;
            } catch (Exception e) {
                ws.getWorkerGui().println("Photo failed to load: " + ((PhotoWorker)pw).getPathFileName());
            }
        }

        // return only the part with photos
        return nPhotos == pws.length ? ps : Arrays.copyOf(ps, nPhotos);
    }
}
