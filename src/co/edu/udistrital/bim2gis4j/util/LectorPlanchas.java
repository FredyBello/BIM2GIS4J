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
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.LIST;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.SET;
import openifctools.com.openifcjavatoolbox.ifcmodel.IfcModel;

import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Edificio;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Piso;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Plancha;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Rectangulo;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Segmento;

public class LectorPlanchas {
	
	protected List<IfcRelAggregates> listaTechosAgregados = new ArrayList();
	
	public void cargarDatosBasicos(IfcModel ifcModel, Edificio edificio){
		
		edificio.setPisos(new ArrayList());

		//Se carga el listado de IfcRelAggregates que contiene las definiciones de techos (un techo puede ser definido por un IfcSlab unicamente o por un IfcRelAggregates que contiene un IfcRoof y un IfcSlab agregados) 
		for (IfcRelAggregates techoAgregado : (Collection<IfcRelAggregates>) ifcModel.getCollection(IfcRelAggregates.class)) {
			Object objetoRelacionado = techoAgregado.getRelatingObject();
			if(objetoRelacionado instanceof IfcRoof){
				listaTechosAgregados.add(techoAgregado);
			}
		}
		
		//Se leen los pisos del edificio y se cargan los IDs de planchas
		for (IfcRelContainedInSpatialStructure currentRelation : (Collection<IfcRelContainedInSpatialStructure>) ifcModel
				.getCollection(IfcRelContainedInSpatialStructure.class)) {
			// solo interesa averiguar por los PISOS del edificio
			if (currentRelation.getRelatingStructure() instanceof IfcBuildingStorey) {
				IfcBuildingStorey storey = (IfcBuildingStorey) currentRelation
						.getRelatingStructure();
				// no se tienen en cuenta los pisos subterraneos ni el piso base
				// (elevation = 0)
				if (storey.getElevation().value >= 0) {

					Piso pisoActual = new Piso();
					pisoActual.setId(storey.getGlobalId().toString());
					pisoActual.setElevacion(storey.getElevation().value);
					pisoActual.setNombre(storey.getName().toString());

					edificio.getPisos().add(pisoActual);

					SET<IfcProduct> relatedElements = currentRelation
							.getRelatedElements();
					
					// se buscan las planchas que tenga el piso
					for (Object product : relatedElements) {
						
						Plancha planchaActual = new Plancha();
						
						planchaActual.setIfcModel(ifcModel);
						planchaActual.setPisoPadre(pisoActual);
						
						if (product instanceof IfcSlab) {

							IfcSlab currentSlab = (IfcSlab) product;
							
							if (
									currentSlab.getPredefinedType().value == IfcSlabTypeEnum.IfcSlabTypeEnum_internal.FLOOR
									||
									currentSlab.getPredefinedType().value == IfcSlabTypeEnum.IfcSlabTypeEnum_internal.BASESLAB
									||
									currentSlab.getPredefinedType().value == IfcSlabTypeEnum.IfcSlabTypeEnum_internal.ROOF
								) {
								
								planchaActual.setId(currentSlab.getGlobalId().toString());
								planchaActual.setTipo(currentSlab.getPredefinedType().value.name());
								pisoActual.getPlanchas().add(planchaActual);
								
							}
							
						}else if (product instanceof IfcRoof){ //los techos que no se hayan capturado en el if anterior se capturan aca, las dos opciones son probables
							
							IfcRoof techoVacioPiso = (IfcRoof) product;
							
							for (IfcRelAggregates techoAgregado : (Collection<IfcRelAggregates>) listaTechosAgregados) {
								IfcRoof techoVacioAgregado = (IfcRoof) techoAgregado.getRelatingObject();
								
								if(techoVacioPiso.getGlobalId().toString().equals(techoVacioAgregado.getGlobalId().toString())){
									//System.err.println("TECHO " + techoVacioPiso.getGlobalId() + " EN PISO " + pisoActual.getNombre());
									
									IfcSlab currentSlab = (IfcSlab) techoAgregado.getRelatedObjects().iterator().next();
									
									planchaActual.setId(currentSlab.getGlobalId().toString());
									planchaActual.setTipo(currentSlab.getPredefinedType().value.name());
									pisoActual.getPlanchas().add(planchaActual);
									
									
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void leerPlanchas(IfcModel ifcModel, List<Piso> pisos){
		
		try {
			
            for (Piso pisoActual : pisos) {
            	for (Plancha planchaActual : pisoActual.getPlanchas()){
            		
            		planchaActual.objectPlacement.setPlacementRelTo_placementRelTo(new Coordenada());
            		planchaActual.objectPlacement.setPlacementRelTo_relativePlacement(new Coordenada());
            		
            		planchaActual.objectPlacement.setRelativePlacement_location(new Coordenada());
            		//Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.].
            		//planchaActual.objectPlacement.setRelativePlacement_axis(new Coordenada(1,0,0));
            		planchaActual.objectPlacement.setRelativePlacement_axis(null);
            		//planchaActual.objectPlacement.setRelativePlacement_refDirection(new Coordenada(0,0,1));
            		planchaActual.objectPlacement.setRelativePlacement_refDirection(null);
            		
            		planchaActual.representation.setRepresentation_position_location(new Coordenada());
            		//Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.]. 
            		//planchaActual.representation.setRepresentation_position_axis(new Coordenada(1,0,0));
            		planchaActual.representation.setRepresentation_position_axis(null);
            		//planchaActual.representation.setRepresentation_position_refDirection(new Coordenada(0,0,1));
            		planchaActual.representation.setRepresentation_position_refDirection(null);
            		
            		planchaActual.representation.setRepresentation_extruded_direction(new Coordenada());
            		
            		IfcSlab planchaEncontrada = (IfcSlab) ifcModel.getIfcObjectByID(planchaActual.getId());
            		
            		
                   
                    //Se ubica en el nodo objectPlacement->placementRelTo de la plancha 
                    IfcLocalPlacement objectPlacement = (IfcLocalPlacement) planchaEncontrada.getObjectPlacement();
            		IfcLocalPlacement placementRelToA = (IfcLocalPlacement) objectPlacement.getPlacementRelTo();
            		

            		
            		//Se lee PlacementRelTo
            		IfcLocalPlacement placementRelToB = (IfcLocalPlacement) placementRelToA.getPlacementRelTo();
            		IfcAxis2Placement3D relativePlacementA = (IfcAxis2Placement3D) placementRelToB.getRelativePlacement();
            		IfcCartesianPoint locationA = relativePlacementA.getLocation();
            		LIST<IfcLengthMeasure> coordinatesA = locationA.getCoordinates();
                    
                    int coordenadasA = coordinatesA.size();
                    
                    Coordenada coordA = new Coordenada();
                    for(int n = 0; n<coordenadasA; n++){

                        double valor = (Double) coordinatesA.get(n).value;
                        
                        switch (n) {
						case 0: coordA.setX(valor); break;
						case 1: coordA.setY(valor); break;
						case 2: coordA.setZ(valor); break;
						}
                        
                    }
                    planchaActual.objectPlacement.setPlacementRelTo_placementRelTo(coordA);

                    
                    
                    //Se lee RelativePlacement
                    
                    IfcAxis2Placement3D relativePlacementB = (IfcAxis2Placement3D) placementRelToA.getRelativePlacement();
                    IfcCartesianPoint locationB = relativePlacementB.getLocation();
                    LIST<IfcLengthMeasure> coordinatesB = locationB.getCoordinates();
                    
                    int coordenadasB = coordinatesB.size();
                    
                    Coordenada coordB = new Coordenada();
                    for(int n = 0; n<coordenadasB;n++){
                    	
                        double valor = (Double) coordinatesB.get(n).value;
                        
                        switch (n) {
						case 0: coordB.setX(valor); break;
						case 1: coordB.setY(valor); break;
						case 2: coordB.setZ(valor); break;
						}

                    }
                    planchaActual.objectPlacement.setPlacementRelTo_relativePlacement(coordB);
                    
                    //Se lee location
                    
                    IfcAxis2Placement3D relativePlacementC = (IfcAxis2Placement3D) objectPlacement.getRelativePlacement();
                    IfcCartesianPoint locationC = relativePlacementC.getLocation();
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
                    planchaActual.objectPlacement.setRelativePlacement_location(coordC);
                    
                    /********************************************************************************/
                    //Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.]. 
                    if(relativePlacementC.getAxis() != null){
                    	
                    	IfcDirection axis = relativePlacementC.getAxis();
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
                        
                        planchaActual.objectPlacement.setRelativePlacement_axis(coord);
                        
                        IfcDirection refDirection = relativePlacementC.getRefDirection();
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
                        
                        planchaActual.objectPlacement.setRelativePlacement_refDirection(coord);
                    }
                    /********************************************************************************/
                    
                    
                    //Se lee Representation
                    
                    IfcProductDefinitionShape representation = (IfcProductDefinitionShape) planchaEncontrada.getRepresentation();
                    LIST<IfcRepresentation> representations = representation.getRepresentations();
                    //se asume que siempre va a existir UNA sola representacion (SOLO SE LEE LA POSICION 0)
                    IfcRepresentation representationActual = representations.get(0);
                    String representationType = representationActual.getRepresentationType().toString();
                    planchaActual.representation.setRepresentation_representationType(representationType);
                    
                    SET<IfcRepresentationItem> items = representationActual.getItems();
                    Iterator it = items.iterator();
                    //se asume que siempre va a existir UNA sola representacion (SOLO SE LEE EL PRIMER ITEM)
                    IfcExtrudedAreaSolid itemActual = (IfcExtrudedAreaSolid) it.next();
                    
                    //System.err.println(representationType + " PLANCHA " + planchaActual.getId() + " PISO " + pisoActual.getNombre() + " DEPTH = " + itemActual.getDepth().value + " EXTRUDED DIR = " + itemActual.getExtrudedDirection().getDirectionRatios());
                    
                    
                    IfcAxis2Placement3D position = itemActual.getPosition();
                    IfcCartesianPoint location = position.getLocation();
                    LIST<IfcLengthMeasure> coordinatesD = location.getCoordinates();
                    
                    int coordenadas = coordinatesD.size();
                    
                    Coordenada coord = new Coordenada();
                    for(int n = 0; n<coordenadas;n++){
                        double valor = (Double) coordinatesD.get(n).value;
                        
                        switch (n) {
						case 0: coord.setX(valor); break;
						case 1: coord.setY(valor); break;
						case 2: coord.setZ(valor); break;
						}
                        
                    }
                    
                    planchaActual.representation.setRepresentation_position_location(coord);
                    
                    /********************************************/
                    
                    IfcDirection direction = itemActual.getExtrudedDirection();
                    LIST<DOUBLE> coordinatesF = direction.getDirectionRatios();
                    
                    coordenadas = coordinatesF.size();
                    
                    coord = new Coordenada();
                    for(int n = 0; n<coordenadas;n++){
                        double valor = (Double) coordinatesF.get(n).value;
                        
                        switch (n) {
						case 0: coord.setX(valor); break;
						case 1: coord.setY(valor); break;
						case 2: coord.setZ(valor); break;
						}
                        
                    }
                    
                    planchaActual.representation.setRepresentation_extruded_direction(coord);
                    
                    /************************************************/
                    
                    //System.out.println(pisoActual.getNombre());
                    //planchaActual
                    
                    //Definition from IAI: If the attribute values for Axis and RefDirection are not given, the placement defaults to P[1] (x-axis) as [1.,0.,0.], P[2] (y-axis) as [0.,1.,0.] and P[3] (z-axis) as [0.,0.,1.]. 
                    if(position.getAxis() != null){
                    	
                    	IfcDirection axis = position.getAxis();
                        LIST<DOUBLE> directionRatios = axis.getDirectionRatios();
                        
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
                        
                        planchaActual.representation.setRepresentation_position_axis(coord);
                        
                        IfcDirection refDirection = position.getRefDirection();
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
                        
                        planchaActual.representation.setRepresentation_position_refDirection(coord);
                    }
                    
                    //System.err.println("          " + " PLANCHA " + planchaActual.getId() + " PISO " + pisoActual.getNombre() + " AXIS = " + planchaActual.representation.getRepresentation_position_axis());
                    //System.err.println("          " + " PLANCHA " + planchaActual.getId() + " PISO " + pisoActual.getNombre() + " RDIR = " + planchaActual.representation.getRepresentation_position_refDirection());
                    
                    
                    if(itemActual.getSweptArea() instanceof IfcArbitraryClosedProfileDef){
                    	
                    	IfcArbitraryClosedProfileDef sweptArea = (IfcArbitraryClosedProfileDef) itemActual.getSweptArea();
                        //int at = (Integer) sweptAreaAtributos.get("Count");
                        //sobra?
                        planchaActual.representation.setRepresentation_representation_SweptAreaType(sweptArea.getClass().getSimpleName());
                        Object outerCurve = sweptArea.getOuterCurve();
                    	
                        if(outerCurve instanceof IfcPolyline){
                        	
                        	planchaActual.setRepresentation_points(new ArrayList());
                        	IfcPolyline outerCurveActual = (IfcPolyline) outerCurve; 
                        	LIST<IfcCartesianPoint> points = outerCurveActual.getPoints();
                        	
                        	int puntos = points.size();
                            
                            for(int n = 0; n<puntos;n++){
                            	
                            	
                            	IfcCartesianPoint pointsActual = points.get(n);
                            	LIST<IfcLengthMeasure> coordinatesE = pointsActual.getCoordinates();
                                int coordenadasE = coordinatesE.size();
                                Coordenada coordenada = new Coordenada();
                                
                                for(int i = 0; i<coordenadasE;i++){
                                	
                                    double valor = coordinatesE.get(i).value;
                                    switch (i) {
									case 0: coordenada.setX(valor); break;
									case 1: coordenada.setY(valor); break;
									}
                                }
                                
                                planchaActual.getRepresentation_points().add(coordenada);
                                
                            }                            
                        	
                        }
                    	
                        
                        if(outerCurve instanceof IfcCompositeCurve){
                        	
                        	planchaActual.setRepresentation_segmentos(new ArrayList());
                        	IfcCompositeCurve outerCurveActual = (IfcCompositeCurve) outerCurve;
                        	LIST<IfcCompositeCurveSegment> segments = outerCurveActual.getSegments();
                        	int segmentos = segments.size();
                            
                            for(int n = 0; n<segmentos;n++){
                            	
                            	Segmento segmento = new Segmento();
                            	
                            	IfcCompositeCurveSegment segmentoActual = segments.get(n);
                            	IfcCurve parentCurve = segmentoActual.getParentCurve();
                            	
                                if(parentCurve instanceof IfcPolyline){
                               	
                                	IfcPolyline parentCurveActual = (IfcPolyline) parentCurve;
                                	LIST<IfcCartesianPoint> points = parentCurveActual.getPoints();
                                	int puntos = points.size();
                                    
                                    Coordenada p0 = new Coordenada();
                                    Coordenada p1 = new Coordenada();
                                    
                                    for(int i = 0; i<puntos;i++){
                                    	
                                    	IfcCartesianPoint puntoActual = points.get(i);
                                    	LIST<IfcLengthMeasure> coordinates = puntoActual.getCoordinates(); 
                                        coordenadas = coordinates.size();
                                        
                                        for(int m = 0; m<coordenadas;m++){
                                        	
                                            double valor = coordinates.get(m).value;
                                            
                                            if(i==0){
                                            	switch (m) {
            									case 0: p0.setX(valor); break;
            									case 1: p0.setY(valor); break;
            									}
                                            	
                                            }
                                            if(i==1){
                                            	switch (m) {
            									case 0: p1.setX(valor); break;
            									case 1: p1.setY(valor); break;
            									}
                                            	
                                            }
                                            
                                        }
                                        
                                        
                                    }
                                    
                                    segmento.setP0(p0);
                                    segmento.setP1(p1);
                                    
                                    planchaActual.getRepresentation_segmentos().add(segmento);
                                    
                                }
                                
                                if(parentCurve instanceof IfcTrimmedCurve){
                                	
                                	//no se tiene en cuenta IfcTrimmedCurve
                                }
                            }
                        }
                    }
                    
                    if(itemActual.getSweptArea() instanceof IfcRectangleProfileDef){
                        
                    	
                    	IfcRectangleProfileDef sweptArea = (IfcRectangleProfileDef) itemActual.getSweptArea();
                    	planchaActual.representation.setRepresentation_representation_SweptAreaType(sweptArea.getClass().getSimpleName());
                    	
                    	Rectangulo rec = new Rectangulo();
                    	
                    	IfcPositiveLengthMeasure xdim = sweptArea.getXDim();
                        rec.setXDim(xdim.value);
                        
                        IfcPositiveLengthMeasure ydim = sweptArea.getYDim();
                        rec.setYDim(ydim.value);
                        
                        IfcAxis2Placement2D positionD = sweptArea.getPosition();
                        IfcCartesianPoint locationD = positionD.getLocation();
                        LIST<IfcLengthMeasure> coordinates = locationD.getCoordinates();
                        coordenadas = coordinates.size();
                        
                        Coordenada coordenada = new Coordenada();
                        
                        for(int i = 0; i<coordenadas;i++){
                        	
                        	double valor = coordinates.get(i).value;
                            switch (i) {
							case 0: coordenada.setX(valor); break;
							case 1: coordenada.setY(valor); break;
							}
                        }
                        
                        rec.setPosition_location(coordenada);

                        IfcDirection refDirectionD = positionD.getRefDirection();
                        LIST<DOUBLE> directionRatiosD = refDirectionD.getDirectionRatios();
                        coordenadas = directionRatiosD.size();
                        
                        coordenada = new Coordenada();
                        
                        for(int i = 0; i<coordenadas;i++){
                        	
                        	double valor = directionRatiosD.get(i).value;
                            switch (i) {
							case 0: coordenada.setX(valor); break;
							case 1: coordenada.setY(valor); break;
							}
                        }
                        
                        rec.setPosition_refDirection(coordenada);
                        
                        planchaActual.setRectangulo(rec);
                        
                        
                    }
                     
                    //se calculan las coordenadas absolutas de la geometria que define el perfil de la plancha
                    planchaActual.calcularCoordenadasAbsolutas();
                    
            	}
            	
			}
			
			
		} catch (Exception e) {
            e.printStackTrace();
        } finally {
            //rm.release();
        }
		
	}

}
