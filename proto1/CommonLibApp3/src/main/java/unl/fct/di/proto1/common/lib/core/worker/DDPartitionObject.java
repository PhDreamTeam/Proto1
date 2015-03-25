package unl.fct.di.proto1.common.lib.core.worker;


import unl.fct.di.proto1.common.lib.protocol.DDObject.MsgPartitionApplyFunctionDDObject;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;
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

    @Override
    public DDPartitionObject clone() {
        T[] array = Arrays.copyOf(data, data.length);
        return new DDPartitionObject<T>(DDUI, partId, array);
    }

    public DDPartitionObject cloneToNewDD(String newDDUI, int partId) {
        T[] array = Arrays.copyOf(data, data.length);
        return new DDPartitionObject<T>(newDDUI, partId, array);
    }

    public DDPartitionObject<T> createNewPartition(String newDDUI, int partId, int length) {
        return new DDPartitionObject<>(newDDUI, partId, length, Arrays.copyOf(data, 0));
    }

    public T[] getData() {
        return data;
    }

    // check DR
    public Object[] getDataToClient() {
        return data;
    }

    // Perform an action as specified by a Consumer object
    //  public void forEach(Consumer<Integer> action, String newDDUI) {
    public <R> DDPartitionObject<R> forEach(MsgPartitionApplyFunctionDDObject<T, R> msg) {
        // create a new partition - partitions are read only
        T[] workData = this.getData();
        DDPartitionObject<R> newDDPartition = new DDPartitionObject<>(msg.getNewDDUI(), this.getPartId(), workData.length, msg.getArrayRType());

        R[] newData = newDDPartition.getData();
        for (int i = 0, size = workData.length; i < size; i++) {
            newData[i] = msg.getAction().apply(workData[i]);
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

    public DDPartitionObject<T> filter(Predicate<T> filter, String newDDUI) {
        // create a new partition - partitions are read only
        T[] workData = this.getData();
        DDPartitionObject<T> newDDPartition = this.createNewPartition(newDDUI, this.getPartId(), workData.length);

        T[] newDDData = newDDPartition.getData();

        // the elements that verify the test go to new DD partition
        int newElems = 0;
        for (int i = 0, size = workData.length; i < size; i++) {
            if (filter.test(workData[i])) {
                newDDData[newElems++] = workData[i]; // if the dataset is writable we have to clone the object
            }
        }

        // set partition data
        newDDPartition.setData(Arrays.copyOf(newDDData, newElems));

        return newDDPartition;

    }

    public Object doReduction(Reduction reduction) {
        Object result = null;

        for (Object elem : getData()) {
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
