package co.edu.udistrital.bim2gis4j.util;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.DOUBLE;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCartesianPoint;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcDirection;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcLengthMeasure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.LIST;

import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.ifc.Placement;


public class LectorAxis2Placement3D {

	
	public static void leerAxis2Placement3D (Placement objectPlacementRecibido, IfcAxis2Placement3D axis2Placement3D){
		
        //Se lee location
        
        IfcCartesianPoint locationC = axis2Placement3D.getLocation();
        LIST<IfcLengthMeasure> coordinatesC = locationC.getCoordinates();
        
        int coordenadasC = coordinatesC.size();
        
        Coordenada coordC = new Coordenada();
        for(int n = 0; n<coordenadasC;n++){
        	double valor = (Double) coordinatesC.get(n).value;
            
            switch (n) {
			case 0: coordC.setX(valor); break;
			case 1: coordC.setY(valor); break;
			case 2: coordC.setZ(valor); break;
			}
            
        }
        objectPlacementRecibido.setRelativePlacement_location(coordC);
        
        
       
        
      //Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.]. 
        if(axis2Placement3D.getAxis() != null){
        	
        	IfcDirection axis = axis2Placement3D.getAxis();
            LIST<DOUBLE> directionRatios = axis.getDirectionRatios();
            
            int coordenadas = directionRatios.size();
            
            Coordenada coord = new Coordenada();
            
            for(int n = 0; n<coordenadas;n++){
                double valor = (Double) directionRatios.get(n).value;
                
                switch (n) {
				case 0: coord.setX(valor); break;
				case 1: coord.setY(valor); break;
				case 2: coord.setZ(valor); break;
				}
                
            }
            
            objectPlacementRecibido.setRelativePlacement_axis(coord);
            
            IfcDirection refDirection = axis2Placement3D.getRefDirection();
            directionRatios = refDirection.getDirectionRatios();
            
            coordenadas = directionRatios.size();
            
            coord = new Coordenada();
            
            for(int n = 0; n<coordenadas;n++){
                double valor = (Double) directionRatios.get(n).value;
                
                switch (n) {
				case 0: coord.setX(valor); break;
				case 1: coord.setY(valor); break;
				case 2: coord.setZ(valor); break;
				}
                
            }
            
            objectPlacementRecibido.setRelativePlacement_refDirection(coord);
            
            
        }		

    }
}
