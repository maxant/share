package ch.maxant.jca_demo.sapresourceadapter

abstract trait SAPConnection extends AutoCloseable {

    /**
     * HelloWorld
     * @param name A name
     * @return String
     */

    def helloWorld(name: String): String

    /**
     * Close
     */
    def close()

}