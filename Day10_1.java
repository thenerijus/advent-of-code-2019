import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Part one of
 * https://adventofcode.com/2019/day/10
 *
 * @author Nerijus
 */
public class Day10_1 {
    public static void main(String[] args) {
        Map.Entry<Position, List<AsteroidInfo>> result = new Day10_1().getResult();
        System.out.println("Max asteroids can be detected: " + result.getValue().size() + " at (" + result.getKey().x + "," + result.getKey().y + ")");
    }

    private Map.Entry<Position, List<AsteroidInfo>> getResult() {
        return getAsteroidPositions(getPuzzleInput())
                .stream()
                .collect(Collectors.toMap(Function.identity(), a -> getVisibleAsteroids(getPuzzleInput(), a)))
                .entrySet()
                .stream()
                .max((e1, e2) -> Integer.compare(e1.getValue().size(), e2.getValue().size()))
                .orElseThrow(() -> new IllegalStateException("Could not find max visible asteroids"));
    }

    List<AsteroidInfo> getVisibleAsteroids(String[][] map, Position station) {
        return getAsteroidPositions(map)
                .stream()
                .filter(a -> !a.isSame(station))
                .map(a -> {
                    AsteroidInfo info = new AsteroidInfo(a);
                    info.angle = angle(station, a);
                    info.lineOfSight = lineOfSight(station, a);
                    info.distance = distance(station, a);
                    return info;
                })
                .collect(Collectors.groupingBy(i -> i.lineOfSight, Collectors.toList()))
                .values()
                .stream()
                .map(this::closest)
                .sorted(Comparator.comparing(AsteroidInfo::getAngle))
                .collect(Collectors.toList());
    }

    // calculate a 'line of sight' identifier, that should be equal for the same directional line
    private String lineOfSight(Position station, Position asteroid) {
        // check if on same x or y first
        if (station.x.equals(asteroid.x)) {
            return sign(asteroid.y - station.y) + "x";
        }
        if (station.y.equals(asteroid.y)) {
            return sign(asteroid.x - station.x) + "y";
        }
        // forms triangle. Get third point
        Position t = new Position(station.x, asteroid.y);

        int line1 = t.x - asteroid.x;
        int line2 = t.y - station.y;
        float ratio = (float)line1 / (float)line2;

        return sign(line1) + sign(line2) + ratio;
    }

    private String sign(int number) {
        return number < 0? "-" : "+";
    }

    private double distance(Position station, Position asteroid) {
        return Math.abs(asteroid.x - station.x) + Math.abs(asteroid.y - station.y);
    }

    private double angle(Position station, Position asteroid) {
        if (station.y.equals(asteroid.y)) {
            if (asteroid.x < station.x) {
                // above
                return 0;
            } else {
                // below
                return 180;
            }
        } else if (station.x.equals(asteroid.x)) {
            if (asteroid.y > station.y) {
                // to the right
                return 90;
            } else {
                // to the left
                return 270;
            }
        } else {
            // calculated
            if (asteroid.x < station.x && asteroid.y > station.y) {
                return Math.toDegrees(Math.atan(((double) asteroid.y - station.y) / ((double) station.x - asteroid.x)));
            } else if (asteroid.x > station.x && asteroid.y > station.y) {
                return Math.toDegrees(Math.atan(((double) asteroid.x - station.x) / ((double) asteroid.y - station.y))) + 90;
            } else if (asteroid.x > station.x) {
                return Math.toDegrees(Math.atan(((double) station.y - asteroid.y) / ((double) asteroid.x - station.x))) + 180;
            } else {
                return Math.toDegrees(Math.atan(((double) station.x - asteroid.x) / ((double) station.y - asteroid.y))) + 270;
            }
        }
    }

    private AsteroidInfo closest(List<AsteroidInfo> asteroids) {
        if (asteroids.size() == 1) {
            return asteroids.get(0);
        }
        return asteroids.stream().min(Comparator.comparing(AsteroidInfo::getDistance)).orElseThrow(() -> new IllegalStateException("Could not find closest"));
    }

    String[][] getPuzzleInput() {
        return getPuzzleInput("Day10");
    }

    String[][] getPuzzleInput(String fileName) {
        List<String> rows = Inputs.readStrings(fileName);
        String[][] map = new String[rows.size()][rows.get(0).length()];
        for (int x = 0; x < rows.size(); x++) {
            String[] row = rows.get(x).split("");
            for (int y = 0; y < row.length; y++) {
                map[x][y] = row[y];
            }
        }
        return map;
    }

    List<Position> getAsteroidPositions(String[][] map) {
        List<Position> positions = new ArrayList<>();
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                String type = map[x][y];
                if ("#".equals(type)) {
                    positions.add(new Position(x, y));
                }
            }
        }
        return positions;
    }

    static class Position {
        Integer x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean isSame(Position position) {
            return position.x.equals(x)
                    && position.y.equals(y);
        }

        @Override
        public String toString() {
            return "(x=" + x + ",y=" + y + ')';
        }
    }

    static class AsteroidInfo {
        Position position;
        String lineOfSight;
        double distance;
        double angle;

        public AsteroidInfo(Position position) {
            this.position = position;
        }

        public double getDistance() {
            return distance;
        }

        public double getAngle() {
            return angle;
        }

        @Override
        public String toString() {
            return "(" + position.x + "," + position.y + "): " +
                    " lineOfSight='" + lineOfSight + "'" +
                    ", distance=" + distance +
                    ", angle=" + angle;
        }
    }
}
