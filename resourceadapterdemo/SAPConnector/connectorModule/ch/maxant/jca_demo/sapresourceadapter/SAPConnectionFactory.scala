package ch.maxant.jca_demo.sapresourceadapter

import javax.resource.ResourceException
import javax.resource.Referenceable

abstract trait SAPConnectionFactory extends Serializable with Referenceable {
    /**
     * Get connection from factory
     *
     * @return HelloWorldConnection instance
     * @exception ResourceException Thrown if a connection can't be obtained
     */
    @throws(classOf[ResourceException])
    def getConnection(): SAPConnection
}