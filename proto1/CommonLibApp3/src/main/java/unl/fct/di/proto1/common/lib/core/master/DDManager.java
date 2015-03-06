package unl.fct.di.proto1.common.lib.core.master;

import java.util.HashMap;
import java.util.UUID;


public class DDManager {

    HashMap<String, DDMaster> DDs =  new HashMap<>();

    public String getNewDDUI() {
        return UUID.randomUUID().toString();
    }

    public DDMaster getDD(String DDUI) {
        return DDs.get(DDUI);
    }

    public DDMaster putDD(DDMaster dd) {
        return DDs.put(dd.getDDUI(), dd);
    }

    public HashMap<String, DDMaster> getDDs() {
        return DDs;
    }
}
