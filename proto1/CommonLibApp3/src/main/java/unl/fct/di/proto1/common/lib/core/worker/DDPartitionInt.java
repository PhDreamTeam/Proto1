package unl.fct.di.proto1.common.lib.core.worker;


import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.util.Arrays;

// TODO check if we need to keep the parent of this DD

public class DDPartitionInt extends DDPartition {
    int[] data;

    public DDPartitionInt(String DDUI, int partId, int[] data) {
        super(DDUI, partId);
        this.data = data;
    }

    @Override
    public DDPartitionInt clone() {
        return new DDPartitionInt(DDUI, partId, Arrays.copyOf(data, data.length));
    }

    public int[] getData() {
        return data;
    }

    // Perform an action as specified by a Consumer object
    //  public void forEach(Consumer<Integer> action, String newDDUI) {
    public DDPartitionInt forEach(Function<Integer, Integer> action, String newDDUI) {
        // create a new partition - partitions are read only
        DDPartitionInt newDDPartition = clone();
        // set the DDUI of the new DDInt
        newDDPartition.setDDUI(newDDUI);

        int[] workData = newDDPartition.getData();

        for (int i = 0, size = data.length; i < size; i++) {
            workData[i] = action.apply(data[i]);
        }
        return newDDPartition;
    }

    // Map objects to another DD as specified by a Function object
    public <R> R map(Function<Integer, ? extends R> mapper) {
        // create a new partition - partitions are read only
        DDPartitionInt newDDPartition = clone();

        int[] workData = newDDPartition.getData();

        for (int i = 0, size = data.length; i < size; i++) {
            //workData[i] = mapper.apply(data[i]);
        }

       return null;
    }

    public DDPartitionInt filter(Predicate<Integer> filter, String newDDUI) {
        // create a new partition - partitions are read only
        DDPartitionInt newDDPartition = clone();
        // set the DDUI of the new DDInt
        newDDPartition.setDDUI(newDDUI);

        int[] newDDData = newDDPartition.getData();

        // the elements that verify the test go to new DD partition
        int newElems = 0;
        for (int i = 0, size = data.length; i < size; i++) {
            if (filter.test(data[i])) {
                newDDData[newElems++] = data[i];
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


    public void setData(int[] data) {
        this.data = data;
    }
}
