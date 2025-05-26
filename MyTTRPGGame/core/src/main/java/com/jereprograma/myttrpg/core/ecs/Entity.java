// core/src/main/java/com/jereprograma/myttrpg/core/ecs/Entity.java
package com.jereprograma.myttrpg.core.ecs;

import com.jereprograma.myttrpg.core.ecs.components.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Una entidad es un contenedor de componentes identificada por un UUID.
 */
public class Entity {
    private final UUID id;
    private final Map<Class<? extends Component>, Component> components;

    /**
     * Crea una nueva entidad con un ID aleatorio.
     */
    public Entity() {
        this.id = UUID.randomUUID();
        this.components = new ConcurrentHashMap<>();
    }

    /**
     * @return el identificador único de esta entidad.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Añade o reemplaza un componente.
     *
     * @param component el componente a añadir
     * @param <T>       tipo del componente
     */
    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    /**
     * Recupera un componente por su clase.
     *
     * @param componentClass la clase del componente a buscar
     * @param <T>            tipo del componente
     * @return el componente, o null si no existe
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return (T) components.get(componentClass);
    }

    /**
     * Elimina el componente de la clase dada.
     *
     * @param componentClass la clase del componente a eliminar
     */
    public void removeComponent(Class<? extends Component> componentClass) {
        components.remove(componentClass);
    }

    /**
     * @return colección inmutable de todos los componentes de esta entidad.
     */
    public Collection<Component> getAllComponents() {
        return Collections.unmodifiableCollection(components.values());
    }
}
