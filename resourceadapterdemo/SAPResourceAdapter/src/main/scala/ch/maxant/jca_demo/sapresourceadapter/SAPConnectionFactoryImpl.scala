package ch.maxant.jca_demo.sapresourceadapter

import javax.resource.spi.ConnectionManager
import javax.naming.Reference
import scala.beans.BeanProperty
import javax.resource.ResourceException

class SAPConnectionFactoryImpl(mcf: SAPManagedConnectionFactory, cxManager: ConnectionManager) extends SAPConnectionFactory {

    @BeanProperty
    var reference: Reference = _

    /**
     * Get connection from factory
     *
     * @return HelloWorldConnection instance
     * @exception ResourceException Thrown if a connection can't be obtained
     */
    @throws(classOf[ResourceException])
    override def getConnection() =
        cxManager.allocateConnection(mcf, null).asInstanceOf[SAPConnection]

}