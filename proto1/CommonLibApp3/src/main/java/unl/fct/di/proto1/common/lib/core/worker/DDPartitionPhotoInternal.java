package unl.fct.di.proto1.common.lib.core.worker;

import unl.fct.di.proto1.common.lib.core.services.photo.IPhoto;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoWorker;
import unl.fct.di.proto1.common.workerService.WorkerService;

import java.util.Arrays;

/**
 * Created by AT DR on 11-03-2015.
 *
 */
public class DDPartitionPhotoInternal extends DDPartitionObject<IPhotoWorker> {
    // TODO quando fazemos load do disco temos de fazer o setWorkerService
    transient WorkerService ws;

    public DDPartitionPhotoInternal(String DDUI, int partID, WorkerService ws) {
        super(DDUI, partID, null);
        this.ws = ws;
    }

    private IPhotoWorker[] getDataPhotoWorkerArray() {
        // get data (photoWorker) from information on file
        return ws.getPhotoManager().getAllPhotoWorkerFromDD(DDUI);
    }

    /**
     * Returns the effective stored data of data set
     */
    public IPhotoWorker[] getData() {
        return getDataPhotoWorkerArray();
    }

    /**
     * Returns the data that client can manipulate. In normal DD is equal to get data.
     */
    public IPhotoRemote[] getDataToClient() {
        // get working photo object
        IPhotoWorker[] pws = getDataPhotoWorkerArray();

        // array to be returned
        IPhoto[] ps = new IPhoto[pws.length];

        // number of photos in array
        int nPhotos = 0;

        // get all photoObjects
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


    @Override
    public DDPartitionObject<IPhotoWorker> doClone() {
         // first get data
        IPhotoWorker[] pws = getDataPhotoWorkerArray();

        // we should return a DDPartitionObject to operate on it
        return new DDPartitionObject<>(DDUI, partId, pws);
    }

    @Override
    public DDPartitionObject<IPhotoWorker> createNewPartition(String newDDUI, int partId, int length) {
        DDPartitionPhoto ddpp = new DDPartitionPhoto(newDDUI, partId, this.ws);
        ddpp.setData(new IPhotoWorker[length]);
        return ddpp;

    }
}
