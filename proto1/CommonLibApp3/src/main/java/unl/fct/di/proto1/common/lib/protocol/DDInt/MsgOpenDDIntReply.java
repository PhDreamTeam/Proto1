package unl.fct.di.proto1.common.lib.protocol.DDInt;

import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.io.Serializable;


public class MsgOpenDDIntReply extends MsgReply implements Serializable {
    int nDataElemsDD;


    public MsgOpenDDIntReply(String DDUI, String requestId, int nDataElemsDD,
                             boolean success, String failureReason) {
        super(DDUI, requestId, success, failureReason);
        this.nDataElemsDD = nDataElemsDD;
    }


    public int getnDataElemsDD() {
        return nDataElemsDD;
    }


    // to be called by toString
    @Override
    public String getIntermediateInfo() {
        return super.getIntermediateInfo() + ", nelems: " + getnDataElemsDD();
    }
}
