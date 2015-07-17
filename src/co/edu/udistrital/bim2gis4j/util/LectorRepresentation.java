package co.edu.udistrital.bim2gis4j.util;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.DOUBLE;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcArbitraryClosedProfileDef;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcAxis2Placement2D;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanClippingResult;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanOperand;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCartesianPoint;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCompositeCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCompositeCurveSegment;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcDirection;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcHalfSpaceSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcLengthMeasure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPlane;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPolyline;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPositiveLengthMeasure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRectangleProfileDef;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentation;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentationItem;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcTrimmedCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.LIST;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.SET;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Muro;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Rectangulo;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Segmento;
import co.edu.udistrital.bim2gis4j.ifc.PlanoDeCorte;
import co.edu.udistrital.bim2gis4j.ifc.Solido;

public class LectorRepresentation {

	public static void procesarSweptSolid(IfcRepresentation representationActual, Solido pSolido){
		String representationType = representationActual.getRepresentationType().toString();
		pSolido.representation.setRepresentation_representationType(representationType);
        //System.err.println("seteado a = " + representationActual.getStepLineNumber());
		pSolido.representation.setStepLineNumber(representationActual.getStepLineNumber());
        
        SET<IfcRepresentationItem> items = representationActual.getItems();
        Iterator it = items.iterator();
        //se asume que siempre va a existir UNA sola representacion (SOLO SE LEE EL PRIMER ITEM)
        IfcExtrudedAreaSolid itemActual = (IfcExtrudedAreaSolid) it.next();
        
        Solido.extraerCoordenadasDeExtrudedAreaSolid(itemActual, pSolido);
        
	}
	
	public static void procesarClipping(IfcRepresentation representationActual, Solido pSolido){
		
		SET<IfcRepresentationItem> items = representationActual.getItems();
        Iterator it = items.iterator();
        
        //se asume que siempre va a existir UNA sola representacion (SOLO SE LEE EL PRIMER ITEM)
        IfcBooleanClippingResult clipping = (IfcBooleanClippingResult) it.next();
        
        IfcBooleanOperand operadorA = clipping.getFirstOperand();
        IfcBooleanOperand operadorB = clipping.getSecondOperand();
        
        
    	if(operadorA instanceof IfcExtrudedAreaSolid && operadorB instanceof IfcHalfSpaceSolid){
    		
    		//operadorA es un Solido y operadorB un Plano

    		extraerCoordenadasYAgregarPlano(representationActual, pSolido, operadorA, operadorB);
        
    		
		}else if(operadorA instanceof IfcBooleanClippingResult && operadorB instanceof IfcHalfSpaceSolid){ 
    	    
			//operadorA es un Clipping 3D y operadorB un Plano
    	
			
	        IfcBooleanOperand opC = ((IfcBooleanClippingResult)operadorA).getFirstOperand();
	        IfcBooleanOperand opD = ((IfcBooleanClippingResult)operadorA).getSecondOperand();
			
	        //opC es un Solido
	        if(opC instanceof IfcExtrudedAreaSolid){

	        	extraerCoordenadasYAgregarPlano(representationActual, pSolido, opC, opD);

	        	//FALTA IMPLEMENTAR EL CORTE CUANDO HAY MAS DE UN PLANO
	        	Solido.agregarPlanoDeCorte(pSolido, (IfcHalfSpaceSolid) operadorB);
	        		

	        	
	        }else{
				
				System.err.println("PENDIENTE DE IMPLEMENTAR !" + pSolido.getId() + "( opC = " + opC.getClass() + " opD = " + opD.getClass() + " )");
				
			}
	        	
	        
	        
			//System.err.println("REVISAR MURO " + muroActual.getId() );
			
		}else{
			
			System.err.println("ESTO NUNCA DEBERIA SALIR" );
		}
		
	}
	
	public static void extraerCoordenadasYAgregarPlano(IfcRepresentation representationActual, Solido pSolido, IfcBooleanOperand opA, IfcBooleanOperand opB){
		
			//se asume que siempre va a existir UNA sola representacion (SOLO SE LEE EL PRIMER ITEM)
	    	IfcExtrudedAreaSolid itemActual = (IfcExtrudedAreaSolid)opA;
	    	
	    	String representationType = representationActual.getRepresentationType().toString();
	    	pSolido.representation.setRepresentation_representationType(representationType);
	        //System.err.println("seteado a = " + representationActual.getStepLineNumber());
	    	pSolido.representation.setStepLineNumber(representationActual.getStepLineNumber());
	        
	        Solido.extraerCoordenadasDeExtrudedAreaSolid(itemActual, pSolido);

	        //despues de calcular las caras se agrega el plano de corte
	        Solido.agregarPlanoDeCorte(pSolido, (IfcHalfSpaceSolid)opB);
			
	}
	
}
