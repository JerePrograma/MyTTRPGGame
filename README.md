# MyTTRPGGame

Este proyecto es un prototipo de juego de mesa virtual construido con libGDX, utilizando una arquitectura ECS (Entity-Component-System) y una consola in-game para comandos de depuración y control.

## 📦 Estructura del Proyecto

* `core/` - Lógica principal del juego (ECS, comandos, servicios)
* `desktop/` - Launcher para JVM usando LWJGL

## 🚀 Cómo Compilar y Ejecutar

1. **Requisitos**:

   * Java 21
   * Gradle 8.x

2. **Clonar repositorio**:

   ```bash
   git clone https://github.com/tu-usuario/MyTTRPGGame.git
   cd MyTTRPGGame
   ```

3. **Construir**:

   ```bash
   ./gradlew build
   ```

4. **Ejecutar**:

   ```bash
   ./gradlew desktop:run
   ```

## 🎮 Comandos Soportados

* `/roll dN` - Lanza un dado de N caras (e.g. `/roll d20`)
* `/spawn X Y` - Crea una entidad en la posición (X,Y)
* `/move <north|south|east|west>` - Mueve la entidad jugador
* `/describe [target]` - Muestra información de la entidad jugador (o de target)
* `/save [name]` - Guarda el estado actual del mapa en `name.json` (`/save` usa `default`)
* `/load name` - Carga un mapa previamente guardado

## 🔧 Consola In-Game

* **Abrir/Cerrar**: Presiona `ENTER` sin texto o `ESC`
* **Historial**: Flecha `↑` y `↓` para navegar comandos previos
* **Salida**: Panel con scroll que muestra comandos y respuestas

## 🛠️ Limpieza de Recursos

Todos los sistemas que instancian recursos (ShapeRenderer, Stage, AssetManager, SpriteBatch) disponen de un método `dispose()`:

```java
public void dispose() {
    // liberar recursos
}
```

Y en `GameApp.dispose()` se invocan:

```java
batch.dispose();
skin.dispose();
console.dispose();
assets.dispose();
gridSystem.dispose();
```

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Puedes ver `LICENSE` para más detalles.
