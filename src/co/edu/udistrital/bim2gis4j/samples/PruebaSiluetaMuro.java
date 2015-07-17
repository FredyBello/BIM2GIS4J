package co.edu.udistrital.bim2gis4j.samples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Poligono;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class PruebaSiluetaMuro {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
 
		/*
		//EJEMPLO QUE DEVUELVE UN SOLO POLIGONO
		Poligono caraMuro = new Poligono();
		
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 0.3, 5.75, 0.0 )));
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 7.41, 5.75, 0.0 )));
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 7.41, 5.75, 2.5 )));
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 0.3, 5.75, 2.5 )));
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 0.3, 5.75, 0.0 )));
		
		Poligono caraPuerta = new Poligono();
		
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 6.1025, 5.75, 0.0 )));
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 6.1025, 5.75, 2.01 )));
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 5.2175, 5.75, 2.01 )));
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 5.2175, 5.75, 0.0 )));
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 6.1025, 5.75, 0.0 )));
		
		*/


		/*
		//EJEMPLO QUE DEVUELVE DOS POLIGONOS
		
		Poligono caraMuro = new Poligono();
		
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 0.3, 5.75, 0.0 )));
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 7.41, 5.75, 0.0 )));
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 7.41, 5.99, 0.0 )));
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 0.3, 5.99, 0.0 )));
		caraMuro.getCoordenadas().add(new Coordenada(new Vector3D( 0.3, 5.75, 0.0 )));
		
		Poligono caraPuerta = new Poligono();
		
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 5.2175, 5.75, 0.0 )));
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 6.1025, 5.75, 0.0 )));
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 6.1025, 5.99, 0.0 )));
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 5.2175, 5.99, 0.0 )));
		caraPuerta.getCoordenadas().add(new Coordenada(new Vector3D( 5.2175, 5.75, 0.0 )));
		*/
		
		Poligono original = new Poligono();
		
		original.getCoordenadas().add(new Coordenada(new Vector3D( 7.41, 5.99, 0.0 )));
		original.getCoordenadas().add(new Coordenada(new Vector3D( 0.3, 5.99, 0.0 )));
		original.getCoordenadas().add(new Coordenada(new Vector3D( 0.3, 5.99, 2.5 )));
		original.getCoordenadas().add(new Coordenada(new Vector3D( 7.41, 5.99, 2.5 )));
		original.getCoordenadas().add(new Coordenada(new Vector3D( 7.41, 5.99, 0.0 )));
		
		Poligono tijera01 = new Poligono();
		
		tijera01.getCoordenadas().add(new Coordenada(new Vector3D( 2.4924999999999997, 5.99, 0.0 )));
		tijera01.getCoordenadas().add(new Coordenada(new Vector3D( 2.4924999999999997, 5.99, 2.01 )));
		tijera01.getCoordenadas().add(new Coordenada(new Vector3D( 1.6075, 5.99, 2.01 )));
		tijera01.getCoordenadas().add(new Coordenada(new Vector3D( 1.6075, 5.99, 0.0 )));
		tijera01.getCoordenadas().add(new Coordenada(new Vector3D( 2.4924999999999997, 5.99, 0.0 )));
		
		
		                              //Resultado =  Polygon[(2.4924999999999997, 5.99, 0.0) ,(7.41, 5.99, 0.0) ,(7.41, 5.99, 2.5) ,(0.3, 5.99, 2.5) ,(0.3, 5.99, 0.0) ,(1.6075, 5.99, 0.0) ,(2.4924999999999997, 5.99, 0.0)]
		                              //Tijera  Polygon[(6.1025, 5.99, 0.0) ,(6.1025, 5.99, 2.01) ,(5.2175, 5.99, 2.01) ,(5.2175, 5.99, 0.0) ,(6.1025, 5.99, 0.0)]
		                              //Resultado =  Polygon[(5.2175, 5.99, 0.0) ,(2.4924999999999997, 5.99, 0.0) ,(1.6075, 5.99, 0.0) ,(0.3, 5.99, 0.0) ,(0.3, 5.990000000000002, 2.5000000000000004) ,(7.41, 5.990000000000002, 2.5000000000000004) ,(7.41, 5.99, 0.0) ,(6.102499999999998, 5.99, 0.0) ,(6.102499999999998, 5.99, 2.0100000000000002) ,(5.2175, 5.99, 2.0100000000000002) ,(5.2175, 5.99, 0.0)]

		
		System.out.println("original = " + original);
		System.out.println("tijera01 = " + tijera01);
		                                                   
		List<Poligono> resultado = original.diferencia(tijera01);
		
		for (Poligono actual : resultado) {
			
			System.out.println(actual);
			
		}		
		
	}	
	
}
