package co.edu.udistrital.bim2gis4j.util;

/*
 * This file is part of citygml4j.
 * Copyright (c) 2007 - 2010
 * Institute for Geodesy and Geoinformation Science
 * Technische Universitaet Berlin, Germany
 * http://www.igg.tu-berlin.de/
 *
 * The citygml4j library is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see 
 * <http://www.gnu.org/licenses/>.
 */
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.JAXBBuilder;
import org.citygml4j.factory.CityGMLFactory;
import org.citygml4j.factory.GMLFactory;
import org.citygml4j.factory.geometry.DimensionMismatchException;
import org.citygml4j.factory.geometry.GMLGeometryFactory;
import org.citygml4j.impl.gml.geometry.primitives.InteriorImpl;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.AbstractOpening;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.building.OpeningProperty;
import org.citygml4j.model.citygml.building.Window;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.gml.geometry.complexes.CompositeSurface;
import org.citygml4j.model.gml.geometry.primitives.AbstractSurface;
import org.citygml4j.model.gml.geometry.primitives.Interior;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.Solid;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.model.module.citygml.CityGMLVersion;
import org.citygml4j.util.gmlid.DefaultGMLIdManager;
import org.citygml4j.util.gmlid.GMLIdManager;
import org.citygml4j.xml.io.CityGMLOutputFactory;
import org.citygml4j.xml.io.writer.CityGMLWriter;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Edificio;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Muro;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Piso;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Plancha;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Poligono;
import co.edu.udistrital.bim2gis4j.ifc.Solido;

import com.vividsolutions.jts.geom.Coordinate;


public class BuildingCreator {
	private static CityGMLFactory citygml;
	private static GMLFactory gml;

	public void crearModeloLOD1(Coordinate[] coordenadas, double elevacion, String archivoSalida) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("[HH:mm:ss] "); 

		System.out.println(df.format(new Date()) + "setting up citygml4j context and JAXB builder");
		CityGMLContext ctx = new CityGMLContext();
		JAXBBuilder builder = ctx.createJAXBBuilder();

		System.out.println(df.format(new Date()) + "creating LOD1 building as citygml4j in-memory object tree");
		GMLGeometryFactory geom = new GMLGeometryFactory();
		citygml = new CityGMLFactory();
		gml = new GMLFactory();

		GMLIdManager gmlIdManager = DefaultGMLIdManager.getInstance();

		Building building = citygml.createBuilding();

		List<AbstractSurface> shell = new ArrayList<AbstractSurface>();
		
		for(int c=0; c < coordenadas.length -1 ; c++){
        	Coordinate coordenadaActual = coordenadas[c];
        	Coordinate coordenadaSiguiente = coordenadas[c+1];
        	
        	Polygon poligonoActual = geom.createLinearPolygon(
        			new double[]
        			           {
        					coordenadaActual.x, coordenadaActual.y,0,
        					coordenadaActual.x, coordenadaActual.y,elevacion,
        					coordenadaSiguiente.x, coordenadaSiguiente.y, elevacion,
        					coordenadaSiguiente.x, coordenadaSiguiente.y, 0,
        					coordenadaActual.x, coordenadaActual.y, 0
        					}
        			, 3
        			);
        	
        	shell.add(poligonoActual);
        }
		
		

		double[] tapa = new double[coordenadas.length*3];
		int c = 0;		
		for(int n=coordenadas.length -1; n >=0  ; n--){
        	Coordinate coordenadaActual = coordenadas[n];
			
			tapa[c] = coordenadaActual.x;
        	tapa[c+1] = coordenadaActual.y;
        	tapa[c+2] = elevacion;
        	
        	c=c+3;
		}

		Polygon poligonoTapa = geom.createLinearPolygon(tapa, 3);
		
		shell.add(poligonoTapa);
		

		
		CompositeSurface exterior = gml.createCompositeSurface(shell);
		Solid solid = gml.createSolid();
		solid.setExterior(gml.createSurfaceProperty(exterior));

		building.setLod1Solid(gml.createSolidProperty(solid));


		CityModel cityModel = citygml.createCityModel();
		cityModel.setBoundedBy(building.calcBoundedBy(false));
		cityModel.addCityObjectMember(citygml.createCityObjectMember(building));

		System.out.println(df.format(new Date()) + "writing citygml4j object tree");
		CityGMLOutputFactory out = builder.createCityGMLOutputFactory(CityGMLVersion.v1_0_0);
		CityGMLWriter writer = out.createCityGMLWriter(new File(archivoSalida));

		writer.setPrefixes(CityGMLVersion.v1_0_0);
		writer.setSchemaLocations(CityGMLVersion.v1_0_0);
		writer.setIndentString("  ");
		writer.write(cityModel);
		writer.close();	
		
		System.out.println(df.format(new Date()) + "CityGML file " + archivoSalida + " written");
		System.out.println(df.format(new Date()) + "sample citygml4j application successfully finished");
	}
	//deberia recibir, caras de planchas y techos
	public void crearModeloLOD2(Edificio edificio, String archivoSalida) throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("[HH:mm:ss] "); 

		System.out.println(df.format(new Date()) + "setting up citygml4j context and JAXB builder");
		CityGMLContext ctx = new CityGMLContext();
		JAXBBuilder builder = ctx.createJAXBBuilder();

		System.out.println(df.format(new Date()) + "creating LOD2 building as citygml4j in-memory object tree");
		GMLGeometryFactory geom = new GMLGeometryFactory();
		citygml = new CityGMLFactory();
		gml = new GMLFactory();

		GMLIdManager gmlIdManager = DefaultGMLIdManager.getInstance();

		Building building = citygml.createBuilding();
		
		
		List<Polygon> poligonosPlanchas = new ArrayList();
		
		List<Polygon> poligonosTechos = new ArrayList();
		
		List<Polygon> poligonosMuros = new ArrayList();
		
		
		for (Piso pisoActual : edificio.getPisos()) {
        	for(Plancha planchaActual : pisoActual.getPlanchas()){
        		for(Poligono poligonoActual : planchaActual.getCaras()){
        			
        			poligonoActual.corregirOrientacion();
        			
        			List<Double> coordenadasEstePoligono = new ArrayList(); 
        			
        			for(Coordenada coordenadaActual : poligonoActual.getCoordenadas()){
        				coordenadasEstePoligono.add(coordenadaActual.getX());
        				coordenadasEstePoligono.add(coordenadaActual.getY());
        				coordenadasEstePoligono.add(coordenadaActual.getZ());
        			}
        			
        			if(planchaActual.getTipo().equals("FLOOR") || planchaActual.getTipo().equals("BASESLAB")){
        				poligonosPlanchas.add(geom.createLinearPolygon(coordenadasEstePoligono, 3));
        			}
        			if(planchaActual.getTipo().equals("ROOF")){
        				poligonosTechos.add(geom.createLinearPolygon(coordenadasEstePoligono, 3));
        			}
        			
        		}
        	}
        	
		}
		
		
		for (Piso pisoActual : edificio.getPisos()) {
        	for(Muro muroActual : pisoActual.getMuros()){
        		for(Poligono poligonoActual : muroActual.getCaras()){
        			
        			poligonoActual.corregirOrientacion();
        			
        			List<Double> coordenadasEstePoligono = new ArrayList(); 
        			
        			for(Coordenada coordenadaActual : poligonoActual.getCoordenadas()){
        				coordenadasEstePoligono.add(coordenadaActual.getX());
        				coordenadasEstePoligono.add(coordenadaActual.getY());
        				coordenadasEstePoligono.add(coordenadaActual.getZ());
        			}
        			

        			try {
        				
        				poligonosMuros.add(geom.createLinearPolygon(coordenadasEstePoligono, 3));
						
					} catch (Exception e) {
					System.err.println("EXCEPECION EN MURO id = " + muroActual.getId() + " poligono vacio = " + poligonoActual);
					}
       				
        			
        		}
        	}
        	
		}
		
		
		for (Polygon polygonActual : poligonosPlanchas) {
			polygonActual.setId(gmlIdManager.generateGmlId());
		}
		
		for (Polygon polygonActual : poligonosTechos) {
			polygonActual.setId(gmlIdManager.generateGmlId());
		}
		for (Polygon polygonActual : poligonosMuros) {
			polygonActual.setId(gmlIdManager.generateGmlId());
		}

		// lod2 solid
		List<SurfaceProperty> surfaceMember = new ArrayList<SurfaceProperty>();
		
		for (Polygon polygonActual : poligonosPlanchas) {
			surfaceMember.add(gml.createSurfaceProperty('#' + polygonActual.getId()));
		}
		
		for (Polygon polygonActual : poligonosTechos) {
			surfaceMember.add(gml.createSurfaceProperty('#' + polygonActual.getId()));
		}
		
		for (Polygon polygonActual : poligonosMuros) {
			surfaceMember.add(gml.createSurfaceProperty('#' + polygonActual.getId()));
		}
		
		CompositeSurface compositeSurface = gml.createCompositeSurface();
		compositeSurface.setSurfaceMember(surfaceMember);		
		Solid solid = gml.createSolid();
		solid.setExterior(gml.createSurfaceProperty(compositeSurface));

		building.setLod2Solid(gml.createSolidProperty(solid));
		
		// thematic boundary surfaces
		List<BoundarySurfaceProperty> boundedBy = new ArrayList<BoundarySurfaceProperty>();
		
		for (Polygon polygonActual : poligonosPlanchas) {
			boundedBy.add(createBoundarySurface(CityGMLClass.FLOOR_SURFACE, polygonActual, null, null));
		}
		
		for (Polygon polygonActual : poligonosTechos) {
			boundedBy.add(createBoundarySurface(CityGMLClass.ROOF_SURFACE, polygonActual, null, null));
		}
		
		for (Polygon polygonActual : poligonosMuros) {
			boundedBy.add(createBoundarySurface(CityGMLClass.WALL_SURFACE, polygonActual, null, null));
		}
		
		building.setBoundedBySurface(boundedBy);
		
		CityModel cityModel = citygml.createCityModel();
		cityModel.setBoundedBy(building.calcBoundedBy(false));
		cityModel.addCityObjectMember(citygml.createCityObjectMember(building));

		System.out.println(df.format(new Date()) + "writing citygml4j object tree");
		CityGMLOutputFactory out = builder.createCityGMLOutputFactory(CityGMLVersion.v1_0_0);
		CityGMLWriter writer = out.createCityGMLWriter(new File(archivoSalida));

		writer.setPrefixes(CityGMLVersion.v1_0_0);
		writer.setSchemaLocations(CityGMLVersion.v1_0_0);
		writer.setIndentString("  ");
		writer.write(cityModel);
		writer.close();	
		
		System.out.println(df.format(new Date()) + "CityGML file " + archivoSalida + " written");
		System.out.println(df.format(new Date()) + "sample citygml4j application successfully finished");
	}
	
	private BoundarySurfaceProperty createBoundarySurface(CityGMLClass type, Polygon geometry, List<Poligono> pCarasInternas, List<Poligono> pCarasTijera) {
		AbstractBoundarySurface boundarySurface = null;

		switch (type) {
		case WALL_SURFACE:
			boundarySurface = citygml.createWallSurface();
			break;
		case ROOF_SURFACE:
			boundarySurface = citygml.createRoofSurface();
			break;
		case GROUND_SURFACE:
			boundarySurface = citygml.createGroundSurface();
			break;
		case FLOOR_SURFACE:
			boundarySurface = citygml.createFloorSurface();
			break;
		}

		if (boundarySurface != null) {
			boundarySurface.setLod3MultiSurface(gml.createMultiSurfaceProperty(gml.createMultiSurface(geometry)));
			agregarCarasDeVentanaOPuerta(type, boundarySurface,pCarasInternas,pCarasTijera);
			return citygml.createBoundarySurfaceProperty(boundarySurface);
		}

		return null;
	}
	
	
	
	private AbstractBoundarySurface agregarCarasDeVentanaOPuerta(CityGMLClass type, AbstractBoundarySurface boundarySurface, List<Poligono> pCarasInternas, List<Poligono> pCarasTijera) {
		
		//primero es necesario invertir las caras, porque siempre van a estar en orden inverso al muro que las contiene
		//ya que asi lo maneja GML
		//si no se invierten en algunas aplicaciones no serán renderizadas
		//esto se debe a que se estan usando las mismas caras de los vacios para generar las puertas y ventanas
		//cuando se utilicen las caras propias de la representation de cada puerta y ventana esto ya no será necesario
		
		if(pCarasInternas!=null)
		for (Poligono poligono : pCarasInternas) {
			poligono.invertirOrientacion();
		}
		
		if(pCarasTijera!=null)
		for (Poligono poligono : pCarasTijera) {
			poligono.invertirOrientacion();
		}
		
		
		//si hay caras internas dentro de esta cara, se deben agregar superficies bien sea de ventana o de puerta a la cara generada
		if(pCarasInternas!=null){
			if(type==CityGMLClass.WALL_SURFACE) {
				for (Poligono poligonoActual : pCarasInternas) {
					if(poligonoActual.getVacioPadre().getTipo().equals("ventana")){
						
						OpeningProperty superficieVentana = null;
						try {
							superficieVentana = createOpeningSurface(CityGMLClass.WINDOW, poligonoActual.getPoligonoGML());
						} catch (DimensionMismatchException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boundarySurface.addOpening(superficieVentana);
						
					}
					
					if(poligonoActual.getVacioPadre().getTipo().equals("puerta")){
						
						OpeningProperty superficiePuerta = null;
						try {
							superficiePuerta = createOpeningSurface(CityGMLClass.DOOR, poligonoActual.getPoligonoGML());
						} catch (DimensionMismatchException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boundarySurface.addOpening(superficiePuerta);
						
					}
				}
			}
		}
		
		
		//si hay caras tijera para esta cara, se deben agregar superficies bien sea de ventana o de puerta a la cara generada
		if(pCarasTijera!=null){
			if(type==CityGMLClass.WALL_SURFACE) {
				for (Poligono poligonoActual : pCarasTijera) {
					if(poligonoActual.getVacioPadre().getTipo().equals("ventana")){
						
						OpeningProperty superficieVentana = null;
						try {
							superficieVentana = createOpeningSurface(CityGMLClass.WINDOW, poligonoActual.getPoligonoGML());
						} catch (DimensionMismatchException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boundarySurface.addOpening(superficieVentana);
						
					}
					
					if(poligonoActual.getVacioPadre().getTipo().equals("puerta")){
						
						OpeningProperty superficiePuerta = null;
						try {
							superficiePuerta = createOpeningSurface(CityGMLClass.DOOR, poligonoActual.getPoligonoGML());
						} catch (DimensionMismatchException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boundarySurface.addOpening(superficiePuerta);
						
					}
				}
			}
		}
		
		return boundarySurface;
		
	}
	
	
	public static OpeningProperty createOpeningSurface(CityGMLClass type, Polygon geometry) {
		AbstractOpening opening = null;

		switch (type) {
		case WINDOW:
			opening = citygml.createWindow();
			break;
		case DOOR:
			opening = citygml.createDoor();
			break;
		}
		
		if (opening != null) {
			opening.setLod3MultiSurface(gml.createMultiSurfaceProperty(gml.createMultiSurface(geometry)));
			
			OpeningProperty openingProperty = citygml.createOpeningProperty();
			openingProperty.setObject(opening);
			
			return openingProperty;
		}

		return null;
	}	
	
	public void crearModeloLOD3(Edificio edificio, String archivoSalida) throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("[HH:mm:ss] "); 

		System.out.println(df.format(new Date()) + "setting up citygml4j context and JAXB builder");
		CityGMLContext ctx = new CityGMLContext();
		JAXBBuilder builder = ctx.createJAXBBuilder();

		System.out.println(df.format(new Date()) + "creating LOD3 building as citygml4j in-memory object tree");
		GMLGeometryFactory geom = new GMLGeometryFactory();
		citygml = new CityGMLFactory();
		gml = new GMLFactory();

		GMLIdManager gmlIdManager = DefaultGMLIdManager.getInstance();

		Building building = citygml.createBuilding();
		
		
		List<Polygon> poligonosPlanchas = new ArrayList();
		
		List<Polygon> poligonosTechos = new ArrayList();
		
		//List<Polygon> poligonosMuros = new ArrayList();
		List<Poligono> poligonosMurosNew = new ArrayList();
		
		
		for (Piso pisoActual : edificio.getPisos()) {
        	for(Plancha planchaActual : pisoActual.getPlanchas()){
        		for(Poligono poligonoActual : planchaActual.getCaras()){
        			
        			List<Double> coordenadasEstePoligono = new ArrayList(); 
        			
        			for(Coordenada coordenadaActual : poligonoActual.getCoordenadas()){
        				coordenadasEstePoligono.add(coordenadaActual.getX());
        				coordenadasEstePoligono.add(coordenadaActual.getY());
        				coordenadasEstePoligono.add(coordenadaActual.getZ());
        			}
        			
        			if(planchaActual.getTipo().equals("FLOOR") || planchaActual.getTipo().equals("BASESLAB")){
        				poligonosPlanchas.add(geom.createLinearPolygon(coordenadasEstePoligono, 3));
        			}
        			if(planchaActual.getTipo().equals("ROOF")){
        				poligonosTechos.add(geom.createLinearPolygon(coordenadasEstePoligono, 3));
        			}
        			
        		}
        	}
        	
		}
		
		
		for (Piso pisoActual : edificio.getPisos()) {
        	for(Muro muroActual : pisoActual.getMuros()){
        		for(Poligono poligonoActual : muroActual.getCaras()){
        			try {
        				//poligonosMuros.add(poligonoActual.obtenerPolygonGML());
        				poligonoActual.calcularPolygonGML();
        				poligonosMurosNew.add(poligonoActual);
					} catch (Exception e) {
					System.err.println("EXCEPECION EN MURO id = " + muroActual.getId() + " poligono vacio = " + poligonoActual);
					}
       				
        			
        		}
        	}
        	
		}
		
		
		for (Polygon polygonActual : poligonosPlanchas) {
			polygonActual.setId(gmlIdManager.generateGmlId());
		}
		
		for (Polygon polygonActual : poligonosTechos) {
			polygonActual.setId(gmlIdManager.generateGmlId());
		}
		for (Poligono poligonoActual : poligonosMurosNew) {
			
			poligonoActual.getPoligonoGML().setId(gmlIdManager.generateGmlId());

		}

		// lod2 solid
		List<SurfaceProperty> surfaceMember = new ArrayList<SurfaceProperty>();
		
		for (Polygon polygonActual : poligonosPlanchas) {
			surfaceMember.add(gml.createSurfaceProperty('#' + polygonActual.getId()));
		}
		
		for (Polygon polygonActual : poligonosTechos) {
			surfaceMember.add(gml.createSurfaceProperty('#' + polygonActual.getId()));
		}
		
		for (Poligono poligonoActual : poligonosMurosNew) {
			surfaceMember.add(gml.createSurfaceProperty('#' + poligonoActual.getPoligonoGML().getId()));
		}
		
		CompositeSurface compositeSurface = gml.createCompositeSurface();
		compositeSurface.setSurfaceMember(surfaceMember);		
		Solid solid = gml.createSolid();
		solid.setExterior(gml.createSurfaceProperty(compositeSurface));

		building.setLod3Solid(gml.createSolidProperty(solid));
		
		// thematic boundary surfaces
		List<BoundarySurfaceProperty> boundedBy = new ArrayList<BoundarySurfaceProperty>();
		
		for (Polygon polygonActual : poligonosPlanchas) {
			boundedBy.add(createBoundarySurface(CityGMLClass.FLOOR_SURFACE, polygonActual, null, null));
		}
		
		for (Polygon polygonActual : poligonosTechos) {
			boundedBy.add(createBoundarySurface(CityGMLClass.ROOF_SURFACE, polygonActual, null, null));
		}
		
		for (Poligono poligonoActual : poligonosMurosNew) {
			BoundarySurfaceProperty muro = createBoundarySurface(CityGMLClass.WALL_SURFACE, poligonoActual.getPoligonoGML(), poligonoActual.getCarasInternas(), poligonoActual.getCarasTijera()) ;
			boundedBy.add(muro);
		}
		
		building.setBoundedBySurface(boundedBy);
		
		CityModel cityModel = citygml.createCityModel();
		cityModel.setBoundedBy(building.calcBoundedBy(false));
		cityModel.addCityObjectMember(citygml.createCityObjectMember(building));

		System.out.println(df.format(new Date()) + "writing citygml4j object tree");
		CityGMLOutputFactory out = builder.createCityGMLOutputFactory(CityGMLVersion.v1_0_0);
		CityGMLWriter writer = out.createCityGMLWriter(new File(archivoSalida));

		writer.setPrefixes(CityGMLVersion.v1_0_0);
		writer.setSchemaLocations(CityGMLVersion.v1_0_0);
		writer.setIndentString("  ");
		writer.write(cityModel);
		writer.close();	
		
		System.out.println(df.format(new Date()) + "CityGML file " + archivoSalida + " written");
		System.out.println(df.format(new Date()) + "sample citygml4j application successfully finished");
		
	}

}