package iut.gon.agario;

public class Config {
    /** Ratio of size A/B for Player A to be able to absorb Player B*/
    public static double ABSORPTION_RATIO = 1.33;

    /** The fraction of Player B that must be covered by Player A to absorb it*/
    public static double MERGE_OVERLAP = 0.33;

    /***/
    public static double DECAY_FACTOR = 5.0;

    /***/
    public static long SPEED_DECAY_DURATION = 1300;

    /***/
    public static long CONTROL_RADIUS = 1000;

    /**The minimum speed of a Player*/
    public static double MIN_SPEED = 0;

    /**The default mass of a pellet*/
    public static double PELLET_MASS = 5;

    /**The default top speed of a Player*/
    public static double INITIAL_MAX_SPEED = 100.0;

    /***/
    public static double COEFFICIENT_ATTENUATION = 0.3;

    /**The minimum size of a Player for it to be able to split itself*/
    public static long MINIMUM_SPLIT = 40;

    /**The default size increase of MassBuffPellet*/
    public static double MASS_BUFF_MULTIPLIER = 1.33;

    /**The default size decrease of MassNerfPellet*/
    public static double MASS_NERF_MULTIPLIER = 0.66;

    /**The default speed increase of SpeedBuffPellet*/
    public static double SPEED_BUFF_MULTIPLIER = 1.33;

    /**The default speed decrease of SpeedNerfPellet*/
    public static double SPEED_NERF_MULTIPLIER = 0.66;

    /**The default zoom of the camera*/
    public static double BASE_ZOOM = 100;

}
