package unl.fct.di.proto1.common.lib.core.worker;

import java.io.Serializable;

/**
 * Created by AT DR on 12-02-2015.
 *
 */
public class DDPartition implements Serializable {
    String DDUI;
    int partId;

    public DDPartition(String DDUI, int partId) {
        this.DDUI = DDUI;
        this.partId = partId;
    }

    public String getDDUI() {
        return DDUI;
    }

    public int getPartId() {
        return partId;
    }

    public void setDDUI(String DDUI) {
        this.DDUI = DDUI;
    }

    public String toString() {
        return getClass().getSimpleName().substring(11) + " " + DDUI + " " + partId;
    }
}
