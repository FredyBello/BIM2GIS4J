package co.edu.udistrital.bim2gis4j.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
/*
import jp.ne.so_net.ga2.no_ji.jcom.IDispatch;
import jp.ne.so_net.ga2.no_ji.jcom.ReleaseManager;
*/
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.DOUBLE;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcArbitraryClosedProfileDef;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcAxis2Placement2D;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanClippingResult;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBuildingStorey;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCartesianPoint;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCompositeCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCompositeCurveSegment;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcDirection;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcLengthMeasure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcLocalPlacement;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPolyline;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPositiveLengthMeasure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcProduct;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcProductDefinitionShape;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcProductRepresentation;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRectangleProfileDef;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRelAggregates;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRelContainedInSpatialStructure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentation;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentationItem;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRoof;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcSlab;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcSlabTypeEnum;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcTrimmedCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcWall;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcWallStandardCase;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.LIST;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.SET;
import openifctools.com.openifcjavatoolbox.ifcmodel.IfcModel;

import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Edificio;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Muro;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Piso;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Plancha;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Rectangulo;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Segmento;

public class LectorMuros {
	
	protected List<IfcRelAggregates> listaTechosAgregados = new ArrayList();
	
	public void cargarDatosBasicos(IfcModel ifcModel, Edificio edificio){

		
		//Se leen los pisos del edificio y se cargan los IDs de muros
		for (IfcRelContainedInSpatialStructure currentRelation : (Collection<IfcRelContainedInSpatialStructure>) ifcModel
				.getCollection(IfcRelContainedInSpatialStructure.class)) {
			// solo interesa averiguar por los PISOS del edificio
			if (currentRelation.getRelatingStructure() instanceof IfcBuildingStorey) {
				IfcBuildingStorey storey = (IfcBuildingStorey) currentRelation
						.getRelatingStructure();
				// no se tienen en cuenta los pisos subterraneos ni el piso base
				// (elevation = 0)
				if (storey.getElevation().value >= 0) {

					Piso pisoActual = edificio.buscarPiso(storey.getGlobalId().toString());
					
					if(pisoActual != null){

						SET<IfcProduct> relatedElements = currentRelation
								.getRelatedElements();
						
						// se buscan las planchas que tenga el piso
						for (Object product : relatedElements) {
							
							Muro muroActual = new Muro();
							
							muroActual.setIfcModel(ifcModel);
							muroActual.setPisoPadre(pisoActual);
							
							if (product instanceof IfcWallStandardCase) {
								
								IfcWallStandardCase currentWall = (IfcWallStandardCase) product;
								
								muroActual.setId(currentWall.getGlobalId().toString());
								muroActual.setTipo("ESTANDAR");
								pisoActual.getMuros().add(muroActual);
									
								
							}else if (product instanceof IfcWall){ //los muros que no son estandar
										
								IfcWall currentWall = (IfcWall) product;
								muroActual.setId(currentWall.getGlobalId().toString());
								muroActual.setTipo("NO ESTANDAR");
								pisoActual.getMuros().add(muroActual);
								
							}
						}						
					}
				}
			}
		}
	}
	
	public void leerMuros(List<Piso> pisos, IfcModel ifcModel){
		
		
		try {
			
            for (Piso pisoActual : pisos) {
            	for (Muro muroActual : pisoActual.getMuros()){
            		
            		muroActual.objectPlacement.setPlacementRelTo_placementRelTo(new Coordenada());
            		muroActual.objectPlacement.setPlacementRelTo_relativePlacement(new Coordenada());
            		
            		muroActual.objectPlacement.setRelativePlacement_location(new Coordenada());
            		//Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.].
            		//planchaActual.objectPlacement.setRelativePlacement_axis(new Coordenada(1,0,0));
            		muroActual.objectPlacement.setRelativePlacement_axis(null);
            		//planchaActual.objectPlacement.setRelativePlacement_refDirection(new Coordenada(0,0,1));
            		muroActual.objectPlacement.setRelativePlacement_refDirection(null);
            		
            		muroActual.representation.setRepresentation_position_location(new Coordenada());
            		//Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.]. 
            		//planchaActual.representation.setRepresentation_position_axis(new Coordenada(1,0,0));
            		muroActual.representation.setRepresentation_position_axis(null);
            		//planchaActual.representation.setRepresentation_position_refDirection(new Coordenada(0,0,1));
            		muroActual.representation.setRepresentation_position_refDirection(null);
            		
            		muroActual.representation.setRepresentation_extruded_direction(new Coordenada());
            		
            		
            		
            		
            		
            		Object objeto = ifcModel.getIfcObjectByID(muroActual.getId());
            		
            		if (objeto instanceof IfcWallStandardCase) {
            			
            			IfcWallStandardCase muroEncontrado = (IfcWallStandardCase) ifcModel.getIfcObjectByID(muroActual.getId());
            			
            			
                    //Se ubica en el nodo objectPlacement->placementRelTo de la plancha 
                    IfcLocalPlacement objectPlacement = (IfcLocalPlacement) muroEncontrado.getObjectPlacement();
            		IfcLocalPlacement placementRelToA = (IfcLocalPlacement) objectPlacement.getPlacementRelTo();
            		

            		
            		//Se lee PlacementRelTo
            		IfcLocalPlacement placementRelToB = (IfcLocalPlacement) placementRelToA.getPlacementRelTo();
            		IfcAxis2Placement3D relativePlacementA = (IfcAxis2Placement3D) placementRelToB.getRelativePlacement();
                    muroActual.objectPlacement.setPlacementRelTo_placementRelTo(LectorCoordenada.Leer(relativePlacementA.getLocation()));

                    
                    
                    //Se lee RelativePlacement
                    
                    IfcAxis2Placement3D relativePlacementB = (IfcAxis2Placement3D) placementRelToA.getRelativePlacement();
                    muroActual.objectPlacement.setPlacementRelTo_relativePlacement(LectorCoordenada.Leer(relativePlacementB.getLocation()));
                    
                    
                    //Se lee location
                    
                    IfcAxis2Placement3D relativePlacementC = (IfcAxis2Placement3D) objectPlacement.getRelativePlacement();
                    LectorAxis2Placement3D.leerAxis2Placement3D(muroActual.objectPlacement, relativePlacementC);
                    
                    
                    //Se lee Representation
                    
                    IfcProductDefinitionShape representation = (IfcProductDefinitionShape) muroEncontrado.getRepresentation();
                    
                    //se asume que siempre va a existir UNA sola representacion (SOLO SE LEE LA POSICION 0)
                    
                    for (IfcRepresentation repActual : representation.getRepresentations()) {
                    	
                    	
                    	//posible valores = Clipping - Curve2D - SweptSolid
                    	if(repActual.getRepresentationType().toString().equals("SweptSolid")){
                    		
                    		//System.err.println("Procesando representation " + repActual.getStepLineNumber() + " en muro " + muroActual.getId());
                    		procesarSweptSolid(repActual, muroActual);
                    		
                    	}else if (repActual.getRepresentationType().toString().equals("Clipping")){
                    		
                    		//System.err.println("En muro " + muroActual.getId() + " procesando representation tipo Clipping con STEP number = " + repActual.getStepLineNumber());
                    		procesarClipping(repActual, muroActual);
                    		
                    	}else if (repActual.getRepresentationType().toString().equals("Curve2D")){
                    		
                    		//System.err.println("MURO " + muroActual.getId() + " DESCARTADA REPRESENTATION PORQUE ES TIPO \"Curve2D\" STEP NUMBER = " + repActual.getStepLineNumber());
                    		muroActual.representation.setRepresentation_representationType("DESCARTADA - " + repActual.getRepresentationType().toString());
                    		
                    	}else{
                    		
                    		System.err.println("DESCARTADA REPRESENTATION PORQUE ES TIPO DESCONOCIDO");
                    		
                    	}
                    	
                    	
                    	
					}
            		}else{
            			//
            			System.err.println("DESCARTADO MURO CON ID = " + muroActual.getId() + " PORQUE NO ES IfcWallStandardCase");
            			
            		}
            	}
			}
		} catch (Exception e) {
            e.printStackTrace();
        } finally {
            //rm.release();
        }
		
	}
	
	public static void procesarSweptSolid(IfcRepresentation representationActual, Muro muroActual){
		

		LectorRepresentation.procesarSweptSolid(representationActual, muroActual);
		        
	}



	public static void procesarClipping(IfcRepresentation representationActual, Muro muroActual){
	
		LectorRepresentation.procesarClipping(representationActual, muroActual);
		
	}

}
