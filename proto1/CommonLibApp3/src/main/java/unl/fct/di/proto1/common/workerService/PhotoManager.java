package unl.fct.di.proto1.common.workerService;

import pt.unl.fct.di.proto1.services.photos.PhotoWorker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 */
public class PhotoManager {

    WorkerService ws;
    // UUIDs and its photoWorkers
    HashMap<String, PhotoWorker> allInternalPhotos = new HashMap<>();

    // TODO: when we remove one photo, the photo must be removed from here and from the previous MAP
    // UUIDs and its pathFileNames
    HashMap<String, String> allInternalPhotoPathName = new HashMap<>();

    String pathNameDDs;

    ArrayList<String> activePhotoGroups = new ArrayList<>();


    public PhotoManager(String path, WorkerService ws) {
        pathNameDDs = path;
        this.ws = ws;
    }

    public PhotoWorker getPhotoWorker(String photoUUID) {
        PhotoWorker pw =  allInternalPhotos.get(photoUUID);
        if(pw == null) {
            // build object
            pw = new PhotoWorker(photoUUID, allInternalPhotoPathName.get(photoUUID), ws.getWorkerActorNode());
            // add it to map of photos
            allInternalPhotos.put(photoUUID, pw);
        }
        return pw;
    }

    public void addActivePhotoGroup(String photoGroup) {
        activePhotoGroups.add(photoGroup);
    }

    public boolean isActivePhotoGroup(String photoGroup) {
        return activePhotoGroups.contains(photoGroup);
    }


    public ArrayList<String> loadPhotoNamesFromDisk(String photoDD) {
        ArrayList<String> photoUuids = new ArrayList<>();
        try {
            Scanner scan = new Scanner(new File(pathNameDDs + "/" +  photoDD + ".txt"));
            while(scan.hasNextLine()) {
                Scanner lineScan = new Scanner(scan.nextLine());
                String photoPathName = lineScan.next();
                String photoUUID = lineScan.next();
                // add photo data to containers
                if(allInternalPhotoPathName.get(photoUUID) == null)
                    allInternalPhotoPathName.put(photoUUID, photoPathName);
                photoUuids.add(photoUUID);
                lineScan.close();
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return photoUuids;
    }

    public PhotoWorker[] getAllPhotoWorkerFromDD(String photoDD) {
        // read photoDD file, get photofilenames and uuids
        ArrayList<String> photoUuids = loadPhotoNamesFromDisk(photoDD);
        // create array
        PhotoWorker[] photoWorkers = new PhotoWorker[photoUuids.size()];
        // create photoWorkers to array
        for (int i = 0, size = photoUuids.size(); i < size; ++i)
            photoWorkers[i] = getPhotoWorker(photoUuids.get(i));
        // return array
        return photoWorkers;
    }
}
