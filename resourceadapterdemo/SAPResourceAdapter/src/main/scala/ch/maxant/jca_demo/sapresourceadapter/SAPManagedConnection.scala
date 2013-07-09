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

class SAPManagedConnection(mcf: SAPManagedConnectionFactory) extends ManagedConnection {

    private val log = Logger.getLogger("SAPManagedConnectionFactory")

    /** The logwriter */
    @BeanProperty
    var logWriter: PrintWriter = _    
    
    /** Listeners */
    val listeners = ListBuffer[ConnectionEventListener]()

    /** Connection */
    var connection: Object = _

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
        this.connection = connection;
    }

    /**
     * Application server calls this method to force any cleanup on
     * the ManagedConnection instance.
     *
     * @throws ResourceException generic exception if operation fails
     */
    @throws(classOf[ResourceException])
    def cleanup() {
        println("cleaning up managed connection")
    }

    /**
     * Destroys the physical connection to the underlying resource manager.
     *
     * @throws ResourceException generic exception if operation fails
     */
    @throws(classOf[ResourceException])
    def destroy() {
        this.connection = null
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
            if (true) throw new RuntimeException("IMPL ME!") else null
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
            "Hello World, " + name + " !"
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
    }

}