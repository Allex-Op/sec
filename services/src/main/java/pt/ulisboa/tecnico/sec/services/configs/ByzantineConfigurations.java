package pt.ulisboa.tecnico.sec.services.configs;

public class ByzantineConfigurations {
    public static final int RANGE = 1; // Grid Range
    public static final int MAX_BYZANTINE_USERS = 1;
    public static final int REQUIRES_NON_BYZ_USERS = MAX_BYZANTINE_USERS + 1;
    public static final int MINIMUM_BYZ_QUORUM = 3; // > (N + f) / 2
}
