package iut.gon.agario.model;

import java.util.Random;

public enum Names {
    LIAM,
    JAMES,
    NOAH,
    LUCAS,
    TOM,
    OLIVIA,
    JULIA,
    SIMONE,
    EMMA,
    CHARLOTTE;

    public static Names getRandomName() {
        Random random = new Random();
        Names[] values = Names.values();
        int index = random.nextInt(values.length);
        return values[index];
    }
}
