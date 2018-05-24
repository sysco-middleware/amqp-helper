package no.sysco.middleware.amqphelper.utils


import pureconfig.loadConfig

case class Config(rabbitMq: RabbitMqConfig)

object Config {
  def load() =
    loadConfig[Config] match {
      case Right(config) => config
      case Left(error) =>
        throw new RuntimeException("Cannot read config file, errors:\n" + error.toList.mkString("\n"))
    }
}

private[utils] case class RabbitMqConfig(host: String, port: Int, username: String, password: String, virtualHost: String)

