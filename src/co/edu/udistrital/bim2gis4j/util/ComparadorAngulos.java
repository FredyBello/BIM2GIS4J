package co.edu.udistrital.bim2gis4j.util;

import java.util.Comparator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;

import com.vividsolutions.jts.geom.Coordinate;


//basado en http://stackoverflow.com/questions/19713092/how-to-order-vertices-in-a-non-convex-polygon-how-to-find-one-of-many-solutions
public class ComparadorAngulos implements Comparator<Coordenada>{

	Coordinate centroide;
	
	public ComparadorAngulos(Coordinate pCentroide){
		centroide = pCentroide;
	}
	
	//compara los angulos de dos coordenadas respecto a un centroide
	@Override
	public int compare(Coordenada cA, Coordenada cB) {

		int r = 0;
		
		double anguloDeA = Math.atan2(cA.getY() - centroide.y, cA.getX() - centroide.x);
		double anguloDeB = Math.atan2(cB.getY() - centroide.y, cB.getX() - centroide.x);
		
		if(anguloDeA > anguloDeB){
			r = 1;
		}
		
		if(anguloDeA == anguloDeB){
			
			double distanciaA = Vector3D.distance(cA.toVector3D(), new Vector3D(centroide.x, centroide.y, centroide.z));
			double distanciaB = Vector3D.distance(cB.toVector3D(), new Vector3D(centroide.x, centroide.y, centroide.z));
			
			if(distanciaA < distanciaB) r = 1;
			if(distanciaB < distanciaA) r = -1;
		}
		
		if(anguloDeA < anguloDeB){
			r = -1;
		}

		return r;
	}

}
