package co.edu.udistrital.bim2gis4j.samples;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class PruebaCommonsMath {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Vector3D axisX = new Vector3D(1, 0, 0);
		//Vector3D axisY = new Vector3D(0, 1, 0);
		Vector3D axisZ = new Vector3D(0, 0, 1);
		
		Vector3D deseadoX = new Vector3D(0, 0.434187, 0.900823); //(AXIS, eje Z)
		//Vector3D deseadoX = new Vector3D(0, 0, 1); //(AXIS, eje Z)
		Vector3D deseadoZ = new Vector3D(1, 0, 0); //(refdirection, eje X)
		//Vector3D deseadoY = Vector3D.crossProduct(deseadoX, deseadoZ); // producto cruz
		//System.out.println(Vector3D.crossProduct(deseadoX, deseadoZ));
		
		Rotation rotacionX = new Rotation(axisX,deseadoZ);
		//Rotation rotacionY = new Rotation(axisY,deseadoY);
		Rotation rotacionZ = new Rotation(axisZ,deseadoX);

		Vector3D punto = new Vector3D(1, 1, 1);
		
		Vector3D puntoRotado = rotacionX.applyTo(punto);
		//puntoRotado = rotacionY.applyTo(puntoRotado);
		puntoRotado = rotacionZ.applyTo(puntoRotado);
		
		System.err.println(puntoRotado);
		
		//System.err.println(puntoRotado.getNorm());
		
		
		
	}

}

