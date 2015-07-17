package co.edu.udistrital.bim2gis4j.bim2gisbm;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Coordenada /*implements Comparable<Coordenada>*/ {
	
	double x;
	double y;
	double z;
	
	//si dos coordenadas tienen una distancia menor a la tolerancia se consideran iguales
	public static double tolerancia = 0.001;
	
	public Coordenada(){
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Coordenada(double X, double Y, double Z){
		x = X;
		y = Y;
		z = Z;
	}
	
	public Coordenada(Vector3D vector){
		x = vector.getX();
		y = vector.getY();
		z = vector.getZ();
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	
	@Override
	public String toString(){
		return "( " + getX() + ", " + getY() + ", " + getZ() + " )";
	}
	
	public Vector3D toVector3D(){
		return new Vector3D(x,y,z);
	}
	
	public boolean esIgual(Coordenada c){
		boolean r = false;
		
		if( x==c.x && y==c.y && z == c.z) r = true;
		
		return r;
	}
	
	
	//se usa para evitar coordenadas repetidas dentro del mismo poligono
	@Override
	public boolean equals(Object otraCoordenada){
		
		boolean r = false;
		
		Coordenada otra = (Coordenada) otraCoordenada;
		double distancia = this.toVector3D().distance(otra.toVector3D());
		
		if(distancia <= tolerancia){
			r = true;
		}
		
		
		return r;
		
		
	}
	

	/*
	@Override
	public int compareTo(Coordenada otraCoordenada) {

		int r = 0;
		
		Coordenada origen = new Coordenada (0,0,0);
		
		double distanciaEstaCoordenada = this.toVector3D().distance(origen.toVector3D()); 
		
		double distanciaOtraCoordenada = otraCoordenada.toVector3D().distance(origen.toVector3D());
		
		if(distanciaEstaCoordenada > distanciaOtraCoordenada) r = 1;
		
		if(distanciaEstaCoordenada == distanciaOtraCoordenada) r = 0;
		
		if(distanciaEstaCoordenada < distanciaOtraCoordenada) r = -1;

		
		return r;
	};
	
	*/
	

}
