package ch.maxant.jca_demo.sapresourceadapter

import javax.resource.spi.ManagedConnectionFactory

class SAPConnectionImpl(mc: SAPManagedConnection, mcf: ManagedConnectionFactory) extends SAPConnection {

    /**
     * Call helloWorld
     * @return String helloworld
     */
    def helloWorld() = {
        helloWorld(/*todo how to get name from resource adapter? mcf.getResourceAdapter().asInstanceOf[SAPResourceAdapter].getName()*/"someName")
    }

    /**
     * Call helloWorld
     * @param name String name
     * @return String helloworld
     */
    def helloWorld(name: String) = mc.helloWorld(name)

    /**
     * Close
     */
    def close() {
        mc.closeHandle(this)
    }

}