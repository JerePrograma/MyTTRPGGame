# MyTTRPGGame

Este proyecto es un prototipo de juego de mesa virtual construido con **libGDX**, utilizando una arquitectura **ECS** (Entity-Component-System) y una **consola in-game** para comandos de depuración y control.

---

## 📦 Estructura del Proyecto

```
MyTTRPGGame/
├── core/          # Lógica principal (ECS, comandos, servicios, tests)
│   ├── src/main/java...
│   └── src/test/java...
├── desktop/       # Launcher JVM (LWJGL)
│   └── src/main/java...
├── build.gradle   # Configuración Gradle multi-módulo + Jacoco
└── settings.gradle
```

* **core/**: ECS, `CommandParser`, `EventBus`, servicios de I/O y tests.
* **desktop/**: `DesktopLauncher` que inicia `GameApp`.

---

## 🚀 Requisitos y Setup

1. **Java 21** o superior.
2. **Gradle 8.x**.

```bash
# Clonar repositorio
git clone https://github.com/tu-usuario/MyTTRPGGame.git
cd MyTTRPGGame

# Construir y ejecutar tests
./gradlew build

# Ejecutar demo desktop
./gradlew :desktop:run
```

---

## 🎮 Comandos Soportados

| Comando                            | Descripción                                                               |
| ---------------------------------- | ------------------------------------------------------------------------- |
| `/roll dN`                         | Lanza un dado de N caras, p.ej. `/roll d20`.                              |
| `/spawn X Y`                       | Crea una entidad en la celda `(X,Y)`.                                     |
| `/move <north\|south\|east\|west>` | Mueve la entidad jugador en la dirección indicada.                        |
| `/describe [target]`               | Muestra posición del jugador (o de `target`, pendiente).                  |
| `/save [name]`                     | Guarda estado en `name.json`. `/save` usa `default`.                      |
| `/load name`                       | Carga el fichero `name.json` si existe; en error, mantiene estado previo. |
| cualquier otro texto               | Muestra `Comando desconocido: <texto>`.                                   |

---

## 🔧 Consola In-Game

* **Abrir/Cerrar**: presiona `ENTER` para alternar; `ESC` cierra.
* **Historial**: flechas `↑` y `↓` para navegar comandos previos.
* **Salida**: panel de texto con scroll automático.
* **Foco**: al abrir, el campo recibe teclado automáticamente.

---

## 🏗️ Limpieza de Recursos

Todos los sistemas que instancian recursos (ShapeRenderer, Stage, AssetManager, SpriteBatch) implementan `dispose()`:

```java
public void dispose() {
    // liberar recursos
}
```

En `GameApp.dispose()` se invoca:

```java
batch.dispose();
skin.dispose();
console.dispose();
assets.dispose();
gridSystem.dispose();
renderSystem.dispose();
```

---

## 🧪 Cobertura de Tests y Jacoco

Hemos integrado el plugin **Jacoco** en `core`. Para generar el informe:

```bash
./gradlew :core:jacocoTestReport
```

El HTML resultante está en `core/build/reports/jacoco/html/index.html`.
Objetivo: **>= 80%** de cobertura.

---

## 🛠️ Pruebas Manuales (Sprint 2)

1. **Flujo completo**: `/roll`, `/spawn`, `/move`, `/describe`, `/save`, `/load`, `/foo`.

   * Verifica respuestas en consola y cambios en pantalla.
2. **Visibilidad**: abrir/cerrar, foco y captura de entrada.
3. **Historial**: navegación con flechas.
4. **Scroll & wrapping**: más de 8 líneas y comandos largos.
5. **Diseño**: comprobá semi-transparencia y tamaño (400×200 px).
6. **Errores I/O**: `/load nombreInexistente` no debe resetear mundo.

---

## 📄 Licencia

Este proyecto está bajo licencia **MIT**. Consulta `LICENSE` para más detalles.