package com.jereprograma.myttrpg.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private ConsoleSystem console;
    private AssetManager assets;

    // Sistemas de juego (sin incluir consola)
    private GridSystem gridSystem;
    private InputSystem inputSystem;
    private LogicSystem logicSystem;
    private RenderSystem renderSystem;
    private final List<EcsSystem> gameSystems = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();

    @Override
    public void create() {
        // 1) Batch & Cámara
        batch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // 2) AssetManager & texturas
        assets = new AssetManager();
        List<String> tilePaths = List.of("tiles/grass.png", "tiles/dirt.png", "tiles/water.png", "tiles/sand.png");
        for (String p : tilePaths) assets.load(p, Texture.class);
        assets.load("player.png", Texture.class);
        assets.finishLoading();

        // 3) Skin para UI
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

        // 4) Mapa inicial
        entities.addAll(MapGenerator.randomMap(25, 18, tilePaths));

        // 5) Consola
        console = new ConsoleSystem(skin, camera);

        // 6) Sistemas de juego
        gridSystem = new GridSystem(camera, 25, 18);
        inputSystem = new InputSystem(console);
        logicSystem = new LogicSystem();
        renderSystem = new RenderSystem(batch, camera, assets);

        gameSystems.add(gridSystem);
        gameSystems.add(inputSystem);
        gameSystems.add(logicSystem);
        gameSystems.add(renderSystem);

        // 7) Listeners de comandos
        EventBus.register(RollCommand.class, cmd -> {
            int r = RollService.lanzar(cmd.notation());
            Gdx.app.log("Roll", "Resultado: " + r);
        });
        EventBus.register(SpawnCommand.class, cmd -> {
            Entity e = EntityPool.borrow();
            e.addComponent(new com.jereprograma.myttrpg.core.ecs.components.PositionComponent(cmd.x(), cmd.y()));
            e.addComponent(new com.jereprograma.myttrpg.core.ecs.components.RenderComponent("player.png"));
            entities.add(e);
            renderSystem.setEntities(entities);
        });
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
                Gdx.app.log("Save", "Mapa guardado: " + cmd.name());
            } catch (Exception e) {
                Gdx.app.error("Save", "Error", e);
            }
        });
        EventBus.register(LoadCommand.class, cmd -> {
            try {
                for (Entity old : entities) EntityPool.release(old);
                List<Entity> loaded = MapPersistenceService.load(cmd.name());
                entities.clear();
                entities.addAll(loaded);
                renderSystem.setEntities(entities);
                Gdx.app.log("Load", "Mapa cargado: " + cmd.name());
            } catch (Exception e) {
                Gdx.app.error("Load", "Error", e);
            }
        });

        // Pre-capturar ENTER para toggle
        Gdx.input.setCatchKey(Input.Keys.ENTER, true);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // 1) Dibujo del juego siempre
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for (EcsSystem sys : gameSystems) {
            sys.update(delta);
        }

        // 2) Toggle de consola
        boolean openKey = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        boolean closeKey = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        if (!console.isVisible() && openKey) console.toggle();
        else if (console.isVisible() && (openKey || closeKey)) console.toggle();

        // 3) Overlay de consola
        if (console.isVisible()) console.update(delta);

        // 4) Procesar comandos
        String line;
        while ((line = console.fetchCommand()) != null) {
            Command cmd = CommandParser.parse(line);
            EventBus.fire(cmd);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        skin.dispose();
        console.dispose();
        assets.dispose();
        // llamadas a dispose de sistemas que manejan recursos
        gridSystem.dispose();
        renderSystem.dispose();
    }

}
