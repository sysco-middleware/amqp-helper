package no.sysco.middleware.amqphelper.utils


import spray.json.{DefaultJsonProtocol, JsonParser, RootJsonFormat}

import scala.io.Source


final case class RabbitMqCommands(queuesCmd: Option[Seq[QueueCmd]],
                                  exchangesCmd: Option[Seq[ExchangeCmd]],
                                  bindings: Option[Seq[BindCmd]])

final case class QueueCmd(queue: String,
                          durable: Boolean = false,
                          exclusive: Boolean = false,
                          autoDelete: Boolean = false)

final case class ExchangeCmd(exchange: String, exchangeType: String, durable: Boolean, autoDelete: Boolean)

final case class BindCmd(queue: String, exchange: String, routingKey: String)

trait JsonRabbitCmdProtocol extends DefaultJsonProtocol {
  implicit def commandsFormat: RootJsonFormat[RabbitMqCommands] = jsonFormat(RabbitMqCommands, "queues", "exchanges", "bindings")

  implicit def queueCmd: RootJsonFormat[QueueCmd] = jsonFormat(QueueCmd, "queue", "durable", "exclusive", "autoDelete")

  implicit def exchangeCmdFormat: RootJsonFormat[ExchangeCmd] = jsonFormat(ExchangeCmd, "exchange", "type", "durable", "autoDelete")

  implicit def bindCmdFormat: RootJsonFormat[BindCmd] = jsonFormat(BindCmd, "queue", "exchange", "routingKey")
}

object RabbitCmds extends JsonRabbitCmdProtocol {
  def load(): RabbitMqCommands = {
    val cmds = JsonParser(Source.fromFile("./rabbit-commands.json").getLines.mkString).convertTo[RabbitMqCommands]
    validate(cmds)
    cmds
  }

  private def validate(cmds: RabbitMqCommands) = {
    import com.rabbitmq.client.BuiltinExchangeType._
    val exchangeTypes = Seq(TOPIC,DIRECT,FANOUT,HEADERS).map(_.getType)

    // cmds
    val queueCmds = cmds.queuesCmd.getOrElse(Seq())
    val exchangeCmds = cmds.exchangesCmd.getOrElse(Seq())
    val bindCmds = cmds.bindings.getOrElse(Seq())

    // seq names
    val queueCmdNames = queueCmds.map(_.queue)
    val exchangeCmdNames = exchangeCmds.map(_.exchange)

    //    1 validate queues
    queueCmds.foreach(qCmd => if(qCmd.queue.trim.isEmpty) throw new IllegalArgumentException("Queue name is empty"))

    //    2 validate exchanges
    exchangeCmds.foreach(eCmd => {
      // exchange can be empty (default)
      //      if(eCmd.exchange.trim.isEmpty) throw new IllegalArgumentException("Exchange name is empty")
      if( ! exchangeTypes.contains(eCmd.exchangeType.toLowerCase.trim) ) throw new IllegalArgumentException("Incorrect exchange type")
    })
    //    3 validate bindings
    bindCmds.foreach(bCmd => {
      if(bCmd.routingKey.trim.isEmpty) throw new IllegalArgumentException("Bindings: routing key is empty")
      if(bCmd.queue.trim.isEmpty) throw new IllegalArgumentException("Bindings: queue is empty")
      if( ! queueCmdNames.contains(bCmd.queue) ) throw new IllegalArgumentException("Bindings: queue for binding can not be found in queue commands")
      if( ! exchangeCmdNames.contains(bCmd.exchange) ) throw new IllegalArgumentException("Bindings: exchange for binding can not be found in exchange commands")
    })
  }
}