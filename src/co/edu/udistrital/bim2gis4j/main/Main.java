package co.edu.udistrital.bim2gis4j.main;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import co.edu.udistrital.bim2gis4j.util.BuildingCreator;
import co.edu.udistrital.bim2gis4j.util.LatLonConvert;
import co.edu.udistrital.bim2gis4j.util.LectorModeloIfc;
import co.edu.udistrital.bim2gis4j.util.LectorMuros;
import co.edu.udistrital.bim2gis4j.util.LectorPlanchas;
import co.edu.udistrital.bim2gis4j.util.LectorVacios;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Muro;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Plancha;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Edificio;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Piso;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Vacio;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBuildingStorey;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcOpeningElement;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcProduct;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRelAggregates;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRelContainedInSpatialStructure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRoof;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcSite;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcSlab;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcSlabTypeEnum;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcSlabTypeEnum.IfcSlabTypeEnum_internal;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.SET;
import openifctools.com.openifcjavatoolbox.ifcmodel.IfcModel;
import openifctools.com.openifcjavatoolbox.step.parser.util.ProgressEvent;
import openifctools.com.openifcjavatoolbox.step.parser.util.StepParserProgressListener;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

public class Main {
	
	private static LectorModeloIfc lectorModeloIfc = new LectorModeloIfc();
	private static LectorPlanchas lectorPlanchas = new LectorPlanchas();
	private static LectorMuros lectorMuros = new LectorMuros();
	private static LectorVacios lectorVacios = new LectorVacios();

	private static IfcModel ifcModel = null;
	private static Edificio edificio;
	
	//Se genera el modelo LOD1
    private static BuildingCreator creador  = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		edificio = new Edificio();

		int[] coordLatitud = null;
		int[] coordLongitud = null;
		
		String rutaArchivo = args[0]; 

		ifcModel = lectorModeloIfc.cargarModeloIfc(rutaArchivo);
		
		//se cargan los datos basicos de planchas al edificio
		lectorPlanchas.cargarDatosBasicos(ifcModel, edificio);

		// SE LEEN LAS COORDENADAS GEOGRAFICAS
		// siempre devuelve un vector de una sola posicion
		Iterator i = ifcModel.getCollection(IfcSite.class).iterator();
		IfcSite site = (IfcSite) i.next();

		int valores = site.getRefLatitude().size();

		coordLatitud = new int[valores];
		boolean latitudNegativa = false;

		for (int n = 0; n < valores; n++) {

			int valor = site.getRefLatitude().get(n).value;

			if (n == 0 && valor < 0) {
				latitudNegativa = true;
			}
			// debido a que IFC guarda TODOS los valores de la latitud como
			// negativos
			// ese necesario ajustarlos
			// ejemplo IFC : -74°-3'-57.-56400'' (MAL !!!)
			// ajustado : -74° 3' 57.56400'' (OK !!!)
			if (n > 0 && latitudNegativa && valor < 0) {
				valor = valor * -1;
			}

			coordLatitud[n] = valor;

			// System.out.println("lat n = " + n + " valor = " +
			// coordLatitud[n]);
		}

		valores = site.getRefLongitude().size();

		coordLongitud = new int[valores];
		boolean longitudNegativa = false;

		for (int n = 0; n < valores; n++) {
			int valor = site.getRefLongitude().get(n).value;

			if (n == 0 && valor < 0) {
				longitudNegativa = true;
			}
			// debido a que IFC guarda TODOS los valores de la latitud como
			// negativos
			// ese necesario ajustarlos
			// ejemplo IFC : -74°-3'-57.-56400'' (MAL !!!)
			// ajustado : -74° 3' 57.56400'' (OK !!!)
			if (n > 0 && longitudNegativa && valor < 0) {
				valor = valor * -1;
			}

			coordLongitud[n] = valor;

			// System.out.println("lon n = " + n + " valor = " +
			// coordLongitud[n]);
		}

		// para ordenar los pisos de menor a mayor elevacion
		int posA = 0;
		int posB = 0;

		for (posA = 0; posA < (edificio.getPisos().size() - 1); posA++) {
			for (posB = posA + 1; posB < edificio.getPisos().size(); posB++) {
				Piso pisoA = edificio.getPisos().get(posA);
				Piso pisoB = edificio.getPisos().get(posB);
				if (pisoA.getElevacion() > pisoB.getElevacion()) {
					Piso aux = pisoA;
					edificio.getPisos()
							.set(posA, edificio.getPisos().get(posB));
					edificio.getPisos().set(posB, aux);
				}

			}
		}

		double mayorElevacionEncontrada = 0;

		for (posA = 0; posA < edificio.getPisos().size(); posA++) {
			Piso pisoA = edificio.getPisos().get(posA);
			if (pisoA.getElevacion() > mayorElevacionEncontrada)
				mayorElevacionEncontrada = pisoA.getElevacion();
		}

		// se borran los pisos que no tengan planchas
		List<Piso> borrar = new ArrayList();
		for (Piso pisoA : edificio.getPisos()) {
			if (pisoA.getPlanchas() == null || pisoA.getPlanchas().size() < 1) {
				borrar.add(pisoA);
			}
		}

		edificio.getPisos().removeAll(borrar);
		lectorPlanchas.leerPlanchas(ifcModel, edificio.getPisos());
        
        //int pisoMinimo = edificio.getPisos().size() - 4;
        int pisoMinimo = 0;
        
/************************ CALCULO DE COORDENADAS GEOGRAFICAS UTM BASADO EN GRADOS MINUTOS Y SEGUNDOS ************************************************************/
        
		//se prueba con las coordenadas del edificio sabio caldas asignadas en el archivo IFC
		// la latitud y longitud reales del edificio son : 4.62787 -74.065849 pero IFC al parecer las trunca
        // se puede probar en http://netvicious.com/gps/
        
        double latitudGrados = coordLatitud[0];
        double latitudMinutos = coordLatitud[1];
        double latitudSegundos = (coordLatitud.length == 3) ? coordLatitud[2] : Double.parseDouble(coordLatitud[2] + "." + coordLatitud[3]);
        
        
        
        double longitudGrados = coordLongitud[0];
        double longitudMinutos = coordLongitud[1];
        double longitudSegundos = (coordLongitud.length == 3) ? coordLongitud[2] : Double.parseDouble(coordLongitud[2] + "." + coordLongitud[3]);
        
		
		LatLonConvert lat = new LatLonConvert(latitudGrados,latitudMinutos,latitudSegundos);
		//System.out.println(lat.getDecimal());
		
		LatLonConvert lon = new LatLonConvert(longitudGrados,longitudMinutos,longitudSegundos);
		//System.out.println(lon.getDecimal());
		
		
		LatLng ll = new LatLng(lat.getDecimal(), lon.getDecimal());
		UTMRef utm = ll.toUTMRef();
		//System.out.println(utm);
		
		// northing is the distance in metres to the equator
		//System.out.println(utm.getNorthing());
		
		// easting is the distance in metres to the false easting — a meridian that is uniquely defined for each UTM zone
		//System.out.println(utm.getEasting());
		

		
		// ---------------------------- CALCULO DE COORDENADAS GEOGRAFICAS UTM BASADO EN GRADOS MINUTOS Y SEGUNDOS ----------------------------         
        
        GeometryFactory fact = new GeometryFactory();
        Geometry unionTodasLasPlanchas = fact.createGeometryCollection(null);
        
        for (Piso pisoA : edificio.getPisos()) {
        	if(edificio.getPisos().indexOf(pisoA) >= pisoMinimo){

        		//para obtener las coordenadas de IFC se podria invocar pisoA.generarPoligonos(0, 0)
        		MultiPolygon poligonosPisoActual = fact.createMultiPolygon(pisoA.generarPoligonos(0, 0));
        		//MultiPolygon poligonosPisoActual = fact.createMultiPolygon(pisoA.generarPoligonos(utm.getEasting(), utm.getNorthing()));
        		
        		
        		Geometry unionEstePiso = fact.createGeometryCollection(null);
        		unionEstePiso = unionEstePiso.union(poligonosPisoActual);
        		
        		        		
        		if(unionEstePiso.isValid()){
        			unionTodasLasPlanchas = unionTodasLasPlanchas.union(unionEstePiso);        			
        		}else{
        			
        			//unionTodasLasPlanchas = unionTodasLasPlanchas.union(unionEstePiso);
        			
        			System.err.println("Geometria INVALIDA : " + unionEstePiso);
        		}
        		
        		
        	}
		}
        
        //si se quiere un modelo LOD1 poco detallado, descomentar esto
        //unionTodasLasPlanchas = unionTodasLasPlanchas.getEnvelope();
        
        
        lectorMuros.cargarDatosBasicos(ifcModel, edificio);
        lectorMuros.leerMuros(edificio.getPisos(), ifcModel);
        
        lectorVacios.cargarDatosBasicos(ifcModel, edificio);
        lectorVacios.leerVacios(ifcModel, edificio);
        
        
        
        
        Coordinate[] coordenadas = unionTodasLasPlanchas.getCoordinates();
        
		System.out.println("GEOMETRIA BASE PARA LOD1 = " + unionTodasLasPlanchas);
		
        

        
        //Se genera el modelo LOD1
        creador = new BuildingCreator();
        try {
			creador.crearModeloLOD1(coordenadas,mayorElevacionEncontrada,args[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
        // ------------------------------------------ PARA GENERAR LOD2
        
        System.out.println("\n\n");
        
//        int pisoMinimoLod2 = 0;
//        int pisoMaximoLod2 = 10;
        
        for (Piso pisoActual : edificio.getPisos()) {
        	
//        	if(edificio.getPisos().indexOf(pisoActual) >= pisoMinimoLod2
//        			&&
//        			edificio.getPisos().indexOf(pisoActual) <= pisoMaximoLod2){

        		for(Plancha planchaActual : pisoActual.getPlanchas()){
            		planchaActual.generarCaras();
            	}
            	
            	for(Muro muroActual : pisoActual.getMuros()){
            		muroActual.generarCaras();
            	}
//        	}
        	
        	
			
		}
        
        //Se genera el modelo LOD2
        //BuildingCreator creador = new BuildingCreator();
        try {
			creador.crearModeloLOD2(edificio, args[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
// ------------------------------------------ PARA GENERAR LOD3
        
        System.out.println("\n\n");
        
        //se generan las caras de todos lo vacios
        for (Piso pisoActual : edificio.getPisos()) {
        	
        	for(Muro muroActual : pisoActual.getMuros()){
        		
        		if(muroActual.getVacios() != null){
					
					for (Vacio vacioActual : muroActual.getVacios()){
						
						vacioActual.generarCaras();
					}
				}
        	}
		}
        
      //se generan las caras de todos lo vacios
        for (Piso pisoActual : edificio.getPisos()) {
        	
        	for(Muro muroActual : pisoActual.getMuros()){
        		
        		if(muroActual.getVacios() != null){
					
        			muroActual.abrirVaciosParaPuertasYVentanas();
        			
				}
        	}
		}
        
      //Se genera el modelo LOD3
        //BuildingCreator creador = new BuildingCreator();
        try {
			creador.crearModeloLOD3(edificio, args[3]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*
        //Se imprime el edificio para visualizar sus valores
        for (Piso pisoA : edificio.getPisos()) {
        	if(edificio.getPisos().indexOf(pisoA) >= pisoMinimo){
        		
        		pisoA.imprimir();
        		
        	}
		}
		*/
        
        //BEEP
        Toolkit.getDefaultToolkit().beep();
	}

}
