import engine.Team;
import engine.map.*;
import engine.object.Flag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class GameMapTest {

    @Nested
    class whenLoadingMapFromFile {
        @Test
        void forANullFile() {
            try{
                GameMap gameMap = GameMap.loadFile((File)null);
            }
            catch (IOException e){
                assert true;
                return;
            }
            assert false;
        }

        @Test
        void forAValidFile() {
            File file = new File("dust.txt");
            GameMap gameMap;
            try{
                gameMap = GameMap.loadFile(file);
                var cells = gameMap.getCells();

                int rows = cells.size();
                int columns = cells.getFirst().size();

                // System.out.printf("hauteur : %d\nlargeur : %d\n", rows, columns);

                StringBuilder cellsTypeString = new StringBuilder();
                StringBuilder cellsTeamString = new StringBuilder();
                for (int row = 0; row < rows; row++) {
                    for (int column = 0; column < columns; column++) {
                        char type = '/';
                        char team = '/';
                        Cell cell = cells.get(row).get(column);

                        if(cell.getClass().equals(Wall.class)){
                            type = '#';
                            team = '0';
                            //System.out.println(row+" - "+column+" : "+" : "+team+" - "+cell.getClass()+" : "+type);
                        }
                        else {
                            if (cell.getClass().equals(Ground.class)) {
                                type = '.';
                                team = Team.teamToChar(((Ground)cell).getTeam());
                                //System.out.println(row+" - "+column+" : "+((Ground) cell).getTeam()+" : "+team+" - "+cell.getClass()+" : "+type);
                            } else if (cell.getClass().equals(SpawningCell.class)) {
                                type = 'O';
                                team = Team.teamToChar(((SpawningCell)cell).getTeam());
                                //System.out.println(row+" - "+column+" : "+((SpawningCell) cell).getTeam()+" : "+team+" - "+cell.getClass()+" : "+type);
                            }
                        }
                        cellsTypeString.append(type);
                        cellsTeamString.append(team);
                        // System.out.printf("%s", team);
                    }
                    cellsTypeString.append('\n');
                    cellsTeamString.append('\n');
                    // System.out.print("\n");
                }
                assert cellsTypeString.toString().equals(
                        """
                                #............................#
                                #............................#
                                #............................#
                                #.....O................O.....#
                                #.............#..............#
                                OO............#.............OO
                                OO..........................OO
                                OO............#.............OO
                                #.............#..............#
                                #............................#
                                #.....#................#.....#
                                #.....#................#.....#
                                #O..........................O#
                                #OO........................OO#
                                """);
                assert cellsTeamString.toString().equals(
                        """
                                011111111111110222222222222220
                                011111111111110222222222222220
                                011111111111110222222222222220
                                011111111111110222222222222220
                                011111111111100022222222222220
                                111111111111100022222222222222
                                111111111111000002222222222222
                                111111111111100022222222222222
                                011111111111100022222222222220
                                011111111111110222222222222220
                                011111011111110222222220222220
                                011111011111110222222220222220
                                011111111111110222222222222220
                                011111111111110222222222222220
                                """);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                assert false;
            }
        }
    }

    @Nested
    class whenLoadingMapFromFileName {
        @Test
        void forANullFile() {
            try{
                GameMap gameMap = GameMap.loadFile((String) null);
            }
            catch (IOException e){
                assert true;
                return;
            }
            assert false;
        }

        @Test
        void forAValidFile() {
            GameMap gameMap;
            try{
                gameMap = GameMap.loadFile("dust.txt");
                var cells = gameMap.getCells();

                int rows = cells.size();
                int columns = cells.getFirst().size();

                // System.out.printf("hauteur : %d\nlargeur : %d\n", rows, columns);

                StringBuilder cellsTypeString = new StringBuilder();
                StringBuilder cellsTeamString = new StringBuilder();
                for (int row = 0; row < rows; row++) {
                    for (int column = 0; column < columns; column++) {
                        char type = '/';
                        char team = '/';
                        Cell cell = cells.get(row).get(column);

                        if(cell.getClass().equals(Wall.class)){
                            type = '#';
                            team = '0';
                            //System.out.println(row+" - "+column+" : "+" : "+team+" - "+cell.getClass()+" : "+type);
                        }
                        else {
                            if (cell.getClass().equals(Ground.class)) {
                                type = '.';
                                team = Team.teamToChar(((Ground)cell).getTeam());
                                //System.out.println(row+" - "+column+" : "+((Ground) cell).getTeam()+" : "+team+" - "+cell.getClass()+" : "+type);
                            } else if (cell.getClass().equals(SpawningCell.class)) {
                                type = 'O';
                                team = Team.teamToChar(((SpawningCell)cell).getTeam());
                                //System.out.println(row+" - "+column+" : "+((SpawningCell) cell).getTeam()+" : "+team+" - "+cell.getClass()+" : "+type);
                            }
                        }
                        cellsTypeString.append(type);
                        cellsTeamString.append(team);
                        // System.out.printf("%s", team);
                    }
                    cellsTypeString.append('\n');
                    cellsTeamString.append('\n');
                    // System.out.print("\n");
                }
                assert cellsTypeString.toString().equals(
                        """
                                #............................#
                                #............................#
                                #............................#
                                #.....O................O.....#
                                #.............#..............#
                                OO............#.............OO
                                OO..........................OO
                                OO............#.............OO
                                #.............#..............#
                                #............................#
                                #.....#................#.....#
                                #.....#................#.....#
                                #O..........................O#
                                #OO........................OO#
                                """);
                assert cellsTeamString.toString().equals(
                        """
                                011111111111110222222222222220
                                011111111111110222222222222220
                                011111111111110222222222222220
                                011111111111110222222222222220
                                011111111111100022222222222220
                                111111111111100022222222222222
                                111111111111000002222222222222
                                111111111111100022222222222222
                                011111111111100022222222222220
                                011111111111110222222222222220
                                011111011111110222222220222220
                                011111011111110222222220222220
                                011111111111110222222222222220
                                011111111111110222222222222220
                                """);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                assert false;
            }
        }
    }
}
