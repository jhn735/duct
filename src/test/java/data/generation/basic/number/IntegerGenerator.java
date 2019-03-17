package data.generation.basic.number;

public class IntegerGenerator extends NumberGenerator<Integer> {
	@Override
	public Integer generateValue() {
		return (int)Math.random();
	}
}
