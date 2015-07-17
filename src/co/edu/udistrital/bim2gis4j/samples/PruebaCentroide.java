package co.edu.udistrital.bim2gis4j.samples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Poligono;
import co.edu.udistrital.bim2gis4j.util.ComparadorAngulos;

import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.Coordinate;

public class PruebaCentroide {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
 
		Poligono poli = new Poligono();
		
		poli.getCoordenadas().add(new Coordenada(new Vector3D( 0.0, 0.0, 3.2 )));
		poli.getCoordenadas().add(new Coordenada(new Vector3D( 11.7, 0.3, 3.3732050815137757 )));
		poli.getCoordenadas().add(new Coordenada(new Vector3D( 12.0, 0.0, 3.2 )));
		poli.getCoordenadas().add(new Coordenada(new Vector3D( 0.3000000000000007, 0.3, 3.3732050815137757 )));

		poli.ordenarVerticesRespectoACentroide();
		
		//System.out.println(poli.getCentroide());
		
		//ordena las coordenadas respecto al centroide para que el poligono se genere correctamente, evitando cruces de bordes por ejemplo
		//Collections.sort(poli.getCoordenadas(), new ComparadorAngulos(poli.getCentroide()));

		System.out.println(poli.toString());
	}
	
	
}
