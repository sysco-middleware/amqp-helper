package no.sysco.middleware.amqphelper

import java.util

import com.rabbitmq.client.ConnectionFactory
import no.sysco.middleware.amqphelper.utils.{ExchangeCmd, JsonRabbitCmdProtocol, RabbitCmds, RabbitMqCommands}

import scala.util.Try

object RabbitMqHelperApplication extends App with JsonRabbitCmdProtocol {

  import no.sysco.middleware.amqphelper.utils.Config

  val config = Config.load()
  val commands = RabbitCmds.load()

  // todo: proper logging
  println(config)
  println(commands)

  val factory = new ConnectionFactory
  factory.setHost(config.rabbitMq.host)
  // TODO: validate vh is present
  factory.setVirtualHost(config.rabbitMq.virtualHost)
  factory.setPort(config.rabbitMq.port)
  factory.setUsername(config.rabbitMq.username)
  factory.setPassword(config.rabbitMq.password)
  factory.setConnectionTimeout(20000)
  run(factory, commands)


  def run(factory: ConnectionFactory, cmds: RabbitMqCommands): Unit = {
    import no.sysco.middleware.amqphelper.utils._
    import scala.collection.JavaConverters._

    // TODO: change to flatten
    // val queueCmds : Seq[QueueCmd] = commands.exchangesCmd.flatten[Seq[QueueCmd]].flatten[Seq[QueueCmd]].getOrElse(Seq())
    val exchangeCmds : Seq[ExchangeCmd] = commands.exchangesCmd match {
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
            println(s"Exchange [#$declareOk:$exchangeCmd]  was created ")
          })
      }

      tryWithResources(connection.createChannel()) { channel =>
        queueCmds
          .foreach(queueCmd =>
            {
              val declareOk = channel.queueDeclare(
                queueCmd.queue,
                queueCmd.durable,
                queueCmd.exclusive,
                queueCmd.autoDelete,
                args)
              println(s"Queue ${declareOk.getQueue} was created ")
            })
      }

      tryWithResources(connection.createChannel()){ channel =>
        bindCmds
          .foreach(bindCmd => {
            val bindOk = channel.queueBind(bindCmd.queue, bindCmd.exchange, bindCmd.routingKey)
            println(s"Bind [$bindOk:$bindCmd] was created")
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

}

