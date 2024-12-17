package ia.perception;

import org.apache.commons.math3.geometry.Vector;

import java.util.List;

public record PerceptionValue(PerceptionType type, List<Double> vector) {
}
