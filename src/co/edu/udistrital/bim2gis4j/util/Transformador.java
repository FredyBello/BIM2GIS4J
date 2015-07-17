package co.edu.udistrital.bim2gis4j.util;

import java.util.ArrayList;
import java.util.List;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcOpeningElement;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Muro;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Poligono;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Rectangulo;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Vacio;
import co.edu.udistrital.bim2gis4j.ifc.Solido;

public class Transformador {

	
	
	public Coordenada convertirEnAbsoluta(Coordenada coordOriginal, Solido pSolido){
		
		Coordenada coordAbsoluta = aplicarObjectRepresentation(coordOriginal, pSolido);
		coordAbsoluta = aplicarObjectPlacement(coordAbsoluta, pSolido);

		return coordAbsoluta;
	}
		
	public static Coordenada rotarCoordenadaVacio(Coordenada original, Vacio pVacio) {

		Coordenada locationMuro = pVacio.getMuroAlQueVacia().objectPlacement.getRelativePlacement_location();
		Coordenada axisMuro = pVacio.getMuroAlQueVacia().objectPlacement.getRelativePlacement_axis();
		Coordenada refDirectionMuro = pVacio.getMuroAlQueVacia().objectPlacement.getRelativePlacement_refDirection();
		
		//Coordenada axisVacio = pVacio.representation.position.axis;
		//Coordenada refDirectionVacio = pVacio.representation.position.refDirection;
		
		Vector3D vectorOrigen = Vector3D.ZERO;
		Vector3D diferenciaConOrigen = vectorOrigen.subtract(locationMuro.toVector3D());
		
		Vector3D coordenadaTrasladada = original.toVector3D().add(diferenciaConOrigen);
		Coordenada coordenadaRotada = rotarCoordenada(new Coordenada(coordenadaTrasladada), axisMuro, refDirectionMuro);
		Vector3D coordenadaRotadaReubicada = coordenadaRotada.toVector3D().subtract(diferenciaConOrigen);
		
		return new Coordenada(coordenadaRotadaReubicada);


	}

	private Coordenada aplicarObjectRepresentation(Coordenada coordOriginal, Solido pSolido){
		
		coordOriginal = rotarCoordenada(
				coordOriginal
				, pSolido.representation.position.axis
				, pSolido.representation.position.refDirection 
				);

		Coordenada location = pSolido.representation.position.location;
		
		double xActual = coordOriginal.getX();
		xActual += location.getX();
		
		double yActual = coordOriginal.getY();
		yActual += location.getY();
		
		double zActual = coordOriginal.getZ();
		zActual += location.getZ();
		
		Coordenada coord = new Coordenada(xActual, yActual, zActual);
		
		return coord;
		
	}
	
	private Coordenada aplicarObjectPlacement(Coordenada original, Solido pSolido){
		//Hay que rotar primero, o no funciona bien
		Coordenada conRotacion = rotarCoordenada(
				original
				, pSolido.objectPlacement.relativePlacement.axis
				, pSolido.objectPlacement.relativePlacement.refDirection
				);
		
		//si es un vacio se necesita aplicar la rotacion propia del vacio
		if(pSolido.objectPlacement.relativePlacement.axis == null && pSolido.getIfcModel().getIfcObjectByID(pSolido.getId()) instanceof IfcOpeningElement){
			
			Vacio vacio = (Vacio)pSolido;
			
			conRotacion = rotarCoordenada(
					original
					, vacio.representation.position.axis
					, vacio.representation.position.refDirection
					);
			
			conRotacion.setZ(conRotacion.getZ() + vacio.objectPlacement.relativePlacement.location.getZ());
		}
		
		double xActual = conRotacion.getX();
		
		
		xActual += pSolido.objectPlacement.placementRelTo_placementRelTo.getX();
		xActual += pSolido.objectPlacement.placementRelTo_relativePlacement.getX();
		xActual += pSolido.objectPlacement.relativePlacement.location.getX();
		
		double yActual = conRotacion.getY();
		
		
		yActual += pSolido.objectPlacement.placementRelTo_placementRelTo.getY();
		yActual += pSolido.objectPlacement.placementRelTo_relativePlacement.getY();
		yActual += pSolido.objectPlacement.relativePlacement.location.getY();
		
		double zActual = conRotacion.getZ();
		
		zActual += pSolido.objectPlacement.placementRelTo_placementRelTo.getZ();
		zActual += pSolido.objectPlacement.placementRelTo_relativePlacement.getZ();
		zActual += pSolido.objectPlacement.relativePlacement.location.getZ();
		
		
		Coordenada coord = new Coordenada(xActual, yActual, zActual);
		
		return coord;
		
	}
	
	public static Coordenada rotarCoordenada(Coordenada coordOriginal, Coordenada axis, Coordenada refDirection){
		
		Coordenada r = new Coordenada();
		
		if(axis != null && refDirection != null){
			
			Vector3D axisX = new Vector3D(1, 0, 0);
			Vector3D axisZ = new Vector3D(0, 0, 1);

			Vector3D deseadoX = new Vector3D(axis.getX(), axis.getY(), axis.getZ()); //(AXIS, eje Z)
			Vector3D deseadoZ = new Vector3D(refDirection.getX(), refDirection.getY(), refDirection.getZ()); //(refdirection, eje X)
			
			Rotation rotacionX = new Rotation(axisX,deseadoZ);
			Rotation rotacionZ = new Rotation(axisZ,deseadoX);
			
			
			Vector3D punto = new Vector3D(coordOriginal.getX(), coordOriginal.getY(), coordOriginal.getZ());
			Vector3D puntoRotado = rotacionX.applyTo(punto);
			puntoRotado = rotacionZ.applyTo(puntoRotado);
			
			r.setX(puntoRotado.getX());
			r.setY(puntoRotado.getY());
			r.setZ(puntoRotado.getZ());
		}else{
			r = coordOriginal;
		}
		

		return r;
	}
	
}
