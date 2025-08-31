
## Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW

##### Parte III. – Avance para el martes, antes de clase.

Este es un juego con N jugadores inmortales, donde cada uno conoce a los demás. Durante la partida, cada jugador ataca constantemente a otro jugador elegido al azar. Cada vez que ataca, le quita M puntos de vida al oponente y suma esos mismos puntos a su propia vida.

El juego no siempre llega a tener un único ganador. En muchos casos, puede terminar en una situación donde solo quedan dos jugadores atacándose mutuamente, infligiéndose y recuperando exactamente la misma cantidad de vida al mismo ritmo.

Cuando ambos jugadores atacan al mismo tiempo, se genera un bucle infinito (deadlock), ya que la cantidad de vida que uno pierde es exactamente la que el otro gana, y viceversa. Esto hace que el sistema entre en un estado estable en el que ningún jugador puede morir, y el programa nunca finaliza.

Un invariante del sistema es que la sumatoria total de las vidas de todos los jugadores se mantiene constante en los momentos en que no se están realizando ataques** (es decir, cuando no hay procesos simultáneos de reducción y adición de vida).

Este invariante se expresa como:

$$
n \times 100
$$

Donde:

- \( n \) es el número de jugadores inmortales.
- 100 es el valor definido por la constante `DEFAULT_INMORTAL_HEALTH`, que representa la vida inicial de cada jugador.

En el constructor por defecto de la clase `ControlFrame`, el campo `numOfImmortals` (un objeto `JTextField`) se inicializa con el valor `"3"`, lo que indica que al comenzar el juego siempre hay 3 jugadores inmortales. Por lo tanto, durante la partida, el invariante de la suma total de vida es:

$$
3 \times 100 = 300
$$

La opción Pause and Check permite visualizar el nombre de cada inmortal junto a su vida actual, así como la sumatoria total de vida en ese momento. Se realizaron cuatro pruebas consecutivas durante la misma ejecución del programa, cuyos resultados se muestran a continuación:

En esta primera prueba, la sumatoria total de vida fue de 270, lo cual es inferior al valor esperado de 300 (para 3 inmortales con 100 de vida cada uno).

![first_inmortal_test.png](img/first_inmortal_test.png)

En la segunda prueba, la suma total fue de 360, superando el valor invariante esperado.

![second_inmortal_test.png](img/second_inmortal_test.png)

En la tercera captura, la vida total alcanzó los 410, nuevamente por encima del valor correcto.

![third_inmortal_test.png](img/third_inmortal_test.png)

Finalmente, en la cuarta prueba, la suma descendió a 210, por debajo del invariante.

![fourth_inmortal_test.png](img/fourth_inmortal_test.png)

Estos resultados evidencian un claro problema de sincronización en el manejo concurrente de la vida de los inmortales.

La razón por la que los resultados no se mostraban correctamente se debía a un problema de sincronización en el manejo concurrente de la vida de los jugadores. Por esta razón, se refactorizó la funcionalidad del botón Pause and Check para que pueda pausar correctamente el hilo, y se añadió un botón de Reanudar para controlar la ejecución.

Sin embargo, durante la pelea persistían problemas de sincronización. Las regiones críticas donde podían ocurrir condiciones de carrera estaban principalmente en los métodos changeHealth y getHealth. Dado que el campo health es un dato compartido entre varios hilos, la opción de sincronización más adecuada fue cambiar el tipo de dato de health de int a AtomicInteger. Este tipo proporciona métodos atómicos para añadir y obtener valores, facilitando el control concurrente en Java.

Además, para evitar problemas adicionales, se decidió eliminar el método changeHealth, que cumplía una función similar a un setter. Esto se debe a que la combinación get() -> set() sin sincronización puede provocar inconsistencias por la gestión de la memoria (hash en memoria), especialmente si múltiples hilos acceden simultáneamente.

En su lugar, se utilizó el método addAndGet() de AtomicInteger para actualizar la vida, permitiendo controlar con precisión cuándo un jugador pierde o gana vida, abarcando así las tres regiones críticas de sincronización.

Se realizaron tres pruebas diferentes para verificar la efectividad del refactor:

Se observa que la sumatoria total de vida se mantiene constante y se cumple el invariante esperado.

![first_test_refactor.png](img/first_test_refactor.png)

El sistema mantiene la vida correctamente sincronizada, sin valores por encima o por debajo del invariante.

![second_test_refactor.png](img/second_test_refactor.png)

Se confirma la estabilidad del sistema sin bloqueos ni inconsistencias en la vida de los jugadores.

![third_test_refactor.png](img/third_test_refactor.png)

Con esta aproximación se logró cumplir el invariante del sistema, es decir, que la suma total de vida de todos los jugadores se mantiene constante en ausencia de ataques simultáneos. Además, esta implementación evita la necesidad de sincronización anidada para el control de la vida durante la pelea, eliminando posibles deadlocks que antes causaban bloqueos y detenían la ejecución del programa.

Antes del cambio, los métodos getHealth y changeHealth al estar sincronizados de forma anidada podían causar bloqueos, especialmente cuando varios hilos intentaban acceder y modificar la vida simultáneamente. La solución basada en AtomicInteger y el uso de addAndGet() resolvió estos problemas y mejoró la estabilidad y consistencia del juego.

9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:
	* Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.
	* Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.

11. Para finalizar, implemente la opción STOP.