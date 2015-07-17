package co.edu.udistrital.bim2gis4j.util;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.DOUBLE;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCartesianPoint;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcDirection;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcLengthMeasure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.LIST;

import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;

public class LectorCoordenada {

	
	public static Coordenada Leer(IfcCartesianPoint puntoCartesiano){
		
        LIST<IfcLengthMeasure> coordinates = puntoCartesiano.getCoordinates();
        
        int coordenadas = coordinates.size();
        
        Coordenada r = new Coordenada();
        for(int n = 0; n<coordenadas;n++){
        	
            double valor = (Double) coordinates.get(n).value;
            
            switch (n) {
			case 0: r.setX(valor); break;
			case 1: r.setY(valor); break;
			case 2: r.setZ(valor); break;
			}

        }
        
        return r;
		
		
	}
	
	public static Coordenada Leer(IfcDirection puntoCartesiano){
		
        LIST<DOUBLE> coordinates = puntoCartesiano.getDirectionRatios();
        
        int coordenadas = coordinates.size();
        
        Coordenada r = new Coordenada();
        for(int n = 0; n<coordenadas;n++){
        	
            double valor = (Double) coordinates.get(n).value;
            
            switch (n) {
			case 0: r.setX(valor); break;
			case 1: r.setY(valor); break;
			case 2: r.setZ(valor); break;
			}

        }
        
        return r;
		
		
	}
}
