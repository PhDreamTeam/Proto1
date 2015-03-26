package unl.fct.di.proto1.common.lib.core.worker;

import pt.unl.fct.di.proto1.services.photos.Photo;
import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;
import unl.fct.di.proto1.common.workerService.WorkerService;

import java.util.Arrays;

/**
 * Created by AT DR on 11-03-2015.
 *
 */
public class DDPartitionPhotoInternal extends DDPartitionObject<IPhotoRemote> {
    // TODO quando fazemos load do disco temos de fazer o setWorkerService
    transient WorkerService ws;

    public DDPartitionPhotoInternal(String DDUI, int partID, WorkerService ws) {
        super(DDUI, partID, null);
        this.ws = ws;
    }

    private PhotoWorker[] getDataPhotoWorkerArray() {
        // get data (photoWorker) from information on file
        return ws.getPhotoManager().getAllPhotoWorkerFromDD(DDUI);
    }

    /**
     * Returns the effective stored data of data set
     */
    public IPhotoRemote[] getData() {
        return getDataPhotoWorkerArray();
    }

    /**
     * Returns the data that client can manipulate. In normal DD is equal to get data.
     */
    public IPhotoRemote[] getDataToClient() {
        // get working photo object
        PhotoWorker[] pws = getDataPhotoWorkerArray();

        // array to be returned
        Photo[] ps = new Photo[pws.length];

        // number of photos in array
        int nPhotos = 0;

        // get all photoObjects
        for (PhotoWorker pw : pws) {
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
    public DDPartitionObject<IPhotoRemote> doClone() {
         // first get data
        PhotoWorker[] pws = getDataPhotoWorkerArray();

        // we should return a DDPartitionObject to operate on it
        return new DDPartitionObject<IPhotoRemote>(DDUI, partId, pws);
    }

    @Override
    public DDPartitionObject<IPhotoRemote> createNewPartition(String newDDUI, int partId, int length) {
        DDPartitionPhoto ddpp = new DDPartitionPhoto(newDDUI, partId, this.ws);
        ddpp.setData(new IPhotoRemote[length]);
        return ddpp;

    }
}
