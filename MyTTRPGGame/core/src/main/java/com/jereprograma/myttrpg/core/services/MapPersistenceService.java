package com.jereprograma.myttrpg.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MapPersistenceService {
    private static final ObjectMapper M = new ObjectMapper();

    /**
     * Guarda sólo los datos esenciales de cada entidad.
     */
    public static void save(String name, List<Entity> entities) throws Exception {
        var dtos = entities.stream()
                .map(e -> {
                    var p = e.getComponent(PositionComponent.class);
                    var r = e.getComponent(RenderComponent.class);
                    return new EntityDto(p.x(), p.y(), r.spritePath());
                })
                .collect(Collectors.toList());
        M.writeValue(new File(name + ".json"), dtos);
    }

    /**
     * Carga el JSON de DTOs y reconstruye las entidades.
     */
    public static List<Entity> load(String name) throws Exception {
        // Definimos el tipo objetivo como List<EntityDto>
        List<EntityDto> dtos = M.readValue(
                new File(name + ".json"),
                M.getTypeFactory().constructCollectionType(List.class, EntityDto.class)
        );

        // Ahora sí dtos es una List<EntityDto> y podemos usar stream()
        return dtos.stream()
                .map(d -> {
                    Entity e = new Entity();
                    e.addComponent(new PositionComponent(d.x, d.y));
                    e.addComponent(new RenderComponent(d.spritePath));
                    return e;
                })
                .collect(Collectors.toList());
    }

}
