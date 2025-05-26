package com.jereprograma.myttrpg.core.services;

import java.util.Random;

public class RollService {
    private static final Random RNG = new Random();

    /**
     * Interpreta notación simple como 'd20', 'd6', devuelve valor aleatorio entre 1 y N.
     */
    public static int lanzar(String notation) {
        if (notation.startsWith("d")) {
            try {
                int sides = Integer.parseInt(notation.substring(1));
                return RNG.nextInt(sides) + 1;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
