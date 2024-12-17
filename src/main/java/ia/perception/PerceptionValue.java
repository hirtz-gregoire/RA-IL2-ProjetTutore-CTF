package ia.perception;

import java.util.List;

public record PerceptionValue(PerceptionType type, List<Double> vector) {
}
