package data.generation.basic.number;

public class IntegerGenerator extends NumberGenerator<Integer> {
	private final int _highBound;
	private final int _lowBound;

	private final int _boundFactor;

	public IntegerGenerator( int highBound, int lowBound ){
		this._highBound = highBound;
		this._lowBound  = lowBound;

		this._boundFactor = highBound - lowBound;
	}

	@Override
	public Integer generateValue() {
		return (int)(Math.random() * this._boundFactor + this._lowBound);
	}
}
