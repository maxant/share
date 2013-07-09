package ch.maxant.jca_demo.sapresourceadapter

abstract trait SAPConnection {

    /**
     * HelloWorld
     * @return String
     */
    def helloWorld(): String
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