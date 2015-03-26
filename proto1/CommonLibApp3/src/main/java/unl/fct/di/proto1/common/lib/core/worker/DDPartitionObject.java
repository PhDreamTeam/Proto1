package unl.fct.di.proto1.common.lib.core.worker;


import unl.fct.di.proto1.common.lib.protocol.DDObject.MsgPartitionApplyFunctionDDObject;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Reduction;

import java.util.Arrays;

// TODO check if we need to keep the parent of this DD

public class DDPartitionObject<T> extends DDPartition {
    T[] data;

    public DDPartitionObject(String DDUI, int partId, T[] data) {
        super(DDUI, partId);
        this.data = data;
    }


    public DDPartitionObject(String newDDUI, int partId, int nDataElem, T[] arrayType) {
        super(newDDUI, partId);
        // create an array of nDataElems of arrayType
        data = Arrays.copyOf(arrayType, nDataElem);
    }

    public DDPartitionObject<T> doClone() {
        return cloneToNewDD(DDUI, partId);
    }

    public DDPartitionObject<T> cloneToNewDD(String newDDUI, int partId) {
        T[] array = Arrays.copyOf(data, data.length);
        return new DDPartitionObject<>(newDDUI, partId, array);
    }

    /**
     * Creates a new partition with empty elements
     */
    public DDPartitionObject<T> createNewPartition(String newDDUI, int partId, int length) {
        return new DDPartitionObject<>(newDDUI, partId, length, Arrays.copyOf(data, 0));
    }

    /**
     * Returns the effective stored data of data set
     */
    public T[] getData() {
        return data;
    }

    /**
     * Returns the data that client can manipulate. In normal DD is equal to get data.
     */
    public T[] getDataToClient() {
        return data;
    }

    public <R> DDPartitionObject<R> forEach(MsgPartitionApplyFunctionDDObject<T, R> msg) {
        // create a new partition - partitions are read only
        T[] workData = this.getData();
        DDPartitionObject<R> newDDPartition = new DDPartitionObject<>(msg.getNewDDUI(), this.getPartId(), workData.length, msg.getArrayRType());

        // get new partition array and store inside it the function results for each element of original dd
        R[] newData = newDDPartition.getData();
        for (int i = 0, size = workData.length; i < size; i++) {
            newData[i] = msg.getAction().apply(workData[i]);
        }
        return newDDPartition;
    }

    public DDPartitionObject<T> filter(Predicate<T> filter, String newDDUI) {
        // create a new partition - partitions are read only
        T[] workData = this.getData();
        DDPartitionObject<T> newDDPartition = this.createNewPartition(newDDUI, this.getPartId(), workData.length);

        // create an upper bounded array to store results
        T[] newDDData = newDDPartition.getData();

        // the elements that verify the test go to results array
        int newElems = 0;
        for (T wd : workData) {
            if (filter.test(wd))
                newDDData[newElems++] = wd;
        }

        // adjust size results data, store in new DD and return it
        newDDPartition.setData(Arrays.copyOf(newDDData, newElems));
        return newDDPartition;
    }

    public T doReduction(Reduction<T> reduction) {
        T result = null;

        for (T elem : getData()) {
            result = result == null ? elem : reduction.reduce(elem, result);
        }
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + " " + Arrays.toString(data);
    }


    public void setData(T[] data) {
        this.data = data;
    }

}
