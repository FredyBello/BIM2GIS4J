package co.edu.udistrital.bim2gis4j.bim2gisbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.ifc.Solido;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentation;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentationItem;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcSlab;
import openifctools.com.openifcjavatoolbox.ifcmodel.IfcModel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class Plancha extends Solido {
	
	
	public void calcularCoordenadasAbsolutas(){
		
		if(representation_points!=null){
			//coordenadasAbsolutas.addAll(getRepresentation_points());
			
			coordenadasAbsolutas = new ArrayList();
			
			for (Coordenada coordActual : representation_points) {
				
				Coordenada coordAbsoluta;
				
				//Coordenada conRotacion = aplicarRotacionSegunRepresentation(coordOriginal);
				if(this.getTipo().equals("ROOF")){
					coordAbsoluta = aplicarObjectRepresentationParaRoof(coordActual);
				}else{
					coordAbsoluta = aplicarObjectRepresentation(coordActual);
				}
				
				coordAbsoluta = aplicarObjectPlacement(coordAbsoluta);
				
				coordenadasAbsolutas.add(coordAbsoluta);
				
			}
		}else if(representation_segmentos!=null){

			coordenadasAbsolutas = new ArrayList();
			
			int contador = 0;
			
			for (Segmento segmentoActual : representation_segmentos) {
				
				Coordenada primeraCoordenadaDelPoligono = new Coordenada();
				Coordenada coordenadaActual;
				
				coordenadaActual = segmentoActual.getP0();
				
				Coordenada coordAbsoluta = aplicarObjectRepresentation(coordenadaActual);
				coordAbsoluta = aplicarObjectPlacement(coordAbsoluta);
				coordenadasAbsolutas.add(coordAbsoluta);
				
				contador++;
				
				if(contador==representation_segmentos.size()){
					coordenadasAbsolutas.add(coordenadasAbsolutas.get(0));
				}
			}
			
			
		}else if(rectangulo!=null){
			
			List<Coordenada> cuatroEsquinas = new ArrayList();
			
			double ancho = 0;
			double alto = 0;
			
			//eje X relativo igual a ejex X real
			if(rectangulo.getPosition_refDirection().getX()!=0){
				ancho = rectangulo.getXDim();
				alto = rectangulo.getYDim();
			}
			
			//eje X relativo igual a eje Y real
			if(rectangulo.getPosition_refDirection().getY()!=0){
				ancho = rectangulo.getYDim();
				alto = rectangulo.getXDim();
			}
			
			Coordenada coord = new Coordenada();
			coord.setX((ancho / 2) * -1);
			coord.setY((alto / 2) * -1);
			cuatroEsquinas.add(coord); //inferior ixzquierda
			
			coord = new Coordenada();
			coord.setX((ancho / 2) * -1);
			coord.setY((alto / 2));
			cuatroEsquinas.add(coord);//superior izquierda
			
			coord = new Coordenada();
			coord.setX((ancho / 2));
			coord.setY((alto / 2));
			cuatroEsquinas.add(coord);//superior derecha
			
			coord = new Coordenada();
			coord.setX((ancho / 2));
			coord.setY((alto / 2) * -1);
			cuatroEsquinas.add(coord);//superior derecha
			
			coordenadasAbsolutas = new ArrayList();
			
			int c = 0;
			Coordenada primera = null;
			for (Coordenada coordenadaActual : cuatroEsquinas) {
				
				Coordenada coordAbsoluta;
				
				//Coordenada conRotacion = aplicarRotacionSegunRepresentation(coordOriginal);
				if(this.getTipo().equals("ROOF")){
					coordAbsoluta = aplicarObjectRepresentationParaRoof(coordenadaActual);
				}else{
					coordAbsoluta = aplicarObjectRepresentation(coordenadaActual);
				}
				
				coordAbsoluta = aplicarObjectPlacement(coordAbsoluta);
				
				
				
				coordenadasAbsolutas.add(coordAbsoluta);
				
				if(c==0){
					primera = coordAbsoluta;
				}
				c++;
			}
			//se añade la primer para cerrar el poligono
			coordenadasAbsolutas.add(primera);

			
		}
		
		//aplicarObjectPlacement(/*coordenadasAbsolutas*/);
		
	}
	
	
	public Coordenada aplicarObjectPlacement(Coordenada original){
		//Hay que rotar primero, o no funciona bien
		Coordenada conRotacion = aplicarRotacionSegunObjectPlacement(original);
		
		double xActual = conRotacion.getX();
		
		
		xActual += objectPlacement.placementRelTo_placementRelTo.getX();
		xActual += objectPlacement.placementRelTo_relativePlacement.getX();
		xActual += objectPlacement.relativePlacement.location.getX();
		
		double yActual = conRotacion.getY();
		
		
		yActual += objectPlacement.placementRelTo_placementRelTo.getY();
		yActual += objectPlacement.placementRelTo_relativePlacement.getY();
		yActual += objectPlacement.relativePlacement.location.getY();
		
		double zActual = conRotacion.getZ();
		
		zActual += objectPlacement.placementRelTo_placementRelTo.getZ();
		zActual += objectPlacement.placementRelTo_relativePlacement.getZ();
		zActual += objectPlacement.relativePlacement.location.getZ();
			
		Coordenada coord = new Coordenada(xActual, yActual, zActual);
		
		return coord;
		
	}
	

	public Coordenada aplicarObjectRepresentationParaRoof(Coordenada coordOriginal){
		
		coordOriginal = aplicarRotacionSegunRepresentation(coordOriginal);
		
		
		
		double xActual = coordOriginal.getX();
		
		xActual += representation.position.location.getX();
		
		double yActual = coordOriginal.getY();
		
		yActual += representation.position.location.getY();
		
		double zActual = coordOriginal.getZ();
		
		zActual += representation.position.location.getZ();
		
		Coordenada coord = new Coordenada(xActual, yActual, zActual);
		
		return coord;
		
	}

	public Coordenada aplicarObjectRepresentation(Coordenada coordOriginal){
		
		double xActual = coordOriginal.getX();
		
		Coordenada refDirection = representation.position.refDirection;
		
		if(refDirection!=null && refDirection.getX()!=0){
			xActual = xActual * refDirection.getX();	
		}
		xActual += representation.position.location.getX();
		
		double yActual = coordOriginal.getY();
		
		if(refDirection!=null && refDirection.getY()!=0){
			yActual = yActual * refDirection.getY();	
		}
		
		yActual += representation.position.location.getY();
		
		double zActual = coordOriginal.getZ();
		
		if(refDirection!=null && refDirection.getZ()!=0){
			zActual = zActual * refDirection.getZ();	
		}
		
		zActual += representation.position.location.getZ();
		
		Coordenada coord = new Coordenada(xActual, yActual, zActual);
		
		return coord;
		
	}
	
	
	public Coordenada aplicarRotacionSegunObjectPlacement(Coordenada coordOriginal){
		
		Coordenada r = new Coordenada();
		
		Coordenada axis = this.objectPlacement.relativePlacement.axis;
		Coordenada refDirection = this.objectPlacement.relativePlacement.refDirection;
		
		if(axis != null && refDirection != null){
			
			Vector3D axisX = new Vector3D(1, 0, 0);
			//Vector3D axisY = new Vector3D(0, 1, 0);
			Vector3D axisZ = new Vector3D(0, 0, 1);

			Vector3D deseadoX = new Vector3D(axis.getX(), axis.getY(), axis.getZ()); //(AXIS, eje Z)
			Vector3D deseadoZ = new Vector3D(refDirection.getX(), refDirection.getY(), refDirection.getZ()); //(refdirection, eje X)
			//Vector3D deseadoY = Vector3D.crossProduct(deseadoX, deseadoZ); // producto cruz
			
			Rotation rotacionX = new Rotation(axisX,deseadoZ);
			//Rotation rotacionY = new Rotation(axisY,deseadoY);
			Rotation rotacionZ = new Rotation(axisZ,deseadoX);
			
			
			Vector3D punto = new Vector3D(coordOriginal.getX(), coordOriginal.getY(), coordOriginal.getZ());
			Vector3D puntoRotado = rotacionX.applyTo(punto);
			//puntoRotado = rotacionY.applyTo(puntoRotado);
			puntoRotado = rotacionZ.applyTo(puntoRotado);
			
			r.setX(puntoRotado.getX());
			r.setY(puntoRotado.getY());
			r.setZ(puntoRotado.getZ());
		}else{
			r = coordOriginal;
		}
		

		return r;
	}
	
	public Coordenada aplicarRotacionSegunRepresentation(Coordenada coordOriginal){
		
		Coordenada r = new Coordenada();
		
		Coordenada axis = this.representation.position.axis;
		Coordenada refDirection = this.representation.position.refDirection;
		
		
		if(axis != null && refDirection != null){
			
			Vector3D axisX = new Vector3D(1, 0, 0);
			//Vector3D axisY = new Vector3D(0, 1, 0);
			Vector3D axisZ = new Vector3D(0, 0, 1);

			Vector3D deseadoX = new Vector3D(axis.getX(), axis.getY(), axis.getZ()); //(AXIS, eje Z)
			//Vector3D deseadoZ = new Vector3D(refDirection.getX(), refDirection.getY(), refDirection.getZ()); //(refdirection, eje X)
			//Vector3D deseadoY = Vector3D.crossProduct(deseadoX, deseadoZ); // producto cruz
			
			//Rotation rotacionX = new Rotation(axisX,deseadoZ);
			//Rotation rotacionY = new Rotation(axisY,deseadoY);
			Rotation rotacionZ = new Rotation(axisZ,deseadoX);
			
			
			Vector3D punto = new Vector3D(coordOriginal.getX(), coordOriginal.getY(), coordOriginal.getZ());
			//Vector3D puntoRotado = rotacionX.applyTo(punto);
			//puntoRotado = rotacionY.applyTo(puntoRotado);
			Vector3D puntoRotado = rotacionZ.applyTo(punto);
			
			r.setX(puntoRotado.getX());
			r.setY(puntoRotado.getY());
			r.setZ(puntoRotado.getZ());
		}else{
			r = coordOriginal;
		}
		

		return r;
	}
	
	
	public Polygon generarPoligono(double easting, double northing){
	    // create a factory using default values (e.g. floating precision)
	    GeometryFactory fact = new GeometryFactory();

	    Coordinate[] coordenadas = new Coordinate[coordenadasAbsolutas.size()];
	    int c=0;
	    	
	    for (Coordenada coordenadaActual : coordenadasAbsolutas){
	    	Coordinate coord = 
	    		new Coordinate(coordenadaActual.getX() + easting, coordenadaActual.getY() + northing, 0);
	    	coordenadas[c] = coord;
	    	c++;
	    }
	    
	     
	    LinearRing anillo = fact.createLinearRing(coordenadas);
	    
	    Polygon poligono = fact.createPolygon(anillo, null);
	    
	    return poligono;
	}

	@Override
	public void generarCaras() {
		
		//inicialmente se obtiene la instancia de la plancha en el modelo IFC
		IfcSlab planchaIfc = (IfcSlab) getIfcModel().getIfcObjectByID(getId());
		IfcRepresentation item = planchaIfc.getRepresentation().getRepresentations().get(0);
		
		//la profundidad de la extrusion, que va a afectar las coordenadas en Z
		Double profundidad = 0d;
		
		//System.err.println("Piso = " + getPisoPadre().getNombre());
		//System.err.println("Plancha = " + this.getId());

		if(item.getRepresentationType().toString().equals("SweptSolid")){
			IfcExtrudedAreaSolid repItem = (IfcExtrudedAreaSolid) item.getItems().iterator().next();
			profundidad = repItem.getDepth().value;
			
			
			// La extrusion debe hacerse en sentuido contrario en el eje Z si sucede esto

			if(
					(repItem.getExtrudedDirection().getDirectionRatios().get(2).value == -1)
					||
					(repItem.getPosition().getAxis()!=null && repItem.getPosition().getAxis().getDirectionRatios().get(2).value == -1)
					
			){
				profundidad = profundidad * -1;
			}
		}
		
				
		Poligono caraSuperior = new Poligono();
		
		for (Coordenada coordenadaActual : coordenadasAbsolutas) {
			
			Coordenada c = new Coordenada(coordenadaActual.getX(), coordenadaActual.getY(), coordenadaActual.getZ() );
			caraSuperior.getCoordenadas().add(c);
			
		}
		
		Poligono caraInferior = new Poligono();
		
		for (Coordenada coordenadaActual : coordenadasAbsolutas) {
			
			Coordenada c = new Coordenada(coordenadaActual.getX(), coordenadaActual.getY(), coordenadaActual.getZ() + profundidad);
			caraInferior.getCoordenadas().add(c);
			
		}
		
		List<Poligono> carasLaterales = new ArrayList();
		
		Integer puntos = caraSuperior.getCoordenadas().size();
		
		for(int i=0; i<puntos-1; i++){
			Poligono estaCara = new Poligono();
			
			estaCara.getCoordenadas().add(caraSuperior.getCoordenadas().get(i));
			estaCara.getCoordenadas().add(caraSuperior.getCoordenadas().get(i+1));
			
			estaCara.getCoordenadas().add(caraInferior.getCoordenadas().get(i+1));
			estaCara.getCoordenadas().add(caraInferior.getCoordenadas().get(i));
			
			estaCara.getCoordenadas().add(caraSuperior.getCoordenadas().get(i)); //se añade nuevamente la primera para cerrar
			
			carasLaterales.add(estaCara);
		}
		
		List<Poligono> todasLasCaras = new ArrayList();
		
		todasLasCaras.add(caraSuperior);
		todasLasCaras.addAll(carasLaterales);
		todasLasCaras.add(caraInferior);
		
		//se aplica la primera rotacion
				
		
		this.setCaras(todasLasCaras);
		
	}
	

}
