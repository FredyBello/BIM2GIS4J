package co.edu.udistrital.bim2gis4j.bim2gisbm;

public class Rectangulo {
	
	Coordenada position_location;
	Coordenada position_refDirection;
	
	double XDim;
	double YDim;
	
	public Rectangulo(){
		
		position_location = new Coordenada();
		position_refDirection = new Coordenada();
		
		XDim = 0;
		YDim = 0;
	}
	
	public Coordenada getPosition_location() {
		return position_location;
	}
	public void setPosition_location(Coordenada position_location) {
		this.position_location = position_location;
	}
	public Coordenada getPosition_refDirection() {
		return position_refDirection;
	}
	public void setPosition_refDirection(Coordenada position_refDirection) {
		this.position_refDirection = position_refDirection;
	}
	public double getXDim() {
		return XDim;
	}
	public void setXDim(double xDim) {
		XDim = xDim;
	}
	public double getYDim() {
		return YDim;
	}
	public void setYDim(double yDim) {
		YDim = yDim;
	}

}
