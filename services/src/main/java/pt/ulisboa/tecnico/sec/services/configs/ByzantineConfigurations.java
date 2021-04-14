package pt.ulisboa.tecnico.sec.services.configs;

public class ByzantineConfigurations {
    public static final int RANGE = 1; // Grid Range
    public static final int MAX_BYZANTINE_USERS = 1;
    public static final int NUMBER_OF_USERS = (3 * MAX_BYZANTINE_USERS) + 1;
    public static final int MINIMUM_BYZ_QUORUM = (2 * MAX_BYZANTINE_USERS) + 1;
}
