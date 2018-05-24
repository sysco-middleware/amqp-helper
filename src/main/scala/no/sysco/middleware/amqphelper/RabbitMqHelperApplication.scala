package no.sysco.middleware.amqphelper

import sun.misc.ObjectInputFilter.Config


object RabbitMqHelperApplication extends App {

  import no.sysco.middleware.amqphelper.utils.Config

  val config = Config.load()

  println(config)

}

//import com.sun.xml.internal.messaging.saaj.soap.Envelope
//import java.io.IOException
//import java.net.URISyntaxException
//import java.security.KeyManagementException
//import java.security.NoSuchAlgorithmException
//import java.util.function.Consumer
//
//object RabbitMqClient {
//  val MY_PRIVATE_QUEUE = "my-private-queue1"
//  val MY_ROUTING_KEY: String = MY_PRIVATE_QUEUE
//  val MY_PRIVATE_EXCHANGE = "my-custom-exchange1"
//  val DEFAULT_EXCHANGE = ""
//
//  @throws[IOException]
//  @throws[TimeoutException]
//  @throws[NoSuchAlgorithmException]
//  @throws[KeyManagementException]
//  @throws[URISyntaxException]
//  def main(args: Array[String]): Unit = {
//    val factory = new Nothing
//    val configuration = DsfServiceConfiguration.load
//    factory.setHost(configuration.rabbitMQ.host)
//    factory.setPort(configuration.rabbitMQ.port)
//    factory.setUsername(configuration.rabbitMQ.username)
//    factory.setPassword(configuration.rabbitMQ.password)
//    factory.setConnectionTimeout(30 _000)
//    preStart(factory)
//    produce5Messages(factory)
//    consumeFrom(MY_PRIVATE_QUEUE, factory)
//  }
//
//  @throws[IOException]
//  @throws[TimeoutException]
//  def preStart(factory: Nothing): Unit = {
//    try {
//      val connection = factory.newConnection
//      try
//        try {
//          val channel = connection.createChannel
//          try {
//            channel.exchangeDeclare(MY_PRIVATE_EXCHANGE, BuiltinExchangeType.DIRECT)
//            channel.queueDeclare(MY_PRIVATE_QUEUE, true, false, false, null)
//            channel.queueBind(MY_PRIVATE_QUEUE, MY_PRIVATE_EXCHANGE, MY_ROUTING_KEY)
//          } finally if (channel != null) channel.close()
//        }
//        finally if (connection != null) connection.close()
//    }
//  }
//
//  @throws[IOException]
//  @throws[TimeoutException]
//  def produce5Messages(factory: Nothing): Unit = {
//    val connection = factory.newConnection
//    val channel = connection.createChannel
//    var count = 0
//    while ( {
//      count < 5
//    }) {
//      val msg = String.valueOf(count)
//      channel.basicPublish(MY_PRIVATE_EXCHANGE, MY_ROUTING_KEY, // routing-key
//        null, // headers
//        msg.getBytes)
//      count += 1
//      System.out.println("Published: " + msg)
//    }
//    channel.close
//    connection.close
//  }
//
//  @throws[IOException]
//  @throws[TimeoutException]
//  def consumeFrom(queueName: String, factory: Nothing): Unit = {
//    val connection = factory.newConnection
//    val channel = connection.createChannel
//    // Old way, because Camel last version has transitive dependency for rabbitmq-client (not latest)
//    val consumer = new Nothing(channel) {
//      @throws[IOException]
//      def handleDelivery(consumerTag: String, envelope: Envelope, properties: Nothing, body: Array[Byte]): Unit = {
//        val message = new String(body, "UTF-8")
//        System.out.println(" [x] Received '" + message + "'")
//      }
//    }
//    val removeAllUpTo = true
//    // endless ( works as while(true) loop )
//    channel.basicConsume(queueName, false, consumer)
//  }
//}
//
