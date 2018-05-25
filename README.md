# AmqpHelper
AmqpHelper - tool to create topics, exchanges & bindings between.

## Inspiration
Inspired by `VIGILO-DSF` integration.  
Main goal - simulate client's AMQPs locally via docker.   

## RabbitMq commands 
Commands are defined in [rabbit-commands.json](./commands/rabbit-commands.json).
Json file should be provided as volume.

## Usage [Docker] 
1. Required volume with commands `/amqp-helper/rabbit-commands.json`
2. Env variables for rabbitMq connection
``` 
- RABBITMQ_HOST=rabbitmq
- RABBITMQ_PORT=5672
- RABBITMQ_USERNAME=foo
- RABBITMQ_PASSWORD=bar
- RABBITMQ_VIRTUAL_HOST=virtual.test 
```

