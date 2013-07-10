package ch.maxant.jca_demo.sapresourceadapter

import javax.resource.spi.ResourceAdapter
import javax.resource.spi.Connector
import javax.resource.spi.TransactionSupport.TransactionSupportLevel.XATransaction
import javax.resource.spi.ConfigProperty
import javax.resource.spi.endpoint.MessageEndpointFactory
import javax.resource.ResourceException
import javax.resource.spi.ActivationSpec
import javax.resource.spi.BootstrapContext
import javax.resource.spi.ResourceAdapterInternalException
import javax.transaction.xa.XAResource
import scala.beans.BeanProperty
import java.util.logging.Logger
import java.util.logging.Level
import org.jboss.jca.core.spi.recovery.RecoveryPlugin

/* TODO https://community.jboss.org/thread/229857 */

/** a resource adapter into the SAP world via a non-transactional web service which suports the TCC pattern */
@Connector(reauthenticationSupport = false, transactionSupport = XATransaction)
class SAPResourceAdapter extends ResourceAdapter with RecoveryPlugin {

    private val log = Logger.getLogger(this.getClass().getName())
    
    @BeanProperty
    @ConfigProperty(defaultValue = "http://localhost:8080/SAPService", supportsDynamicUpdates = true)
    var url: String = _

    /**
     * This is called during the activation of a message endpoint.
     *
     * @param endpointFactory A message endpoint factory instance.
     * @param spec An activation spec JavaBean instance.
     * @throws ResourceException generic exception
     */
    @throws(classOf[ResourceException])
    def endpointActivation(endpointFactory: MessageEndpointFactory, spec: ActivationSpec) {
        log.log(Level.INFO, "activating sap endpoint")
    }

    /**
     * This is called when a message endpoint is deactivated.
     *
     * @param endpointFactory A message endpoint factory instance.
     * @param spec An activation spec JavaBean instance.
     */
    def endpointDeactivation(endpointFactory: MessageEndpointFactory, spec: ActivationSpec) {
        log.log(Level.INFO, "deactivating sap endpoint")
    }

    /**
     * This is called when a resource adapter instance is bootstrapped.
     *
     * @param ctx A bootstrap context containing references
     * @throws ResourceAdapterInternalException indicates bootstrap failure.
     */
    @throws(classOf[ResourceAdapterInternalException])
    def start(ctx: BootstrapContext) {
        log.log(Level.INFO, "starting resource adapter")
    }

    /**
     * This is called when a resource adapter instance is undeployed or
     * during application server shutdown.
     */
    def stop() {
        log.log(Level.INFO, "stopping resource adapter")
    }

    /**
     * This method is called by the application server during crash recovery.
     *
     * @param specs an array of ActivationSpec JavaBeans
     * @throws ResourceException generic exception
     * @return an array of XAResource objects
     */
    @throws(classOf[ResourceException])
    def getXAResources(specs: Array[ActivationSpec]): Array[XAResource] = {
        log.log(Level.INFO, "getting xa resources")
        null
    }

    override def hashCode() = {
        if (url != null) {
            url.##
        } else {
            17
        }
    }

    override def equals(other: Any) = other match {
        case that: SAPResourceAdapter =>
            this.url == that.url
        case _ => false
    }

    @throws(classOf[ResourceException])
    override def close(arg0: Object){
        log.log(Level.INFO, "Recovery plugin closing " + arg0)
    }
    
    @throws(classOf[ResourceException])
    override def isValid(arg0: Object) = {
        log.log(Level.INFO, "Recovery plugin checking validity of " + arg0)
        true
    }
    
}