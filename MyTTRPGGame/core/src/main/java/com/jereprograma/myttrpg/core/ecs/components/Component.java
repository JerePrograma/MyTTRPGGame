// core/src/main/java/com/jereprograma/myttrpg/core/ecs/Component.java
package com.jereprograma.myttrpg.core.ecs.components;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public interface Component {
}
