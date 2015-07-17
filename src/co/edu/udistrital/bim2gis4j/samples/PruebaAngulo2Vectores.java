package co.edu.udistrital.bim2gis4j.samples;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class PruebaAngulo2Vectores {

	
	public static void main(String[] args){
		
		Vector3D normal = new Vector3D(1,1,-0.001);
		
		Vector3D normalPlanoXY = new Vector3D(0,0,-1);
		
		double angulo = Vector3D.angle(normalPlanoXY, normal);
		
		System.out.println(Math.toDegrees(angulo));
		
	}
}
