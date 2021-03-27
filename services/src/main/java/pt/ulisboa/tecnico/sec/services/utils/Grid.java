package pt.ulisboa.tecnico.sec.services.utils;

import java.util.Arrays;

public class Grid {

    private static final int user1=1;
    private static final int user2=2;
    private static final int user3=3; // Byzantine
    private static final int user4=4;

    // 0 means position empty
    public static int[][] locations = {{0,user3,user2},
                                      {user4,user1,0},
                                      {0,0,0}};

    public static void main(String[] args){
        generateGrid();
        getUserLocation();

    }

    public static void generateGrid(){

        for(int x = 0; x < locations.length; x++){
            for (int y = 0; y < locations.length; y++){
                System.out.print(locations[x][y]+"\t");
            }
            System.out.println();
        }
    }

    public static void getUserLocation() {
        int[] position = null;
        int userToFind=user1;
        for ( int i = 0; i < locations.length; ++i ) {
            for ( int j = 0; j < locations.length; ++j ) {
                if ( locations[i][j] == userToFind ) {
                    position= new int[] { i, j };
                    System.out.print("User:"+userToFind+ " in coordinates:" + Arrays.toString(position));
                }
            }
        }
    }
}
