package ch.maxant.jca_demo.sapresourceadapter

import javax.resource.spi.ConnectionManager
import javax.naming.Reference
import scala.beans.BeanProperty
import javax.resource.ResourceException
import java.util.logging.Level
import java.util.logging.Logger

class SAPConnectionFactoryImpl(mcf: SAPManagedConnectionFactory, cxManager: ConnectionManager) extends SAPConnectionFactory {

    private val log = Logger.getLogger(this.getClass().getName())
    
    @BeanProperty
    var reference: Reference = _

    /**
     * Get connection from factory
     *
     * @return HelloWorldConnection instance
     * @exception ResourceException Thrown if a connection can't be obtained
     */
    @throws(classOf[ResourceException])
    override def getConnection() = {
        log.log(Level.INFO, "getConnection of sap called")
        cxManager.allocateConnection(mcf, null).asInstanceOf[SAPConnection]
    }

}