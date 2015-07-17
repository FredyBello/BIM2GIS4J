
package co.edu.udistrital.bim2gis4j.bim2gisbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.ifc.PlanoDeCorte;
import co.edu.udistrital.bim2gis4j.util.LectorCoordenada;
import co.edu.udistrital.bim2gis4j.util.Transformador;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcHalfSpaceSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPlane;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class Piso {
	
	private String id;
	private String nombre;
	private double elevacion;
	
	private List<Plancha> planchas;
	private List<Muro> muros;

	

	public List<Muro> getMuros() {
		return muros;
	}

	public void setMuros(List<Muro> muros) {
		this.muros = muros;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getElevacion() {
		return elevacion;
	}

	public void setElevacion(double elevacion) {
		this.elevacion = elevacion;
	}

	public List<Plancha> getPlanchas() {
		return planchas;
	}

	public void setPlanchas(List<Plancha> planchas) {
		this.planchas = planchas;
	}

	
	public Piso(){
		planchas = new ArrayList();
		muros = new ArrayList();
	}
	
	public void imprimir(){
		String cadena = "";
		cadena += "\nPiso \"" + getNombre() + "\"  ( id=" + getId() + " , elevacion=" + getElevacion() + " )";
		
		cadena += "\n  " + getPlanchas().size() + " Planchas:";
		for (Plancha planchaActual : getPlanchas()) {
			cadena += "\n  |__ Plancha";
			cadena += "\n      id = " + planchaActual.getId();
			cadena += "\n      tipo = " + planchaActual.getTipo();
			cadena += "\n      placementRelTo";
			cadena += "\n          placementRelTo = [ ";
			
			cadena += planchaActual.objectPlacement.getPlacementRelTo_placementRelTo().getX() + " ";
			cadena += planchaActual.objectPlacement.getPlacementRelTo_placementRelTo().getY() + " ";
			cadena += planchaActual.objectPlacement.getPlacementRelTo_placementRelTo().getZ() + " ";
			cadena += "]";
			
			cadena += "\n          relativePlacement = [ ";
			cadena += planchaActual.objectPlacement.getPlacementRelTo_relativePlacement().getX() + " ";
			cadena += planchaActual.objectPlacement.getPlacementRelTo_relativePlacement().getY() + " ";
			cadena += planchaActual.objectPlacement.getPlacementRelTo_relativePlacement().getZ() + " ";
			cadena += "]";
			
			cadena += "\n      relativePlacement";
			cadena += "\n          location = [ ";
			cadena += planchaActual.objectPlacement.getRelativePlacement_location().getX() + " ";
			cadena += planchaActual.objectPlacement.getRelativePlacement_location().getY() + " ";
			cadena += planchaActual.objectPlacement.getRelativePlacement_location().getZ() + " ";
			cadena += "]";
			
			cadena += "\n          Axis = [ ";
			cadena += (( planchaActual.objectPlacement.getRelativePlacement_axis() !=null) ? planchaActual.objectPlacement.getRelativePlacement_axis().getX() : "null") + " ";
			cadena += (( planchaActual.objectPlacement.getRelativePlacement_axis() !=null) ? planchaActual.objectPlacement.getRelativePlacement_axis().getY() : "null") + " ";
			cadena += (( planchaActual.objectPlacement.getRelativePlacement_axis() !=null) ? planchaActual.objectPlacement.getRelativePlacement_axis().getZ() : "null") + " ";
			cadena += "]";
			
			cadena += "\n          RefDirection = [ ";
			cadena += (( planchaActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? planchaActual.objectPlacement.getRelativePlacement_refDirection().getX() : "null") + " ";
			cadena += (( planchaActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? planchaActual.objectPlacement.getRelativePlacement_refDirection().getY() : "null") + " ";
			cadena += (( planchaActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? planchaActual.objectPlacement.getRelativePlacement_refDirection().getZ() : "null") + " ";
			cadena += "]";
			
			
			cadena += "\n      Representation";
			cadena += "\n          RepresentationType = " + planchaActual.representation.getRepresentation_representationType();
			cadena += "\n          SweptAreaType = " + planchaActual.representation.getRepresentation_representation_SweptAreaType();
			cadena += "\n          Position ";
			cadena += "\n              Location = [ ";
			cadena += planchaActual.representation.getRepresentation_position_location().getX() + " ";
			cadena += planchaActual.representation.getRepresentation_position_location().getY() + " ";
			cadena += planchaActual.representation.getRepresentation_position_location().getZ() + " ";
			cadena += "]";

			

			cadena += "\n              Axis = [ ";
			cadena += (( planchaActual.representation.getRepresentation_position_axis()!=null) ? planchaActual.representation.getRepresentation_position_axis().getX() : "null") + " ";
			cadena += (( planchaActual.representation.getRepresentation_position_axis()!=null) ? planchaActual.representation.getRepresentation_position_axis().getY() : "null") + " ";
			cadena += (( planchaActual.representation.getRepresentation_position_axis()!=null) ? planchaActual.representation.getRepresentation_position_axis().getZ() : "null") + " ";
			cadena += "]";
			
			cadena += "\n              RefDirection = [ ";
			cadena += (( planchaActual.representation.getRepresentation_position_refDirection()!=null) ? planchaActual.representation.getRepresentation_position_refDirection().getX() : "null") + " ";
			cadena += (( planchaActual.representation.getRepresentation_position_refDirection()!=null) ? planchaActual.representation.getRepresentation_position_refDirection().getY() : "null") + " ";
			cadena += (( planchaActual.representation.getRepresentation_position_refDirection()!=null) ? planchaActual.representation.getRepresentation_position_refDirection().getZ() : "null") + " ";
			cadena += "]";
			
			
			cadena += "\n          ExtrudedDirection = [ ";
			cadena += planchaActual.representation.getRepresentation_extruded_direction().getX() + " ";
			cadena += planchaActual.representation.getRepresentation_extruded_direction().getY() + " ";
			cadena += planchaActual.representation.getRepresentation_extruded_direction().getZ() + " ";
			cadena += "]";
			
			if(planchaActual.getRepresentation_points()!=null){
				cadena += "\n          Puntos (" + planchaActual.getRepresentation_points().size() + ") = [ ";
				for (Coordenada coordenadaActual : planchaActual.getRepresentation_points()) {
					cadena += coordenadaActual.getX() + " " + coordenadaActual.getY() + " , ";
				}
				cadena += "]";
			}
			
			if(planchaActual.getRepresentation_segmentos()!=null){
				cadena += "\n          Segmentos (" + planchaActual.getRepresentation_segmentos().size() + ") = [ ";
				for (Segmento segmentoActual : planchaActual.getRepresentation_segmentos()) {
					cadena += segmentoActual.getP0().getX() + " " + segmentoActual.getP0().getY() + " , ";
					cadena += segmentoActual.getP1().getX() + " " + segmentoActual.getP1().getY() + " , ";
				}
				cadena += "]";
			}
			
			if(planchaActual.getRectangulo()!=null){
				Rectangulo rec = planchaActual.getRectangulo(); 
				cadena += "\n          Rectangulo";
				cadena += "\n              Location = [ " + rec.getPosition_location().getX() + " " + rec.getPosition_location().getY() + " ]";
				cadena += "\n              RefDirection = [ " + rec.getPosition_refDirection().getX() + " " + rec.getPosition_refDirection().getY() + " ]";
				cadena += "\n              XDim = " + rec.getXDim();
				cadena += "\n              YDim = " + rec.getYDim();
			}
			
			if(planchaActual.getCoordenadasAbsolutas()!=null){
				cadena += "\n          Absolutas (" + planchaActual.getCoordenadasAbsolutas().size() + ") = [ ";
				for (Coordenada coordenadaActual : planchaActual.getCoordenadasAbsolutas()) {
					cadena += coordenadaActual.getX() + " " + coordenadaActual.getY() + " " + coordenadaActual.getZ() + " , ";
				}
				cadena += "]";
			}
			
			cadena += "\n          Piso Padre";
			cadena += "\n              Id = " + planchaActual.getPisoPadre().getId();
			cadena += "\n              Nombre = " + planchaActual.getPisoPadre().getNombre();
			cadena += "\n              Elevacion = " + planchaActual.getPisoPadre().getElevacion();
			
			if(planchaActual.getCaras()!=null){
				
				Iterator i = planchaActual.getCaras().iterator();
				cadena += "\n          Caras GEOGEBRA 5 3D (" + planchaActual.getCaras().size() + ") = \n";
				
				for (Poligono caraActual : planchaActual.getCaras()) {
					cadena += "                                  " + caraActual + "\n";
				}
				
			}

		}
		
		
		cadena += "\n  " + getMuros().size() + " Muros:";
		for (Muro muroActual : getMuros()) {
			cadena += "\n  |__ Muro";
			cadena += "\n      id = " + muroActual.getId();
			cadena += "\n      tipo = " + muroActual.getTipo();
			cadena += "\n      placementRelTo";
			cadena += "\n          placementRelTo = [ ";
			
			cadena += muroActual.objectPlacement.getPlacementRelTo_placementRelTo().getX() + " ";
			cadena += muroActual.objectPlacement.getPlacementRelTo_placementRelTo().getY() + " ";
			cadena += muroActual.objectPlacement.getPlacementRelTo_placementRelTo().getZ() + " ";
			cadena += "]";
			
			cadena += "\n          relativePlacement = [ ";
			cadena += muroActual.objectPlacement.getPlacementRelTo_relativePlacement().getX() + " ";
			cadena += muroActual.objectPlacement.getPlacementRelTo_relativePlacement().getY() + " ";
			cadena += muroActual.objectPlacement.getPlacementRelTo_relativePlacement().getZ() + " ";
			cadena += "]";
			
			cadena += "\n      relativePlacement";
			cadena += "\n          location = [ ";
			cadena += muroActual.objectPlacement.getRelativePlacement_location().getX() + " ";
			cadena += muroActual.objectPlacement.getRelativePlacement_location().getY() + " ";
			cadena += muroActual.objectPlacement.getRelativePlacement_location().getZ() + " ";
			cadena += "]";
			
			cadena += "\n          Axis = [ ";
			cadena += (( muroActual.objectPlacement.getRelativePlacement_axis() !=null) ? muroActual.objectPlacement.getRelativePlacement_axis().getX() : "null") + " ";
			cadena += (( muroActual.objectPlacement.getRelativePlacement_axis() !=null) ? muroActual.objectPlacement.getRelativePlacement_axis().getY() : "null") + " ";
			cadena += (( muroActual.objectPlacement.getRelativePlacement_axis() !=null) ? muroActual.objectPlacement.getRelativePlacement_axis().getZ() : "null") + " ";
			cadena += "]";
			
			cadena += "\n          RefDirection = [ ";
			cadena += (( muroActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? muroActual.objectPlacement.getRelativePlacement_refDirection().getX() : "null") + " ";
			cadena += (( muroActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? muroActual.objectPlacement.getRelativePlacement_refDirection().getY() : "null") + " ";
			cadena += (( muroActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? muroActual.objectPlacement.getRelativePlacement_refDirection().getZ() : "null") + " ";
			cadena += "]";
			
			cadena += "\n      Representation";
			cadena += "\n          STEP Line number = " + muroActual.representation.getStepLineNumber();
			cadena += "\n          RepresentationType = " + muroActual.representation.getRepresentation_representationType();
			cadena += "\n          SweptAreaType = " + muroActual.representation.getRepresentation_representation_SweptAreaType();
			cadena += "\n          Position ";
			cadena += "\n              Location = [ ";
			cadena += muroActual.representation.getRepresentation_position_location().getX() + " ";
			cadena += muroActual.representation.getRepresentation_position_location().getY() + " ";
			cadena += muroActual.representation.getRepresentation_position_location().getZ() + " ";
			cadena += "]";
			
			cadena += "\n              Axis = [ ";
			cadena += (( muroActual.representation.getRepresentation_position_axis()!=null) ? muroActual.representation.getRepresentation_position_axis().getX() : "null") + " ";
			cadena += (( muroActual.representation.getRepresentation_position_axis()!=null) ? muroActual.representation.getRepresentation_position_axis().getY() : "null") + " ";
			cadena += (( muroActual.representation.getRepresentation_position_axis()!=null) ? muroActual.representation.getRepresentation_position_axis().getZ() : "null") + " ";
			cadena += "]";
			
			cadena += "\n              RefDirection = [ ";
			cadena += (( muroActual.representation.getRepresentation_position_refDirection()!=null) ? muroActual.representation.getRepresentation_position_refDirection().getX() : "null") + " ";
			cadena += (( muroActual.representation.getRepresentation_position_refDirection()!=null) ? muroActual.representation.getRepresentation_position_refDirection().getY() : "null") + " ";
			cadena += (( muroActual.representation.getRepresentation_position_refDirection()!=null) ? muroActual.representation.getRepresentation_position_refDirection().getZ() : "null") + " ";
			cadena += "]";
			
			
			cadena += "\n          ExtrudedDirection = [ ";
			cadena += muroActual.representation.getRepresentation_extruded_direction().getX() + " ";
			cadena += muroActual.representation.getRepresentation_extruded_direction().getY() + " ";
			cadena += muroActual.representation.getRepresentation_extruded_direction().getZ() + " ";
			cadena += "]";

			
			if(muroActual.getRepresentation_points()!=null){
				cadena += "\n          Puntos (" + muroActual.getRepresentation_points().size() + ") = [ ";
				for (Coordenada coordenadaActual : muroActual.getRepresentation_points()) {
					cadena += coordenadaActual.getX() + " " + coordenadaActual.getY() + " , ";
				}
				cadena += "]";
			}
			
			if(muroActual.getRepresentation_segmentos()!=null){
				cadena += "\n          Segmentos (" + muroActual.getRepresentation_segmentos().size() + ") = [ ";
				for (Segmento segmentoActual : muroActual.getRepresentation_segmentos()) {
					cadena += segmentoActual.getP0().getX() + " " + segmentoActual.getP0().getY() + " , ";
					cadena += segmentoActual.getP1().getX() + " " + segmentoActual.getP1().getY() + " , ";
				}
				cadena += "]";
			}
			
			if(muroActual.getRectangulo()!=null){
				Rectangulo rec = muroActual.getRectangulo(); 
				cadena += "\n          Rectangulo";
				cadena += "\n              Location = [ " + rec.getPosition_location().getX() + " " + rec.getPosition_location().getY() + " ]";
				cadena += "\n              RefDirection = [ " + rec.getPosition_refDirection().getX() + " " + rec.getPosition_refDirection().getY() + " ]";
				cadena += "\n              XDim = " + rec.getXDim();
				cadena += "\n              YDim = " + rec.getYDim();
			}
			
			if(muroActual.getCoordenadasAbsolutas()!=null){
				cadena += "\n          Absolutas (" + muroActual.getCoordenadasAbsolutas().size() + ") = [ ";
				for (Coordenada coordenadaActual : muroActual.getCoordenadasAbsolutas()) {
					cadena += coordenadaActual + " ";
				}
				cadena += "]";
				
			}
			
			if(muroActual.getCaras()!=null){
				
				Iterator i = muroActual.getCaras().iterator();
				cadena += "\n          Caras GEOGEBRA 5 3D (" + muroActual.getCaras().size() + ") = ";
				
				for (Poligono caraActual : muroActual.getCaras()) {
					cadena +=  "\n                                " + caraActual;
				}
				
			}
			
			
			if(muroActual.getPlanosDeCorte()!=null){
				cadena += "\n          Planos de corte (" + muroActual.getPlanosDeCorte().size() + ") = ";
				int c = 0;
				for (PlanoDeCorte planoActual : muroActual.getPlanosDeCorte()) {
					
					c++;
					cadena += "\n          |__ Plano " + c;
					
					cadena += "\n              Step Line = " + planoActual.getPlanoIfc().getStepLineNumber();
					cadena += "\n              Ifc : origen = " + planoActual.getLocationAbsolutaIfc() + " normal = " + planoActual.getNormalAbsolutaIfc();
					
					Vector3D normal = planoActual.getPlanoApache().getNormal();
					Coordenada normalApache = new Coordenada (normal.getX(), normal.getY(), normal.getZ());
					Vector3D origen = planoActual.getPlanoApache().getOrigin();
					Coordenada origenApache = new Coordenada (origen.getX(), origen.getY(), origen.getZ());
					
					cadena +=  "\n              Apache : origen = " + origenApache + " normal = " + normalApache;
					
					cadena += "\n              agreementFlagIfc = " + planoActual.getAgreementFlagIfc();
					
					cadena +=  "\n              Caras A Cortar = ";
					
					for (Poligono caraActual : planoActual.getCarasACortar() ) {
						
						cadena += " " + caraActual;
						
					}
					
					cadena +=  "\n              Caras Resultado = ";

					for (Poligono caraActual : planoActual.getCarasResultado() ) {
						
						cadena += " " + caraActual;
						
					}
					
					cadena += " cara de corte = " + planoActual.getCaraDeCorte();
				}
				//cadena += "]";
				
				
				
			}
			
			if(muroActual.getVacios()!=null){
				
				Iterator i = muroActual.getCaras().iterator();
				
				cadena += "\n      " + muroActual.getVacios().size() + " Vacios:";
				for (Vacio vacioActual : muroActual.getVacios()) {


					cadena += "\n      |__ Vacio";
					cadena += "\n          id = " + vacioActual.getId();
					cadena += "\n          tipo = " + vacioActual.getTipo();
					cadena += "\n          placementRelTo";
					cadena += "\n              placementRelTo = [ ";
					
					cadena += vacioActual.objectPlacement.getPlacementRelTo_placementRelTo().getX() + " ";
					cadena += vacioActual.objectPlacement.getPlacementRelTo_placementRelTo().getY() + " ";
					cadena += vacioActual.objectPlacement.getPlacementRelTo_placementRelTo().getZ() + " ";
					cadena += "]";
					
					cadena += "\n              relativePlacement = [ ";
					cadena += vacioActual.objectPlacement.getPlacementRelTo_relativePlacement().getX() + " ";
					cadena += vacioActual.objectPlacement.getPlacementRelTo_relativePlacement().getY() + " ";
					cadena += vacioActual.objectPlacement.getPlacementRelTo_relativePlacement().getZ() + " ";
					cadena += "]";
					
					cadena += "\n          relativePlacement";
					cadena += "\n              location = [ ";
					cadena += vacioActual.objectPlacement.getRelativePlacement_location().getX() + " ";
					cadena += vacioActual.objectPlacement.getRelativePlacement_location().getY() + " ";
					cadena += vacioActual.objectPlacement.getRelativePlacement_location().getZ() + " ";
					cadena += "]";
					
					cadena += "\n              Axis = [ ";
					cadena += (( vacioActual.objectPlacement.getRelativePlacement_axis() !=null) ? vacioActual.objectPlacement.getRelativePlacement_axis().getX() : "null") + " ";
					cadena += (( vacioActual.objectPlacement.getRelativePlacement_axis() !=null) ? vacioActual.objectPlacement.getRelativePlacement_axis().getY() : "null") + " ";
					cadena += (( vacioActual.objectPlacement.getRelativePlacement_axis() !=null) ? vacioActual.objectPlacement.getRelativePlacement_axis().getZ() : "null") + " ";
					cadena += "]";
					
					cadena += "\n              RefDirection = [ ";
					cadena += (( vacioActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? vacioActual.objectPlacement.getRelativePlacement_refDirection().getX() : "null") + " ";
					cadena += (( vacioActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? vacioActual.objectPlacement.getRelativePlacement_refDirection().getY() : "null") + " ";
					cadena += (( vacioActual.objectPlacement.getRelativePlacement_refDirection() !=null) ? vacioActual.objectPlacement.getRelativePlacement_refDirection().getZ() : "null") + " ";
					cadena += "]";
					
					cadena += "\n          Representation";
					cadena += "\n              STEP Line number = " + vacioActual.representation.getStepLineNumber();
					cadena += "\n              RepresentationType = " + vacioActual.representation.getRepresentation_representationType();
					cadena += "\n              SweptAreaType = " + vacioActual.representation.getRepresentation_representation_SweptAreaType();
					cadena += "\n              Position ";
					cadena += "\n                  Location = [ ";
					cadena += vacioActual.representation.getRepresentation_position_location().getX() + " ";
					cadena += vacioActual.representation.getRepresentation_position_location().getY() + " ";
					cadena += vacioActual.representation.getRepresentation_position_location().getZ() + " ";
					cadena += "]";
					
					cadena += "\n                  Axis = [ ";
					cadena += (( vacioActual.representation.getRepresentation_position_axis()!=null) ? vacioActual.representation.getRepresentation_position_axis().getX() : "null") + " ";
					cadena += (( vacioActual.representation.getRepresentation_position_axis()!=null) ? vacioActual.representation.getRepresentation_position_axis().getY() : "null") + " ";
					cadena += (( vacioActual.representation.getRepresentation_position_axis()!=null) ? vacioActual.representation.getRepresentation_position_axis().getZ() : "null") + " ";
					cadena += "]";
					
					cadena += "\n                  RefDirection = [ ";
					cadena += (( vacioActual.representation.getRepresentation_position_refDirection()!=null) ? vacioActual.representation.getRepresentation_position_refDirection().getX() : "null") + " ";
					cadena += (( vacioActual.representation.getRepresentation_position_refDirection()!=null) ? vacioActual.representation.getRepresentation_position_refDirection().getY() : "null") + " ";
					cadena += (( vacioActual.representation.getRepresentation_position_refDirection()!=null) ? vacioActual.representation.getRepresentation_position_refDirection().getZ() : "null") + " ";
					cadena += "]";
					
					
					cadena += "\n              ExtrudedDirection = [ ";
					cadena += vacioActual.representation.getRepresentation_extruded_direction().getX() + " ";
					cadena += vacioActual.representation.getRepresentation_extruded_direction().getY() + " ";
					cadena += vacioActual.representation.getRepresentation_extruded_direction().getZ() + " ";
					cadena += "]";

					
					if(vacioActual.getRepresentation_points()!=null){
						cadena += "\n              Puntos (" + vacioActual.getRepresentation_points().size() + ") = [ ";
						for (Coordenada coordenadaActual : vacioActual.getRepresentation_points()) {
							cadena += coordenadaActual.getX() + " " + coordenadaActual.getY() + " , ";
						}
						cadena += "]";
					}
					
					if(vacioActual.getRepresentation_segmentos()!=null){
						cadena += "\n              Segmentos (" + vacioActual.getRepresentation_segmentos().size() + ") = [ ";
						for (Segmento segmentoActual : vacioActual.getRepresentation_segmentos()) {
							cadena += segmentoActual.getP0().getX() + " " + segmentoActual.getP0().getY() + " , ";
							cadena += segmentoActual.getP1().getX() + " " + segmentoActual.getP1().getY() + " , ";
						}
						cadena += "]";
					}
					
					if(vacioActual.getRectangulo()!=null){
						Rectangulo rec = vacioActual.getRectangulo(); 
						cadena += "\n              Rectangulo";
						cadena += "\n                  Location = [ " + rec.getPosition_location().getX() + " " + rec.getPosition_location().getY() + " ]";
						cadena += "\n                  RefDirection = [ " + rec.getPosition_refDirection().getX() + " " + rec.getPosition_refDirection().getY() + " ]";
						cadena += "\n                  XDim = " + rec.getXDim();
						cadena += "\n                  YDim = " + rec.getYDim();
					}
					
					if(vacioActual.getCoordenadasAbsolutas()!=null){
						cadena += "\n              Absolutas (" + vacioActual.getCoordenadasAbsolutas().size() + ") =";
						for (Coordenada coordenadaActual : vacioActual.getCoordenadasAbsolutas()) {
							cadena += "\n                         " + coordenadaActual;
						}
						
					}
					
					if(vacioActual.getCaras()!=null){
						
						//Iterator i2 = vacioActual.getCaras().iterator();
						cadena += "\n              Caras GEOGEBRA 5 3D (" + vacioActual.getCaras().size() + ") = ";
						
						for (Poligono caraActual : vacioActual.getCaras()) {
							
							if(caraActual.getInterno()){
								cadena += "\n                                I " + caraActual ;
							}else{
								cadena += "\n                                " + caraActual ;
							}
							
						}
						
					}

					
				}
				
				
			}
			

			/*

			if(planchaActual.getCoordenadasAbsolutas()!=null){
				cadena += "\n          Absolutas (" + planchaActual.getCoordenadasAbsolutas().size() + ") = [ ";
				for (Coordenada coordenadaActual : planchaActual.getCoordenadasAbsolutas()) {
					cadena += coordenadaActual.getX() + " " + coordenadaActual.getY() + " " + coordenadaActual.getZ() + " , ";
				}
				cadena += "]";
			}
			
			cadena += "\n          Piso Padre";
			cadena += "\n              Id = " + planchaActual.getPisoPadre().getId();
			cadena += "\n              Nombre = " + planchaActual.getPisoPadre().getNombre();
			cadena += "\n              Elevacion = " + planchaActual.getPisoPadre().getElevacion();

		}
 
 
 */					
		}
		

		
		System.out.println(cadena);
	}
	
	public Polygon[] generarPoligonos(double easting, double northing){
		
		int planchasTipoFloor = 0;
		for (Plancha planchaActual : getPlanchas()){
			if(planchaActual.getTipo().equals("FLOOR") || planchaActual.getTipo().equals("BASESLAB")){
				planchasTipoFloor++;
			}
		}
		
		
		Polygon[] poligonos = new Polygon[planchasTipoFloor];
		int c=0;
		for (Plancha planchaActual : getPlanchas()){
			//solo se toman en cuenta las planchas que sea de tipo "FLOOR,BASESLAB", se descartan "ROOF,LANDING,USERDEFINED,NOTDEFINED"
			if(planchaActual.getTipo().equals("FLOOR") || planchaActual.getTipo().equals("BASESLAB")){
				
				Polygon poligonoActual = planchaActual.generarPoligono(easting, northing);
				poligonos[c] = poligonoActual;
				c++;
				
			}
			
		}
		
		return poligonos;
	}

}
