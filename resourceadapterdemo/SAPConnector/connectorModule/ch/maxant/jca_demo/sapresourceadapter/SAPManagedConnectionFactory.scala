package ch.maxant.jca_demo.sapresourceadapter
import javax.resource.spi.ConnectionDefinition
import javax.resource.spi.ManagedConnectionFactory
import javax.resource.spi.ResourceAdapterAssociation
import javax.resource.ResourceException
import javax.resource.spi.ConnectionManager
import javax.resource.spi.ConnectionRequestInfo
import javax.security.auth.Subject
import javax.resource.spi.ManagedConnection
import java.util.Set
import scala.beans.BeanProperty
import javax.resource.spi.ResourceAdapter
import java.io.PrintWriter

/**
 */
@ConnectionDefinition(connectionFactory = classOf[SAPConnectionFactory],
    connectionFactoryImpl = classOf[SAPConnectionFactoryImpl],
    connection = classOf[SAPConnection],
    connectionImpl = classOf[SAPConnectionImpl])
class SAPManagedConnectionFactory extends ManagedConnectionFactory with ResourceAdapterAssociation {

    /** The logwriter */
    @BeanProperty
    var logWriter: PrintWriter = _    
    
    /** The resource adapter */
    @BeanProperty
    var resourceAdapter: ResourceAdapter = _

    /**
     * Creates a Connection Factory instance.
     *
     * @return EIS-specific Connection Factory instance or
     * javax.resource.cci.ConnectionFactory instance
     * @throws ResourceException Generic exception
     */
    @throws(classOf[ResourceException])
    def createConnectionFactory(): Object = {
        throw new ResourceException("This resource adapter doesn't support non-managed environments")
    }

    /**
     * Creates a Connection Factory instance.
     *
     * @param cxManager ConnectionManager to be associated with created EIS
     * connection factory instance
     * @return EIS-specific Connection Factory instance or
     * javax.resource.cci.ConnectionFactory instance
     * @throws ResourceException Generic exception
     */
    @throws(classOf[ResourceException])
    def createConnectionFactory(cxManager: ConnectionManager): Object = {
        return new SAPConnectionFactoryImpl(this, cxManager)
    }

    /**
     * Creates a new physical connection to the underlying EIS resource manager.
     *
     * @param subject Caller's security information
     * @param cxRequestInfo Additional resource adapter specific connection
     * request information
     * * @throws ResourceException generic exception
     * @return ManagedConnection instance
     */
    @throws(classOf[ResourceException])
    def createManagedConnection(subject: Subject,
                                cxRequestInfo: ConnectionRequestInfo): ManagedConnection =
        {
            new SAPManagedConnection(this)
        }

    /**
     * Returns a matched connection from the candidate set of connections.
     *
     * @param connectionSet Candidate connection set
     * @param subject Caller's security information
     * @param cxRequestInfo Additional resource adapter specific connection request information
     * @throws ResourceException generic exception
     * @return ManagedConnection if resource adapter finds an acceptable match otherwise null
     */
    @throws(classOf[ResourceException])
    def matchManagedConnections(connectionSet: Set[_],
                                subject: Subject, cxRequestInfo: ConnectionRequestInfo): ManagedConnection =
        {
            var result: ManagedConnection = null
            val it = connectionSet.iterator
            while (result == null && it.hasNext) {
                val mc = it.next.asInstanceOf[ManagedConnection]
                if (mc.isInstanceOf[SAPManagedConnection]) {
                    result = mc.asInstanceOf[SAPManagedConnection]
                }
            }
            result
        }

    override def hashCode() = 17

    override def equals(other: Any) = other match {
        case that: SAPManagedConnectionFactory =>
            true
        case _ => false
    }
    
}