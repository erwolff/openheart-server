package art.openhe.queue

import art.openhe.config.EnvConfig
import art.openhe.queue.consumer.SqsMessageConsumer
import art.openhe.util.logger
import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import javax.inject.Inject
import javax.inject.Singleton
import javax.jms.MessageProducer
import javax.jms.Session

@Singleton
class QueueManager
@Inject constructor(private val envConfig: EnvConfig) {

    private val log = logger()
    private val messageListeners = mutableMapOf<String, SqsMessageConsumer>()
    private val messageProducers = mutableMapOf<String, MessageProducer>()
    private var session: Session? = null

    fun registerQueues() {
        val connectionFactory = SQSConnectionFactory(ProviderConfiguration(),
            AmazonSQSClientBuilder.standard()
                .withRegion(envConfig.awsRegion())
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        BasicAWSCredentials(envConfig.awsAccessKey(), envConfig.awsSecretKey())))
                .build()
        )

        val connection = connectionFactory.createConnection()
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE)
        connection.start()
    }

    fun registerConsumer(queueName: String, sqsMessageConsumer: SqsMessageConsumer) {
        messageListeners[queueName] = sqsMessageConsumer
    }

    fun start() {
        messageListeners.entries.forEach { entry ->
            try {
                session?.createQueue(entry.key)?.let {
                    session?.createProducer(it)?.let { producer -> messageProducers.put(entry.key, producer) }
                    session?.createConsumer(it)?.messageListener = entry.value
                }
            } catch (e: Exception) {
                log.error("Failed to register SqsMessageListener for queue: ${entry.key}", e)
            }
        }
    }

    fun publish(queueName: String, message: String) {
        val textMessage = session?.createTextMessage(message);
        messageProducers[queueName]?.send(textMessage)
    }
}