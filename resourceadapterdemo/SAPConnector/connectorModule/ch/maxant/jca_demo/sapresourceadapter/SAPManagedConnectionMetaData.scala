package ch.maxant.jca_demo.sapresourceadapter

import javax.resource.spi.ManagedConnectionMetaData

class SAPManagedConnectionMetaData extends ManagedConnectionMetaData {

    /**
     * the EIS name
     */
    override def getEISProductName() = "SAP Resource Adapter"

    /**
     * Returns Product version of the underlying EIS instance connected
     * through the ManagedConnection.
     *
     * @return Product version of the EIS instance
     * @throws ResourceException Thrown if an error occurs
     */
    override def getEISProductVersion() = "1.0"

    /**
     * Returns maximum limit on number of active concurrent connections
     *
     * @return Maximum limit for number of active concurrent connections
     * @throws ResourceException Thrown if an error occurs
     */
    override def getMaxConnections() = 0

    /**
     * Returns name of the user associated with the ManagedConnection instance
     *
     * @return Name of the user
     * @throws ResourceException Thrown if an error occurs
     */
    override def getUserName(): String = null

}