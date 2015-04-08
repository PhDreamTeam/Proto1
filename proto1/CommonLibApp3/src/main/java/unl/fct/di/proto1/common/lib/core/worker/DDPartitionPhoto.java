package unl.fct.di.proto1.common.lib.core.worker;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhoto;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoWorker;
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
    public IPhotoWorker[] getData() {
        return data;
    }

    /*
   * getData to client
   */
    public IPhotoRemote[] getDataToClient() {
        // get working photo object
        IPhotoWorker[] pws = this.getData();

        // array to be returned
        IPhoto[] ps = new IPhoto[pws.length];

        // get all photoObjects
        int nPhotos = 0;
        for (IPhotoWorker pw : pws) {
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
