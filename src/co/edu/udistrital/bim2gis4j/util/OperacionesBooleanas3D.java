package co.edu.udistrital.bim2gis4j.util;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanClippingResult;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanOperand;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcHalfSpaceSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPolygonalBoundedHalfSpace;

public class OperacionesBooleanas3D {
	
	public void resolverClipping(IfcBooleanClippingResult clipping){
		
		
		
		if(clipping.getOperator().value.toString().equals("DIFFERENCE")){
			
			clippingDifference(clipping);
			
		}if(clipping.getOperator().value.toString().equals("UNION")){
			
			System.err.println("UNION NO IMPLEMENTADO");
			
		}if(clipping.getOperator().value.toString().equals("INTERSECTION")){
			
			System.err.println("INTERSECTION NO IMPLEMENTADO");
			
		}
		
		
		
	}
	
	public void clippingDifference(IfcBooleanClippingResult clipping){
		
		IfcBooleanOperand opA = clipping.getFirstOperand();
		IfcBooleanOperand opB = clipping.getSecondOperand();
		
		String claseDeA = "DESCONOCIDA";
		String claseDeB = "DESCONOCIDA";
		
		
		
		if(opA instanceof IfcExtrudedAreaSolid){
			claseDeA = "IfcExtrudedAreaSolid";
		}
		
		if(opA instanceof IfcBooleanClippingResult){
			claseDeA = "IfcBooleanClippingResult";
		}
		
		
		
		if(opB instanceof IfcHalfSpaceSolid){
			claseDeB = "IfcHalfSpaceSolid";
			

		}
		
		
		System.err.println("A = " + claseDeA + " B = " + claseDeB );
		
	}

}
