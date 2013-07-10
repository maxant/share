package ch.maxant.jca_demo.sapresourceadapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.resource.ResourceException;

import org.jboss.jca.core.spi.recovery.RecoveryPlugin;

public class RecoveryPluginImpl implements RecoveryPlugin {

    private final Logger log = Logger.getLogger(this.getClass().getName());
    
    @Override
    public void close(Object arg0) throws ResourceException {
        log.log(Level.INFO, "Recovery plugin closing " + arg0);
    }
    
    @Override
    public boolean isValid(Object arg0) throws ResourceException {
        log.log(Level.INFO, "Recovery plugin checking validity of " + arg0);
        return true;
    }
}
