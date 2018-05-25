package no.sysco.middleware.amqphelper

import com.rabbitmq.client.ConnectionFactory
import no.sysco.middleware.amqphelper.utils._

import scala.util.Try

object RabbitMqHelperApplication extends App {

  import no.sysco.middleware.amqphelper.utils.Config

  val config = Config.load()
  val commands = RabbitCmds.load()


  println(config)
  println(commands)

  val factory = new ConnectionFactory
  factory.setHost(config.rabbitMq.host)
  factory.setPort(config.rabbitMq.port)
  factory.setUsername(config.rabbitMq.username)
  factory.setPassword(config.rabbitMq.password)
  factory.setConnectionTimeout(20000)
  run(factory, commands)


  //  channel.exchangeDeclare(MY_PRIVATE_EXCHANGE, BuiltinExchangeType.DIRECT)
  //  channel.queueDeclare(MY_PRIVATE_QUEUE, true, false, false, null)
  //  channel.queueBind(MY_PRIVATE_QUEUE, MY_PRIVATE_EXCHANGE, MY_ROUTING_KEY)
  def run(factory: ConnectionFactory, cmds: RabbitMqCommands): Unit = {
    tryWithResources(factory.newConnection()) { connection =>
      tryWithResources(connection.createChannel()) { channel =>
        commands.exchangesCmd.flatten[ExchangeCmd]
          .foreach(exchangeCmd => channel.exchangeDeclare(exchangeCmd.exchange, exchangeCmd.exchangeType))
        commands.queuesCmd.flatten[QueueCmd]
          .foreach(queueCmd => channel.queueDeclare(queueCmd.queue, queueCmd.durable, queueCmd.exclusive, queueCmd.autoDelete, java.util.Map[String, AnyRef]))
        commands.bindings.flatten[BindCmd]
          .foreach(bindCmd => channel.queueBind(bindCmd.queue, bindCmd.exchange, bindCmd.routingKey))
      }
    }
  }


  private[amqphelper] def tryWithResources[A <: AutoCloseable, B](resource: A)(code: A ⇒ B): Try[B] = {
    val tryResult = Try {
      code(resource)
    }
    resource.close()
    tryResult
  }

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
//    val factory = new ConnectionFactory
//    factory.setHost(configuration.rabbitMQ.host)
//    factory.setPort(configuration.rabbitMQ.port)
//    factory.setUsername(configuration.rabbitMQ.username)
//    factory.setPassword(configuration.rabbitMQ.password)
//    factory.setConnectionTimeout(30 _000)
//    preStart(factory)
//    produce5Messages(factory)
//    consumeFrom(MY_PRIVATE_QUEUE, factory)
//  }

//  @throws[IOException]
//  @throws[TimeoutException]

//
//}

