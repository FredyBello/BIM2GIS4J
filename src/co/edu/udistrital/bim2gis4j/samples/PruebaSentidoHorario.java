package co.edu.udistrital.bim2gis4j.samples;

import java.util.Collections;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Poligono;

public class PruebaSentidoHorario {

//  http://stackoverflow.com/questions/1988100/how-to-determine-ordering-of-3d-vertices
//	
//	As others have noted, your question isn't entirely clear. Is the for something like a 3D backface culling test? 
//	If so, you need a point to determine the winding direction relative to. Viewed from one side of the polygon the 
//	vertices will appear to wind clockwise. From the other side they'll appear to wind counter clockwise.
//
//	But suppose your polygon is convex and properly planar. Take any three consecutive vertices A, B, and C. 
//	Then you can find the surface normal vector using the cross product:
//
//	N = (B - A) x (C - A)
//	Taking the dot product of the normal with a vector from the given view point, V, to one of the vertices 
//	will give you a value whose sign indicates which way the vertices appear to wind when viewed from V:
//
//	w = N . (A - V)
//	
//	Whether this is positive for clockwise and negative for anticlockwise or the opposite will depend on 
//	the handedness of your coordinate system.		

	public static void main(String[] args) {


		Poligono p = new Poligono();
		
		p.getCoordenadas().add(new Coordenada(-0.5, 5.00000000155568, 6.317691469461097));
		p.getCoordenadas().add(new Coordenada(12.5, 5.00000000155568, 6.317691469461097));
		p.getCoordenadas().add(new Coordenada(12.5, -0.5000000001555679, 3.1422649740538904));
		p.getCoordenadas().add(new Coordenada(-0.5, -0.5000000001555679, 3.1422649740538904));
		p.getCoordenadas().add(new Coordenada(-0.5, 5.00000000155568, 6.317691469461097));

		Vector3D A = p.getCoordenadas().get(0).toVector3D();
		Vector3D B = p.getCoordenadas().get(1).toVector3D(); 
		Vector3D C = p.getCoordenadas().get(2).toVector3D();
		Vector3D B_A = B.subtract(A);
		Vector3D C_A = C.subtract(A);
		
		Vector3D V = new Vector3D(0,0,0);
		Vector3D A_V = A.subtract(V);
		
		Vector3D N = B_A.crossProduct(C_A);
		
		//si sentido es negativo el poligono es antihorario, si no es horario
		double sentido = N.dotProduct(A_V);

		System.out.println("Originales = " + p);
		System.out.println(sentido);
		
		if(sentido<0){
			Collections.reverse(p.getCoordenadas());
			System.out.println("Revertidas = " + p);
		}
		

	}

}
