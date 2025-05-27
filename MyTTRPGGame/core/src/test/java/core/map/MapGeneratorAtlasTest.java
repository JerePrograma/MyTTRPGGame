// core/src/test/java/com/jereprograma/myttrpg/core/map/MapGeneratorAtlasTest.java
package core.map;

import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;
import com.jereprograma.myttrpg.core.map.MapGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MapGeneratorAtlasTest {

    private Set<String> loadAtlasRegionNames() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("tiles/tiles.atlas");
        assertNotNull(in, "No se encontró tiles/tiles.atlas en el classpath");

        try (var reader = new BufferedReader(new InputStreamReader(in))) {
            Set<String> regions = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                // sólo líneas sin indentación y sin ':', y que no sean nombres de fichero
                line = line.trim();
                if (!line.isBlank() && !line.startsWith(" ") && !line.contains(":") && !line.contains(".")) {
                    regions.add(line);
                }
            }
            return regions;
        }
    }

    @Test
    void allRandomMapRegionsMustExistInAtlas() throws Exception {
        Set<String> atlasRegions = loadAtlasRegionNames();

        List<String> wanted = List.of("grass", "dirt", "water", "sand", "player");
        var tiles = MapGenerator.randomMap(5, 4, wanted);

        List<String> missing = tiles.stream()
                .map(e -> e.getComponent(RenderComponent.class).spritePath())
                .distinct()
                .filter(key -> !atlasRegions.contains(key))
                .toList();

        assertTrue(missing.isEmpty(), () ->
                "Estas regiones NO están en tiles.atlas: " + missing +
                        "\nRegiones cargadas del atlas: " + atlasRegions
        );
    }
}
