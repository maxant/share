import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSWriter {

	public static void main(String[] args) throws Exception {

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

		// Create a Connection
		Connection connection = connectionFactory.createConnection();
		connection.start();

		long start = System.nanoTime();
		int numMessages = 1000;
		for (int i = 0; i < numMessages; i++) {
			// Create a transactional Session
			Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
			Destination destination = session.createQueue("TEST.FOO");
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);

			// Create a messages, 1000 chars
			String text = "some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string,";
			
			//send message
			BytesMessage message = session.createBytesMessage();
			message.writeBytes(text.getBytes());
			producer.send(message);

			//commit and close
			session.commit();
			session.close();
		}

		System.out.println("avg write "
				+ (((System.nanoTime() - start) / 1000000.0) / numMessages)
				+ " ms");

		connection.close();
	}
}
