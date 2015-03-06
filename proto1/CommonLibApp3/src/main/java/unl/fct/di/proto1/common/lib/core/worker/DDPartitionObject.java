package unl.fct.di.proto1.common.lib.core.worker;


import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.util.Arrays;

// TODO check if we need to keep the parent of this DD

public class DDPartitionObject extends DDPartition {
    Object[] data;

    public DDPartitionObject(String DDUI, int partId, Object[] data) {
        super(DDUI, partId);
        this.data = data;
    }

    @Override
    public DDPartitionObject clone() {
        return new DDPartitionObject(DDUI, partId, Arrays.copyOf(data, data.length));
    }

    public Object[] getData() {
        return data;
    }

    // Perform an action as specified by a Consumer object
    //  public void forEach(Consumer<Integer> action, String newDDUI) {
    public DDPartitionObject forEach(Function<Object, Object> action, String newDDUI) {
        // create a new partition - partitions are read only
        DDPartitionObject newDDPartition = clone();
        // set the DDUI of the new DDInt
        newDDPartition.setDDUI(newDDUI);

        Object[] workData = newDDPartition.getData();

        for (int i = 0, size = data.length; i < size; i++) {
            workData[i] = action.apply(data[i]);
        }
        return newDDPartition;
    }

    // Map objects to another DD as specified by a Function object
    public <R> R map(Function<Object, ? extends R> mapper) {
        // create a new partition - partitions are read only
        DDPartitionObject newDDPartition = clone();

        Object[] workData = newDDPartition.getData();

        for (int i = 0, size = data.length; i < size; i++) {
            //workData[i] = mapper.apply(data[i]);
        }

       return null;
    }

    public DDPartitionObject filter(Predicate<Object> filter, String newDDUI) {
        // create a new partition - partitions are read only
        DDPartitionObject newDDPartition = clone();
        // set the DDUI of the new DDInt
        newDDPartition.setDDUI(newDDUI);

        Object[] newDDData = newDDPartition.getData();

        // the elements that verify the test go to new DD partition
        int newElems = 0;
        for (int i = 0, size = data.length; i < size; i++) {
            if (filter.test(data[i])) {
                newDDData[newElems++] = data[i]; // if the dataset is writable we have to clone the object
            }
        }

        // set partition data
        newDDPartition.setData(Arrays.copyOf(newDDData, newElems));

        return newDDPartition;

    }

    @Override
    public String toString() {
        return super.toString() + " " + Arrays.toString(data);
    }


    public void setData(Object[] data) {
        this.data = data;
    }
}
