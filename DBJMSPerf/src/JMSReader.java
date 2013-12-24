import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;


public class JMSReader {

	public static void main(String[] args) throws Exception {

	
		// Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        //connection.setExceptionListener(this);

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer consumer = session.createConsumer(destination);

        boolean keepGoing = true;
		// Wait for a message
        while(keepGoing){
        	Message message = consumer.receive(10000);
        	
        	if (message instanceof TextMessage) {
        		TextMessage textMessage = (TextMessage) message;
        		String text = textMessage.getText();
        		System.out.println("Received: " + text);
        	} else {
        		System.out.println("Received: " + message);
        	}
        }

        consumer.close();
        session.close();
        connection.close();	
	}
}
