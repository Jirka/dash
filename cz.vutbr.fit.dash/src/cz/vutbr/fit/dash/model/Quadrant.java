package cz.vutbr.fit.dash.model;

public enum Quadrant {
	I(0),
	II(1),
	III(2),
	IV(3);
	
	private int index;

	private Quadrant(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
