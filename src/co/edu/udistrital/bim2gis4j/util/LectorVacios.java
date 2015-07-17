package co.edu.udistrital.bim2gis4j.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBuildingStorey;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcDoor;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcLocalPlacement;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcOpeningElement;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcProduct;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcProductDefinitionShape;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRelContainedInSpatialStructure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRelFillsElement;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRelVoidsElement;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentation;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcWall;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcWallStandardCase;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcWindow;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.SET;
import openifctools.com.openifcjavatoolbox.ifcmodel.IfcModel;

import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Edificio;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Muro;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Piso;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Vacio;

public class LectorVacios {

	public void cargarDatosBasicos(IfcModel ifcModel, Edificio edificio){

		
		HashMap<String, List<Vacio>> murosYVacios = new HashMap<String, List<Vacio>>();
		
		//Se carga la relacion que indica los vacios y a cual muro pertenecen
		for (IfcRelVoidsElement currentRelation : (Collection<IfcRelVoidsElement>) ifcModel
				.getCollection(IfcRelVoidsElement.class)) {
			
			
			// se va a indagar por los vacios que pertenezcan exclusivamente a muros
			if (currentRelation.getRelatingBuildingElement() instanceof IfcWallStandardCase) {
				
				IfcWallStandardCase muro = (IfcWallStandardCase) currentRelation.getRelatingBuildingElement();
				String idMuro = muro.getGlobalId().toString();
				
				IfcOpeningElement vacioIfc = (IfcOpeningElement) currentRelation.getRelatedOpeningElement();

				
				
				
				//si el muro aun no esta registrado
				if(!murosYVacios.containsKey(idMuro)){
					
					List<Vacio> listaDeVacios = new ArrayList();
					
					Vacio vacioActual = new Vacio();
					vacioActual.setId(vacioIfc.getGlobalId().toString());
					vacioActual.setIfcModel(ifcModel);
					listaDeVacios.add(vacioActual);
					
					murosYVacios.put(idMuro, listaDeVacios );
					
				}else{
					
					List<Vacio> listaDeVacios = murosYVacios.get(idMuro);
					
					Vacio vacioActual = new Vacio();
					vacioActual.setId(vacioIfc.getGlobalId().toString());
					vacioActual.setIfcModel(ifcModel);
					listaDeVacios.add(vacioActual);
					
				}
			}
		}
		
		
		//se asignan a cada muro sus vacios respectivos
		for (Piso pisoActual : edificio.getPisos()) {
			
			for (Muro muroActual : pisoActual.getMuros()) {
				
				muroActual.setVacios(murosYVacios.get(muroActual.getId()));
				
			} 
			
		}
		
		//a cada vacio se le asigna el tipo de elemento que lo "rellena" que puede ser una puerta o una ventana
		
		HashMap<String, String> vacioYTipo = new HashMap<String, String>();
		
		//Se carga la relacion que indica los vacios y a cual muro pertenecen
		for (IfcRelFillsElement currentRelation : (Collection<IfcRelFillsElement>) ifcModel
				.getCollection(IfcRelFillsElement.class)){
			
			String idDelVacio = currentRelation.getRelatingOpeningElement().getGlobalId().toString();
			String puertaOVentana = "desconocido";

			if (currentRelation.getRelatedBuildingElement() instanceof IfcDoor){
				puertaOVentana = "puerta";
			}else if (currentRelation.getRelatedBuildingElement() instanceof IfcWindow){
				puertaOVentana = "ventana";
			}
			
			vacioYTipo.put(idDelVacio, puertaOVentana);
			
		}
		
		//se asignan a cada vacio su tipo de relleno, puerta o ventana
		for (Piso pisoActual : edificio.getPisos()) {
			
			for (Muro muroActual : pisoActual.getMuros()) {
				
				if(muroActual.getVacios() != null){
					
					for (Vacio vacioActual : muroActual.getVacios()){
						
						String puertaOVentana = vacioYTipo.get(vacioActual.getId());
						vacioActual.setTipo(puertaOVentana);
						
					}
					
				}
				
			} 
			
		}
			
	}
	
	
	public void leerVacios(IfcModel ifcModel, Edificio edificio){
		
		
		try {
			
			//se asignan a cada vacio su tipo de relleno, puerta o ventana
			for (Piso pisoActual : edificio.getPisos()) {
				
				for (Muro muritoActual : pisoActual.getMuros()) {
					
					if(muritoActual.getVacios() != null){
						
						for (Vacio vacioActual : muritoActual.getVacios()){
							
							//lo unico que la diferencia de LectorMuros es esta linea
							vacioActual.setMuroAlQueVacia(muritoActual);
							
							vacioActual.objectPlacement.setPlacementRelTo_placementRelTo(new Coordenada());
							vacioActual.objectPlacement.setPlacementRelTo_relativePlacement(new Coordenada());
		            		
							vacioActual.objectPlacement.setRelativePlacement_location(new Coordenada());
		            		//Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.].
		            		//planchaActual.objectPlacement.setRelativePlacement_axis(new Coordenada(1,0,0));
							vacioActual.objectPlacement.setRelativePlacement_axis(null);
		            		//planchaActual.objectPlacement.setRelativePlacement_refDirection(new Coordenada(0,0,1));
							vacioActual.objectPlacement.setRelativePlacement_refDirection(null);
		            		
							vacioActual.representation.setRepresentation_position_location(new Coordenada());
		            		//Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.]. 
		            		//planchaActual.representation.setRepresentation_position_axis(new Coordenada(1,0,0));
							vacioActual.representation.setRepresentation_position_axis(null);
		            		//planchaActual.representation.setRepresentation_position_refDirection(new Coordenada(0,0,1));
							vacioActual.representation.setRepresentation_position_refDirection(null);
		            		
							vacioActual.representation.setRepresentation_extruded_direction(new Coordenada());
		            		
		            		
		            		
		            		
		            		
		            		Object objeto = ifcModel.getIfcObjectByID(vacioActual.getId());
		            		
		            		if (objeto instanceof IfcOpeningElement) {
		            			
		            			IfcOpeningElement vacioEncontrado = (IfcOpeningElement) ifcModel.getIfcObjectByID(vacioActual.getId());
		            			
		            			
		                    //Se ubica en el nodo objectPlacement->placementRelTo de la plancha 
		                    IfcLocalPlacement objectPlacement = (IfcLocalPlacement) vacioEncontrado.getObjectPlacement();
		            		IfcLocalPlacement placementRelToA = (IfcLocalPlacement) objectPlacement.getPlacementRelTo();
		            		

		            		
		            		//Se lee PlacementRelTo
		            		IfcLocalPlacement placementRelToB = (IfcLocalPlacement) placementRelToA.getPlacementRelTo();
		            		IfcAxis2Placement3D relativePlacementA = (IfcAxis2Placement3D) placementRelToB.getRelativePlacement();
		                    vacioActual.objectPlacement.setPlacementRelTo_placementRelTo(LectorCoordenada.Leer(relativePlacementA.getLocation()));

		                    
		                    
		                    //Se lee RelativePlacement
		                    
		                    IfcAxis2Placement3D relativePlacementB = (IfcAxis2Placement3D) placementRelToA.getRelativePlacement();
		                    vacioActual.objectPlacement.setPlacementRelTo_relativePlacement(LectorCoordenada.Leer(relativePlacementB.getLocation()));
		                    
		                    
		                    //Se lee location
		                    
		                    IfcAxis2Placement3D relativePlacementC = (IfcAxis2Placement3D) objectPlacement.getRelativePlacement();
		                    LectorAxis2Placement3D.leerAxis2Placement3D(vacioActual.objectPlacement, relativePlacementC);
		                    
		                    
		                    //Se lee Representation
		                    
		                    IfcProductDefinitionShape representation = (IfcProductDefinitionShape) vacioEncontrado.getRepresentation();
		                    
		                    //se asume que siempre va a existir UNA sola representacion (SOLO SE LEE LA POSICION 0)
		                    
		                    for (IfcRepresentation repActual : representation.getRepresentations()) {
		                    	
		                    	
		                    	//posible valores = Clipping - Curve2D - SweptSolid
		                    	if(repActual.getRepresentationType().toString().equals("SweptSolid")){
		                    		
		                    		//System.err.println("Procesando representation " + repActual.getStepLineNumber() + " en muro " + muroActual.getId());
		                    		procesarSweptSolid(repActual, vacioActual);
		                    		
		                    	}else if (repActual.getRepresentationType().toString().equals("Clipping")){
		                    		
		                    		//System.err.println("En muro " + muroActual.getId() + " procesando representation tipo Clipping con STEP number = " + repActual.getStepLineNumber());
		                    		procesarClipping(repActual, vacioActual);
		                    		
		                    	}else if (repActual.getRepresentationType().toString().equals("Curve2D")){
		                    		
		                    		//System.err.println("MURO " + muroActual.getId() + " DESCARTADA REPRESENTATION PORQUE ES TIPO \"Curve2D\" STEP NUMBER = " + repActual.getStepLineNumber());
		                    		vacioActual.representation.setRepresentation_representationType("DESCARTADA - " + repActual.getRepresentationType().toString());
		                    		
		                    	}else{
		                    		
		                    		System.err.println("DESCARTADA REPRESENTATION PORQUE ES TIPO DESCONOCIDO");
		                    		
		                    	}
		                    	
		                    	
		                    	
							}
		            		}else{
		            			//
		            			System.err.println("DESCARTADO VACIO CON ID = " + vacioActual.getId() + " PORQUE NO ES IfcOpeningElement");
		            			
		            		}
							
						}
						
					}
					
				} 
				
			}
		} catch (Exception e) {
            e.printStackTrace();
        } finally {
            //rm.release();
        }
		
	}
	
	public static void procesarSweptSolid(IfcRepresentation representationActual, Vacio vacioActual){
		

		LectorRepresentation.procesarSweptSolid(representationActual, vacioActual);
		        
	}



	public static void procesarClipping(IfcRepresentation representationActual, Vacio vacioActual){
	
		LectorRepresentation.procesarClipping(representationActual, vacioActual);
		
	}

	
	
}
