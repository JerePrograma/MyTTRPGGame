// src/main/java/com/jereprograma/myttrpg/core/GameApp.java
package com.jereprograma.myttrpg.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jereprograma.myttrpg.core.commands.*;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.system.*;
import com.jereprograma.myttrpg.core.events.EventBus;
import com.jereprograma.myttrpg.core.map.MapGenerator;
import com.jereprograma.myttrpg.core.services.MapPersistenceService;
import com.jereprograma.myttrpg.core.services.RollService;

import java.util.ArrayList;
import java.util.List;

public class GameApp extends ApplicationAdapter {
    public static final int CELL_SIZE = 32;

    private SpriteBatch batch;
    private Skin skin;
    private ConsoleSystem consoleSystem;
    private AssetManager assets;

    private final List<EcsSystem> systems = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();

    // Paleta de sprites para el mapa
    private static final List<String> TILE_SPRITES = List.of(
            "tiles/grass.png",
            "tiles/dirt.png",
            "tiles/water.png",
            "tiles/sand.png"
    );

    @Override
    public void create() {
        // 1) Inicialización básica
        batch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // 2) AssetManager y carga de texturas
        assets = new AssetManager();
        for (String path : TILE_SPRITES) {
            assets.load(path, Texture.class);
        }
        assets.load("player.png", Texture.class);
        assets.finishLoading();

        // 3) Skin y atlas de UI
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

        // 4) Generar mapa inicial
        entities.addAll(MapGenerator.randomMap(25, 18, TILE_SPRITES));

        // 5) Crear sistemas
        consoleSystem = new ConsoleSystem(skin, camera);
        RenderSystem renderSystem = new RenderSystem(batch, camera, assets);

        systems.add(consoleSystem);
        systems.add(new GridSystem(camera, 25, 18));
        systems.add(new InputSystem());
        systems.add(new LogicSystem());
        systems.add(renderSystem);

        // 6) Listeners de comandos
        EventBus.register(RollCommand.class, cmd -> {
            int result = RollService.lanzar(cmd.notation());
            Gdx.app.log("Roll", "Resultado: " + result);
        });

        EventBus.register(SpawnCommand.class, cmd -> {
            Entity e = EntityPool.borrow();
            e.addComponent(new com.jereprograma.myttrpg.core.ecs.components.PositionComponent(cmd.x(), cmd.y()));
            e.addComponent(new com.jereprograma.myttrpg.core.ecs.components.RenderComponent("player.png"));
            entities.add(e);
            renderSystem.setEntities(entities);
        });

        // Entidad jugador inicial
        Entity player = new Entity();
        player.addComponent(new com.jereprograma.myttrpg.core.ecs.components.PositionComponent(1, 1));
        player.addComponent(new com.jereprograma.myttrpg.core.ecs.components.RenderComponent("player.png"));
        entities.add(player);
        renderSystem.setEntities(entities);

        EventBus.register(DescribeCommand.class, cmd -> {
            Entity pl = entities.get(entities.size() - 1);
            var pos = pl.getComponent(com.jereprograma.myttrpg.core.ecs.components.PositionComponent.class);
            Gdx.app.log("Describe", "Jugador en x=" + pos.x() + ", y=" + pos.y());
        });

        EventBus.register(MoveCommand.class, cmd -> {
            Entity pl = entities.get(entities.size() - 1);
            var pos = pl.getComponent(com.jereprograma.myttrpg.core.ecs.components.PositionComponent.class);
            int x = pos.x(), y = pos.y();
            switch (cmd.dir()) {
                case UP:
                    y++;
                    break;
                case DOWN:
                    y--;
                    break;
                case LEFT:
                    x--;
                    break;
                case RIGHT:
                    x++;
                    break;
            }
            pl.removeComponent(com.jereprograma.myttrpg.core.ecs.components.PositionComponent.class);
            pl.addComponent(new com.jereprograma.myttrpg.core.ecs.components.PositionComponent(x, y));
        });

        EventBus.register(SaveCommand.class, cmd -> {
            try {
                MapPersistenceService.save(cmd.name(), entities);
                Gdx.app.log("Save", "Mapa guardado como " + cmd.name() + ".json");
            } catch (Exception e) {
                Gdx.app.error("Save", "Error al guardar", e);
            }
        });

        EventBus.register(LoadCommand.class, cmd -> {
            try {
                // liberar entidades actuales
                for (Entity old : entities) {
                    EntityPool.release(old);
                }
                // cargar nuevo mapa
                List<Entity> loaded = MapPersistenceService.load(cmd.name());
                entities.clear();
                entities.addAll(loaded);
                renderSystem.setEntities(entities);
                Gdx.app.log("Load", "Mapa " + cmd.name() + " cargado");
            } catch (Exception e) {
                Gdx.app.error("Load", "Error al cargar", e);
            }
        });
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (EcsSystem sys : systems) {
            sys.update(delta);
        }

        String line;
        while ((line = consoleSystem.fetchCommand()) != null) {
            Gdx.app.log("Console", "Comando ingresado: " + line);
            Command cmd = CommandParser.parse(line);
            EventBus.fire(cmd);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        skin.dispose();
        consoleSystem.dispose();
        assets.dispose();
    }
}
