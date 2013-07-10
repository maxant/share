package ch.maxant.jca_demo.sapresourceadapter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SAPWebService {

    private final Logger log = Logger.getLogger(this.getClass().getName());
    
    public String trySomeBusinessMethod(String name, String txid) throws Exception {
        
        log.log(Level.INFO, "TRY: " + name + " for TXID " + txid);
        if("FAILWS".equals(name)){
            throw new Exception("failed for test purposes");
        }else{
            return "hello " + name;
        }
    }

    public void confirm(String txId){
        log.log(Level.INFO, "CONFIRM: " + txId);
    }
    
    public void cancel(String txId){
        log.log(Level.INFO, "CANCEL: " + txId);
    }

}
