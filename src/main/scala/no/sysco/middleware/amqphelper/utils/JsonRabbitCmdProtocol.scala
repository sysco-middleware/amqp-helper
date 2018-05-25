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

final case class ExchangeCmd(exchange: String, exchangeType: String)

final case class BindCmd(queue: String, exchange: String, routingKey: String)

trait JsonRabbitCmdProtocol extends DefaultJsonProtocol {
  implicit def commandsFormat: RootJsonFormat[RabbitMqCommands] = jsonFormat(RabbitMqCommands, "queues", "exchanges", "bindings")

  implicit def queueCmd: RootJsonFormat[QueueCmd] = jsonFormat(QueueCmd, "queue", "durable", "exclusive", "autoDelete")

  implicit def exchangeCmdFormat: RootJsonFormat[ExchangeCmd] = jsonFormat(ExchangeCmd, "exchange", "type")

  implicit def bindCmdFormat: RootJsonFormat[BindCmd] = jsonFormat(BindCmd, "queue", "exchange", "routingKey")
}

object RabbitCmds extends JsonRabbitCmdProtocol {
  def load(): RabbitMqCommands = {
    val cmds = JsonParser(Source.fromFile("./rabbit-commands.json").getLines.mkString).convertTo[RabbitMqCommands]

    /** TODO: validation
      *
      * 1. BuiltinExchangeType
      * 2. RegEx
      * 3. bindings queues&exchanges exists
      *
      * */
    cmds
  }
}