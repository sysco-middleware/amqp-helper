package no.sysco.middleware.amqphelper


import com.rabbitmq.client.ConnectionFactory
import no.sysco.middleware.amqphelper.utils._
import org.slf4j.LoggerFactory

import scala.util.Try

object RabbitMqHelperApplication extends App with JsonRabbitCmdProtocol {

  import no.sysco.middleware.amqphelper.utils.Config

  val logger = LoggerFactory.getLogger(this.getClass)
  val config = Config.load()
  val commands = RabbitCmds.load()
  logger.debug(s"Config loaded: $config")
  logger.debug(s"Commands loaded: $commands")
  val factory = getFactory(config)
  run(factory, commands)


  def run(factory: ConnectionFactory, cmds: RabbitMqCommands): Unit = {

    // TODO: change to flatten
    // val queueCmds : Seq[QueueCmd] = commands.exchangesCmd.flatten[Seq[QueueCmd]].getOrElse(Seq())
    val exchangeCmds: Seq[ExchangeCmd] = commands.exchangesCmd match {
      case Some(value) => value
      case None => Seq()
    }
    val queueCmds: Seq[QueueCmd] = commands.queuesCmd match {
      case Some(value) => value
      case None => Seq()
    }
    val bindCmds: Seq[BindCmd] = commands.bindings match {
      case Some(value) => value
      case None => Seq()
    }
    val args = new java.util.HashMap[String, AnyRef]


    tryWithResources(factory.newConnection()) { connection =>

      tryWithResources(connection.createChannel()) { channel =>
        exchangeCmds
          .foreach(exchangeCmd => {
            val declareOk = channel.exchangeDeclare(exchangeCmd.exchange, exchangeCmd.exchangeType)
            logger.info(s"Exchange [#$declareOk:$exchangeCmd]  was created ")
          })
      }

      tryWithResources(connection.createChannel()) { channel =>
        queueCmds
          .foreach(queueCmd => {
            val declareOk = channel.queueDeclare(
              queueCmd.queue,
              queueCmd.durable,
              queueCmd.exclusive,
              queueCmd.autoDelete,
              args)
            logger.info(s"Queue ${declareOk.getQueue} was created ")
          })
      }

      tryWithResources(connection.createChannel()) { channel =>
        bindCmds
          .foreach(bindCmd => {
            val bindOk = channel.queueBind(bindCmd.queue, bindCmd.exchange, bindCmd.routingKey)
            logger.info(s"Bind [$bindOk:$bindCmd] was created")
          })
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

  private[amqphelper] def getFactory(config: Config): ConnectionFactory = {
    val connFactory = new ConnectionFactory
    connFactory.setHost(config.rabbitMq.host)
    // TODO: validate vh is present
    connFactory.setVirtualHost(config.rabbitMq.virtualHost)
    connFactory.setPort(config.rabbitMq.port)
    connFactory.setUsername(config.rabbitMq.username)
    connFactory.setPassword(config.rabbitMq.password)
    connFactory.setConnectionTimeout(20000)
    connFactory
  }

}

