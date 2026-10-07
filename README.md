# Hello World con RabbitMQ, Docker y Spring Boot

Laboratorio de mensajería asíncrona: una aplicación Java con Spring Boot publica mensajes en una cola de RabbitMQ (que corre en Docker) y otro componente de la misma aplicación los consume.

```
Sender (RabbitTemplate) -> Exchange por defecto -> cola "hello" -> Receiver (@RabbitListener)
```

Está basado en la guía docente *"Hello World con RabbitMQ en Docker y Spring Boot"*, con las adaptaciones indicadas al final.

## Tecnologías

| Herramienta | Versión |
|---|---|
| Java | 17 o superior |
| Spring Boot | 4.1.1 (Spring for RabbitMQ / Spring AMQP) |
| Maven | Maven Wrapper (`mvnw`), incluido en el repositorio |
| RabbitMQ | imagen `rabbitmq:4.2-management` |
| Docker | Docker Desktop con Docker Compose |

## Requisitos previos

- Docker Desktop instalado y **abierto**.
- JDK 17 o superior (`java --version` y `javac --version`).
- Conexión a internet la primera vez: se descargan la imagen de RabbitMQ, Maven y las dependencias.
- No hace falta instalar Maven: el proyecto incluye el Maven Wrapper.

## Estructura del proyecto

```
.
├── compose.yaml                      # RabbitMQ con Management UI
├── pom.xml
├── mvnw / mvnw.cmd                   # Maven Wrapper
└── src/main
    ├── java/com/example/rabbitmq_tutorials
    │   ├── RabbitmqTutorialsApplication.java   # Clase principal y menú por consola
    │   ├── RabbitMQConfig.java                 # Declara la cola "hello"
    │   ├── Sender.java                         # Productor
    │   └── Receiver.java                       # Consumidor
    └── resources
        └── application.yaml                    # Conexión a RabbitMQ
```

## Cómo ejecutarlo

### 1. Clonar el repositorio

```
git clone https://github.com/Bastian131416/rabbitmq-spring-boot-hello-world.git
cd rabbitmq-spring-boot-hello-world
```

### 2. Levantar RabbitMQ

```
docker compose up -d
docker compose ps
```

Espera a que el estado sea `healthy`. Para comprobar el broker:

```
docker exec rabbitmq rabbitmq-diagnostics ping
```

Debe responder `Ping succeeded`.

### 3. Compilar

En Windows:

```
mvnw.cmd clean package -DskipTests
```

En Linux o macOS:

```
./mvnw clean package -DskipTests
```

Se usa `-DskipTests` porque el test que genera Spring Initializr arranca la aplicación completa y el menú por consola queda esperando entrada. Debe terminar en `BUILD SUCCESS`.

### 4. Ejecutar la aplicación

```
mvnw.cmd spring-boot:run
```

(`./mvnw spring-boot:run` en Linux o macOS.)

Cuando aparezca el menú, elige `1`, escribe un mensaje y pulsa Enter:

```
1. Enviar mensaje
2. Salir
Seleccione: 1
Mensaje: Hola RabbitMQ
[OK] Mensaje enviado: [21:52:07.327] Hola RabbitMQ

1. Enviar mensaje
2. Salir
Seleccione: [21:52:07.889] [OK] Mensaje recibido: [21:52:07.327] Hola RabbitMQ
```

La línea de "recibido" aparece junto al menú porque el `Receiver` corre en otro hilo: es el comportamiento asíncrono esperado. Para terminar, elige la opción `2`.

### 5. Verificar en la Management UI

Abre <http://localhost:15672> con usuario `guest` y contraseña `guest`. Con la aplicación en ejecución, en **Overview** deberías ver 1 conexión, 2 canales, 1 cola y 1 consumidor. En **Queues and Streams** aparece la cola `hello`.

### 6. Detener todo

```
docker compose down
```

El volumen con los datos de RabbitMQ se conserva, salvo que agregues `-v`.

## Puertos

| Puerto | Uso |
|---|---|
| 5672 | AMQP: por aquí se conecta la aplicación al broker |
| 15672 | Interfaz web de administración de RabbitMQ (no se usa en `spring.rabbitmq.port`) |
| 8080 | Servidor web de Spring Boot (solo relevante si se agrega una API REST) |

## Configuración

La conexión está en `src/main/resources/application.yaml`: host `localhost`, puerto `5672`, usuario `guest` y contraseña `guest`. Son credenciales de laboratorio para uso local; **no deben usarse en producción**.

## Problemas frecuentes

| Síntoma | Qué revisar |
|---|---|
| `failed to connect to the docker API` | Docker Desktop está apagado. Ábrelo y espera a que el motor esté en ejecución. |
| `port is already allocated` (5672 o 15672) | Otro proceso usa el puerto. Detenlo o cambia el mapeo en `compose.yaml` y en `application.yaml`. |
| `Connection refused` a `localhost:5672` | El contenedor no está `healthy`. Revisa `docker compose ps` y `docker logs rabbitmq`. |
| `cannot find symbol` al compilar | Las clases deben estar en el mismo paquete (`com.example.rabbitmq_tutorials`) y la línea `package` debe coincidir con su carpeta. |
| `PRECONDITION_FAILED` al arrancar | La cola `hello` ya existe con otras propiedades. Elimínala en la Management UI o recrea el contenedor. |

## Notas y diferencias respecto de la guía

- La cola `hello` se declara **no durable** (`new Queue("hello", false)`): los mensajes no sobreviven a un reinicio del broker. Es suficiente para el laboratorio.
- Hay un único `@RabbitListener` sobre la cola. Con dos listeners sobre la misma cola, los mensajes se reparten entre ellos en lugar de duplicarse.
- Al enviar un `String`, el mensaje no se convierte a JSON; para objetos se necesitaría un conversor JSON.
- Spring Boot 4.1.1 en lugar de 3.x, porque Spring Initializr ya no ofrecía versiones 3.x estables. El código del laboratorio es el mismo.
- El archivo de configuración es `application.yaml` (extensión generada por Initializr); Spring Boot lee igual `.yml` y `.yaml`.
- No se incluye el endpoint REST ni las extensiones de ACK manual, retry y concurrencia, que la guía marca como opcionales.

## Contexto

Laboratorio académico de Ingeniería en Informática sobre mensajería asíncrona con RabbitMQ.
