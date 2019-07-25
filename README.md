# dgtic-jpa
Proyecto de la formación de JPA con tecnología estándar de la DGTIC

## Puntos importantes para mejorar el rendimiento de una aplicación JPA
### Getters en `@ManagedBeans`

Los getters en `@ManagedBeans` son llamados en repetidas ocasiones por el motor de JSF, por lo que poner cualquier tipo de consultas en estos es una mala práctica. Estas deberían realizarse en un `@PostConstruct` o en los métodos que ejecutan acciones a partir de botones o links (los atributos `action`) que solo son llamados una vez, cuando se realiza la acción.

### Lógica de negocio en `@ManagedBeans`

Recordemos que el Contexto de Persistencia tiene una vida que dura lo mismo que la transacción, que a su vez empieza y termina con cada método de negocio de un EJB. Esto implica que si realizamos lógica de negocio dentro de un `@ManagedBean`, las consultas de base de datos que se realizan a un EJB, ya mapeadas a objetos java, se tienen que volver a consultar y mapear en una nueva llamada a un método distinto (o incluso el mismo) de un EJB. Y es al realizar las modificaciones fuera del EJB cuando se hace necesario crear un `saveOrUpdate()` (con un `em.merge()` dentro), para guardar los cambios. Si la lógica se realiza dentro del EJB estos cambios ya se hacen persistentes de forma automática.

Los EJB sirven para realizar la lógica de negocio, y los `@ManagedBeans` deberían usarse solamente para controlar el front.

### EAGER
Nunca deberemos meter un `FetchType.EAGER` en una relación `@OneToMany` o `@ManyToMany`. Esto suele hacerse para evitar un `LazyIitializationException`, que normalmente se da por dos motivos:
1. Por un lado se está realizando lógica de negocio en los `@ManagedBeans`, y por lo tanto las propiedades `LAZY` ya no pueden ser recuperadas. 
2. No se han recuperado las propiedades `LAZY` mediante un `join fetch`, para poder utilizarlas fuera del contexto de persistencia. Fuera de este se pueden necesitar para el front, pero no para realizar lógica de negocio, que debería realizarse dentro del EJB.

Pero si lo solucionamos con un `EAGER` perdemos la capacidad de poder hacerlo `LAZY` en consultas explícitas, sin embargo esto no sucede al revés.

Y recordemos que esto tampoco significa que `LAZY` sea siempre la opción correcta en un `@OneToOne` o en un `@ManyToOne`. Todo dependerá de la complejidad de las relaciones.

### Revisar las consultas SQL que se generan

La diferencia entre poner un `fetch join` y olvidárselo puede ser pasar de 4000 consultas a unas pocas. Revisar siempre las consultas que se generan no siempre es viable por restricciones de tiempo en el desarrollo, pero en la medida de lo posible deberan revisarse, al menos en los puntos que puedan resultar más conflictivos, y siempre debera ser el primer paso cuando se detectan problemas de rendimiento.

Para poder mostrar las consultas en el JBoss de la dgtic deberemso hacer lo siguiente:

1. Añadir un nuevo Appender

```xml
    <!-- A time/date based rolling appender -->
   <appender name="JPA" class="org.jboss.logging.appender.DailyRollingFileAppender">
      <errorHandler class="org.jboss.logging.util.OnlyOnceErrorHandler"/>
      <param name="File" value="${jboss.server.log.dir}/jpa.log"/>
      <param name="Append" value="true"/>
      <!-- In AS 5.0.x the server log threshold was set by a system
           property. In 5.1 and later we are instead using the system
           property to set the priority on the root logger (see <root/> below)
      <param name="Threshold" value="${jboss.server.log.threshold}"/>
      -->
      
      <!-- Rollover at midnight each day -->
      <param name="DatePattern" value="'.'yyyy-MM-dd"/>

      <!-- Rollover at the top of each hour
      <param name="DatePattern" value="'.'yyyy-MM-dd-HH"/>
      -->

      <layout class="org.apache.log4j.PatternLayout">
         <!-- The default pattern: Date Priority [Category] (Thread) Message\n -->
         <param name="ConversionPattern" value="%d %m%n"/>

         <!-- The full pattern: Date MS Priority [Category] (Thread:NDC) Message\n
         <param name="ConversionPattern" value="%d %-5r %-5p [%c] (%t:%x) %m%n"/>
          -->
      </layout>
   </appender>
```

2. Añadir las categorías que queramos:

Para que muestre solo las consultas:
```xml
   <category name="org.hibernate.SQL">
      <priority value="DEBUG"/>
      <appender-ref ref="JPA"/>
   </category>
```
Para que muestre también los parámetros:
```xml
   <category name="org.hibernate.type">
      <priority value="TRACE"/>
      <appender-ref ref="JPA"/>
   </category>
```
