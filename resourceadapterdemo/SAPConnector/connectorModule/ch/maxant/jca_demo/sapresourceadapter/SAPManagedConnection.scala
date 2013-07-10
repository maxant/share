package ch.maxant.jca_demo.sapresourceadapter

import javax.resource.spi.ManagedConnection
import javax.resource.spi.ConnectionEventListener
import javax.resource.ResourceException
import javax.resource.spi.ConnectionRequestInfo
import javax.security.auth.Subject
import scala.collection.mutable.ListBuffer
import javax.resource.NotSupportedException
import javax.resource.spi.LocalTransaction
import javax.transaction.xa.XAResource
import javax.resource.spi.ManagedConnectionMetaData
import javax.resource.spi.ConnectionEvent
import java.util.logging.Logger
import java.io.PrintWriter
import scala.beans.BeanProperty
import java.util.logging.Level
import javax.transaction.xa.Xid
import java.io.Serializable

/**
 * there is an assumption here that instances of this class are not thread safe
 * in that they can only be used one at a time.  TRY-CONFIRM or TRY-CANCEL.
 * The class contains logic to ensure this.
 */
class SAPManagedConnection(mcf: SAPManagedConnectionFactory) extends ManagedConnection with Serializable {

    private val log = Logger.getLogger("SAPManagedConnectionFactory")

    /** The logwriter */
    @BeanProperty
    var logWriter: PrintWriter = _

    /** Listeners */
    private val listeners = ListBuffer[ConnectionEventListener]()

    /** Connection */
    private var connection: SAPConnection = _

    //TODO take from pool - does container help here? or is it already pooled, since its a container managed connection?
    //TODO or inject from container?
    val webService: SAPWebService = new SAPWebService
    
    /** was the call to TRY successful? (tristate, null means ready for new connection) */
    private var tryWasSuccessful: java.lang.Boolean = null

    /** the current transaction ID */
    private var currentTxId: String = null
    
    /**
     * Creates a new connection handle for the underlying physical connection
     * represented by the ManagedConnection instance.
     *
     * @param subject Security context as JAAS subject
     * @param cxRequestInfo ConnectionRequestInfo instance
     * @return generic Object instance representing the connection handle.
     * @throws ResourceException generic exception if operation fails
     */
    @throws(classOf[ResourceException])
    def getConnection(subject: Subject,
                      cxRequestInfo: ConnectionRequestInfo): Object =
    {
        if(currentTxId == null) throw new IllegalStateException("transaction not started?")
        if(tryWasSuccessful != null) throw new IllegalStateException("did you forget to call close?")
        
        connection = new SAPConnectionImpl(this, mcf)
        connection
    }

    /**
     * Used by the container to change the association of an
     * application-level connection handle with a ManagedConneciton instance.
     *
     * @param connection Application-level connection handle
     * @throws ResourceException generic exception if operation fails
     */

    @throws(classOf[ResourceException])
    def associateConnection(connection: Object) {
        if (!connection.isInstanceOf[SAPConnection]) throw new IllegalArgumentException("Connection must be of type SAPConnection instead of " + connection.getClass)
        this.connection = connection.asInstanceOf[SAPConnection]
    }

    /**
     * Application server calls this method to force any cleanup on
     * the ManagedConnection instance.
     *
     * @throws ResourceException generic exception if operation fails
     */
    @throws(classOf[ResourceException])
    def cleanup() {
        log.log(Level.INFO, "cleaning up managed connection")

        currentTxId = null
        tryWasSuccessful = null
    }

    /**
     * Destroys the physical connection to the underlying resource manager.
     *
     * @throws ResourceException generic exception if operation fails
     */
    @throws(classOf[ResourceException])
    def destroy() {
        this.connection = null
        currentTxId = null
        tryWasSuccessful = null
    }

    /**
     * Adds a connection event listener to the ManagedConnection instance.
     *
     * @param listener A new ConnectionEventListener to be registered
     */
    def addConnectionEventListener(listener: ConnectionEventListener) {
        if (listener == null)
            throw new IllegalArgumentException("Listener is null")
        listeners += listener
    }

    /**
     * Removes an already registered connection event listener
     * from the ManagedConnection instance.
     *
     * @param listener Already registered connection event listener to be removed
     */
    def removeConnectionEventListener(listener: ConnectionEventListener) {
        if (listener == null)
            throw new IllegalArgumentException("Listener is null");
        listeners -= listener
    }

    /**
     * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
     *
     * @return LocalTransaction instance
     * @throws ResourceException generic exception if operation fails
     */
    @throws(classOf[ResourceException])
    def getLocalTransaction(): LocalTransaction =
    {
        //TODO impl this too
        throw new NotSupportedException("LocalTransaction not supported");
    }
    
    /**
     * Returns an <code>javax.transaction.xa.XAresource</code> instance.
     *
     * @return XAResource instance
     * @throws ResourceException generic exception if operation fails
     */
    @throws(classOf[ResourceException])
    def getXAResource(): XAResource =
    {
        val xa = new XASAPResource(this)
        xa
    }

    /**
     * Gets the metadata information for this connection's underlying
     * EIS resource manager instance.
     *
     * @return ManagedConnectionMetaData instance
     * @throws ResourceException generic exception if operation fails
     */
    @throws(classOf[ResourceException])
    def getMetaData(): ManagedConnectionMetaData =
    {
        return new SAPManagedConnectionMetaData()
    }

    /**
     * Call helloWorld
     * @param name String name
     * @return String helloworld
     */
    def helloWorld(name: String) =
    {
        if(currentTxId == null) throw new IllegalStateException("XID not yet set - was transaction started?")
        if(tryWasSuccessful != null) throw new IllegalStateException("not closed?")

        try{
            val r = this.webService.trySomeBusinessMethod(name, currentTxId)
            tryWasSuccessful = true
            r
        }catch{
            case e: Exception => {
                tryWasSuccessful = false
                throw e;
            }
        }
    }

    /**
     * Close handle
     * @param handle The handle
     */
    def closeHandle(handle: SAPConnection) {
        
        val event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED)
        event.setConnectionHandle(handle)
        listeners.foreach {
            _.connectionClosed(event)
        }
        
        currentTxId = null
    }

    /** 
     * was the call to the try method successful? 
     * used by the XAResource to determine if PREPARE can
     * return OK or NOK.
     * 
     * @throws IllegalStateException if the call to try has not yet occurred
     */
    def wasTrySuccessful = {
        if(this.tryWasSuccessful == null){
            throw new IllegalStateException("not expecting a call to wasTrySuccessful at this time")
        }else{
            tryWasSuccessful
        }
    }
    
    def setCurrentTxId(txId: String){
        if(currentTxId != null) throw new IllegalStateException("not ready for a new transaction - was this connection closed?")

        this.currentTxId = txId
    }
}
