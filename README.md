````markdown
# 🎲 MyTTRPGGame

**MyTTRPGGame** es un prototipo de tablero virtual construido con **libGDX** (Java 21), basado en una arquitectura **Entity-Component-System (ECS)** y con una **consola in-game** para control por comandos. Este README recoge desde la instalación y estructura hasta los detalles de Sprint 3 (pulido, atlas, culling, logging y pooling).

---

## 📋 Tabla de Contenidos

1. [📦 Estructura del Proyecto](#estructura-del-proyecto)
2. [🚀 Requisitos y Setup](#requisitos-y-setup)
3. [🛠️ Compilar y Testear](#compilar-y-testear)
4. [🎮 Cómo Jugar: Comandos](#cómo-jugar-comandos)
5. [🖼️ Atlas de Tiles & Sprites](#atlas-de-tiles--sprites)
6. [🏗️ Arquitectura ECS & Eventos](#arquitectura-ecs--eventos)
7. [✨ Sprint 3 – Pulido & Demo Básica](#sprint-3--pulido--demo-básica)
   - [3.1.2 Debug de errores de región](#312-debug-de-errores-de-región)
   - [3.2.1 Culling AABB de Tiles](#321-culling-aabb-de-tiles)
   - [3.3 Pooling con trazas seguras](#33-pooling-con-trazas-seguras)
8. [🔧 Limpieza de Recursos](#limpieza-de-recursos)
9. [📊 Cobertura & Jacoco](#cobertura--jacoco)
10. [📦 Empaquetado y Release](#empaquetado-y-release)
11. [📖 Futuras Mejoras](#futuras-mejoras)
12. [📄 Licencia](#licencia)

---

## 📦 Estructura del Proyecto

```text
MyTTRPGGame/
├── core/                  # Lógica → ECS, comandos, EventBus, I/O, tests, recursos
│   ├── src/main/java/...  # Código fuente principal
│   ├── src/main/resources/tiles/tiles.atlas, tiles.png
│   └── src/test/java/...   # Tests: EntityPoolTest, MapGeneratorAtlasTest, integración
├── desktop/               # Launcher LWJGL → DesktopLauncher
│   └── src/main/java/...
├── build.gradle           # Gradle multi-módulo + Jacoco
└── settings.gradle
```
````

- **core/**

  - **ECS**: `Entity`, `Component` (`record`), `EcsSystem`
  - **Systems**: `GridSystem`, `RenderSystem`, `LogicSystem`, `InputSystem`, `ConsoleSystem`
  - **Map & I/O**: `MapGenerator`, `MapPersistenceService`
  - **Commands & Events**: `CommandParser`, `EventBus`, handlers de `/roll`, `/spawn`, etc.
  - **Tests**: unitarios e integración, validaciones de atlas y pooling

- **desktop/**

  - Punto de entrada JVM que arranca `GameApp`

---

## 🚀 Requisitos y Setup

1. **Java 21+**
2. **Gradle 8.x**
3. Git, IDE (IntelliJ, Eclipse…)

```bash
# 1) Clonar
git clone https://github.com/tu-usuario/MyTTRPGGame.git
cd MyTTRPGGame

# 2) Build + Tests
./gradlew clean build

# 3) Correr Demo (desktop)
./gradlew :desktop:run
```

---

## 🛠️ Compilar y Testear

- **Build completo**: `./gradlew build`
- **Sólo tests core**: `./gradlew :core:test`
- **Jacoco Report**: `./gradlew :core:jacocoTestReport`

  - HTML en `core/build/reports/jacoco/html/index.html`

- **Cobertura mínima esperada**: ≥ 80 %

---

## 🎮 Cómo Jugar: Comandos

| Comando              | Descripción                                                |      |         |                                                             |
| -------------------- | ---------------------------------------------------------- | ---- | ------- | ----------------------------------------------------------- |
| `/roll dN`           | Lanza un dado de N caras (e.g. `/roll d20`).               |      |         |                                                             |
| `/spawn X Y`         | Coloca una entidad en la celda `(X,Y)`.                    |      |         |                                                             |
| \`/move \<north      | south                                                      | east | west>\` | Mueve la última entidad (jugador) en la dirección indicada. |
| `/describe [target]` | Muestra posición del jugador (o de otro `target`).         |      |         |                                                             |
| `/save [name]`       | Guarda estado en `name.json` (`default` si omites `name`). |      |         |                                                             |
| `/load name`         | Carga `name.json` (si falla, permanece el estado actual).  |      |         |                                                             |
| cualquier otro texto | `Comando desconocido: <texto>`.                            |      |         |                                                             |

**Consola In-Game**

- Toggle con **ENTER**, cierra con **ESC**
- Historial: flechas ↑↓
- Scroll automático, semi-transparencia
- Al abrir, el campo captura automáticamente el foco

---

## 🖼️ Atlas de Tiles & Sprites

- **Archivo**: `core/src/main/resources/tiles/tiles.atlas`
- **Imagen**: `tiles.png` (256×64) con regiones:

  ```
  dirt   @ (2,2)   size=32×32
  grass  @ (36,2)
  player @ (70,2)
  sand   @ (104,2)
  water  @ (138,2)
  ```

- **MapGenerator.randomMap** recibe la lista de keys (`"grass"`, `"dirt"`, …) y crea entidades con `new RenderComponent(spriteKey)`.

> **Tip**: al agregar nuevos tiles, edita `tiles.png` + `tiles.atlas` (con TexturePacker) y añade el key a tu lista en `GameApp` o en tests.

---

## 🏗️ Arquitectura ECS & Eventos

1. **Entity**

   - UUID único, mapa de `<Class<? extends Component>, Component>`

2. **Component** (`interface`)

   - Datos puros, todos definidos como **`record`**

3. **System** (`interface EcsSystem`)

   - `void update(float delta)`
   - Sistemas principales:

     - **GridSystem** – dibuja líneas de celda (32 px)
     - **RenderSystem** – renderiza `TextureRegion` desde atlas
     - **LogicSystem** – maneja lógica de comandos/resultados
     - **InputSystem** – canaliza input UI → CommandParser
     - **ConsoleSystem** – UI de consola con Scene2D

4. **EventBus**

   - Registro de handlers: `EventBus.register(Command.class, cmd -> …)`
   - Fire de Events: `EventBus.fire(new RollResultEvent(value))`

---

## ✨ Sprint 3 – Pulido & Demo Básica

### 3.1.2 Debug de errores de región

En `RenderSystem`, si `atlas.findRegion(...) == null` ahora se lanza:

```java
Gdx.app.error("RenderSystem",
    String.format(
        "Región no encontrada: '%s' en coords=(%d,%d)  camBounds=[%.1f,%.1f→%.1f,%.1f]",
        key, pos.x(), pos.y(), camLeft, camBottom, camRight, camTop
    )
);
```

> Agrupamos _spriteKey_, _coordenadas de tile_ y _bounds de cámara_.

### 3.2.1 Culling AABB de Tiles

Antes de cada `batch.draw`:

```java
if (cellX + CELL_SIZE < camLeft ||
    cellX > camRight ||
    cellY + CELL_SIZE < camBottom ||
    cellY > camTop) {
    continue; // tile fuera de cámara
}
batch.draw(region, cellX, cellY);
```

### 3.3 Pooling con trazas seguras

**EntityPool.java** ahora:

- `borrow()` / `release()` hacen `Gdx.app.debug(...)` solo si `Gdx.app != null`.
- `size()` no toca `Gdx.app`, evitando NPE en tests.
- Tests unitarios cubren creación, release, limpieza y reutilización.

---

## 🔧 Limpieza de Recursos

Todos los recursos se liberan en `GameApp.dispose()`:

```java
@Override
public void dispose() {
    batch.dispose();
    skin.dispose();
    console.dispose();
    assets.dispose();
    gridSystem.dispose();
    renderSystem.dispose();
}
```

Cada sistema implementa `dispose()` si gestiona recursos propios.

---

## 📊 Cobertura & Jacoco

- Plugin Jacoco en **core**
- Generar report: `./gradlew :core:jacocoTestReport`
- Report HTML: `core/build/reports/jacoco/html/index.html`
- Cobertura actual: **100%**, meta mínima: **80%**

---

## 📦 Empaquetado y Release

1. **Generar JAR**

   ```bash
   ./gradlew :desktop:dist
   ```

   Produce `desktop/build/libs/desktop-1.0.jar`.

2. **Ejecutar JAR**

   ```bash
   java -jar desktop-1.0.jar
   ```

3. **Assets**

   - Incluir `tiles.atlas` y `tiles.png` en el classpath.

4. **GitHub Release**

   - Subir `desktop-1.0.jar`, `README.md`, GIF demo y CHANGELOG en la release v0.1.

---

## 📖 Futuras Mejoras

- **Multiplayer** con Netty/libGDX-net
- **Modo Edición** (undo/redo)
- **Persistencia avanzada**: versionado de mapas
- **IA & Plugins**: integraciones GPT-style
- **Optimización**: frustum culling preciso, batching por tileset

---

## 📄 Licencia

**MIT License** © 2025 Jeremías Rivelli
Consulta [`LICENSE`](LICENSE) para detalles.

---

> “Juego rápido, código limpio, depuración instantánea.” 🚀

```

```
