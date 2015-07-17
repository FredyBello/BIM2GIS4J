
package co.edu.udistrital.bim2gis4j.ifc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.DOUBLE;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcArbitraryClosedProfileDef;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcAxis2Placement2D;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanClippingResult;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanOperand;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCartesianPoint;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcClass;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCompositeCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCompositeCurveSegment;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcDirection;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcHalfSpaceSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcLengthMeasure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcOpeningElement;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPlane;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPolyline;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPositiveLengthMeasure;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRectangleProfileDef;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentation;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcTrimmedCurve;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcWallStandardCase;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.LIST;
import openifctools.com.openifcjavatoolbox.ifcmodel.IfcModel;

import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet;
import org.apache.commons.math3.geometry.euclidean.threed.SubLine;
import org.apache.commons.math3.geometry.euclidean.threed.SubPlane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Piso;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Poligono;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Rectangulo;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Segmento;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Vacio;
import co.edu.udistrital.bim2gis4j.util.LectorCoordenada;
import co.edu.udistrital.bim2gis4j.util.Transformador;

public class Solido {
	
	private IfcModel ifcModel;
	
	private Piso pisoPadre;

	private String id;
	
	private String tipo;
	
	public Transformador transformador = new Transformador();
	
	
	
	public Placement objectPlacement = new Placement();
	
	public Representation representation = new Representation();
	
	//el listado de caras se inicializa, aunque puede que permanezca siempre vacío
	private List<Poligono> caras = new ArrayList();

	
	// estos 3 atributos son mutuamente excluyentes
	
	protected List<Coordenada> representation_points;
	
	protected List<Segmento> representation_segmentos;
	
	protected Rectangulo rectangulo;
	
	//solo se usa para imprimir y probar datos
	public List<PlanoDeCorte> planosDeCorte;
	
	
	//este atributo contiene las coordenadas absolutas del perfil de la plancha
	//sin importar si se deriva de representation_points, representation_segmentos o rectangulo
	
	public List<PlanoDeCorte> getPlanosDeCorte() {
		return planosDeCorte;
	}

	public void setPlanosDeCorte(List<PlanoDeCorte> planosDeCorte) {
		this.planosDeCorte = planosDeCorte;
	}

	protected List<Coordenada> coordenadasAbsolutas;
	

	
	
	public List<Coordenada> getCoordenadasAbsolutas() {
		return coordenadasAbsolutas;
	}

	public void setCoordenadasAbsolutas(List<Coordenada> coordenadasAbsolutas) {
		this.coordenadasAbsolutas = coordenadasAbsolutas;
	}
	
	
	public Rectangulo getRectangulo() {
		return rectangulo;
	}

	public void setRectangulo(Rectangulo rectangulo) {
		this.rectangulo = rectangulo;
	}

	public List<Segmento> getRepresentation_segmentos() {
		return representation_segmentos;
	}

	public void setRepresentation_segmentos(List<Segmento> representation_segmentos) {
		this.representation_segmentos = representation_segmentos;
	}

	public List<Coordenada> getRepresentation_points() {
		return representation_points;
	}

	public void setRepresentation_points(List<Coordenada> representation_points) {
		this.representation_points = representation_points;
	}

	

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public IfcModel getIfcModel() {
		return ifcModel;
	}

	public void setIfcModel(IfcModel ifcModel) {
		this.ifcModel = ifcModel;
	}
	
	public Piso getPisoPadre() {
		return pisoPadre;
	}

	public void setPisoPadre(Piso pisoPadre) {
		this.pisoPadre = pisoPadre;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public List<Poligono> getCaras() {
		return caras;
	}

	public void setCaras(List<Poligono> caras) {
		this.caras = caras;
	}
	
	public void calcularCoordenadasAbsolutas(){
		
		if(representation_points!=null){
			//coordenadasAbsolutas.addAll(getRepresentation_points());
			
			coordenadasAbsolutas = new ArrayList();
			
			for (Coordenada coordenadaActual : representation_points) {
				
				Coordenada coordAbsoluta = transformador.convertirEnAbsoluta(coordenadaActual, this);
				coordenadasAbsolutas.add(coordAbsoluta);
				
			}
		}else if(representation_segmentos!=null){

			coordenadasAbsolutas = new ArrayList();
			
			int contador = 0;
			
			for (Segmento segmentoActual : representation_segmentos) {
				
				Coordenada primeraCoordenadaDelPoligono = new Coordenada();
				Coordenada coordenadaActual;
				
				coordenadaActual = segmentoActual.getP0();
				
				Coordenada coordAbsoluta = transformador.convertirEnAbsoluta(coordenadaActual, this);
				coordenadasAbsolutas.add(coordAbsoluta);
				
				contador++;
				
				if(contador==representation_segmentos.size()){
					coordenadasAbsolutas.add(coordenadasAbsolutas.get(0));
				}
			}
			
			
		}else if(rectangulo!=null){
			
			//TODO: SUMAR VALORES QUE FALTAN A LAS COORDENADAS DEL RECTANGULO
			List<Coordenada> cuatroEsquinas = new ArrayList();
			
			double ancho = 0;
			double alto = 0;
			
			//eje X relativo igual a ejex X real
			//if(rectangulo.getPosition_refDirection().getX()!=0){
				ancho = rectangulo.getXDim();
				alto = rectangulo.getYDim();
			//}
			
			//eje X relativo igual a eje Y real
			//if(rectangulo.getPosition_refDirection().getY()!=0){
				//ancho = rectangulo.getYDim();
				//alto = rectangulo.getXDim();
			//}
			
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
			
			Coordenada locationRectangulo = rectangulo.getPosition_location();
			//Coordenada refDirectionRectangulo = rectangulo.getPosition_refDirection();
			
			int c = 0;
			Coordenada primera = null;
			for (Coordenada coordenadaActual : cuatroEsquinas) {
				
				Coordenada coordAbsoluta = coordenadaActual;
				
				if(locationRectangulo != null /*&& refDirectionRectangulo != null*/){
					
					double X = locationRectangulo.getX();
					double Y = locationRectangulo.getY();
					
					coordAbsoluta.setX(coordAbsoluta.getX() + X);
					coordAbsoluta.setY(coordAbsoluta.getY() + Y);
					
				}
				
				
				/*Coordenada*/ coordAbsoluta = transformador.convertirEnAbsoluta(coordenadaActual, this);
				
				//coordAbsoluta = transformador.aplicarPositionLocationRectangulo(coordAbsoluta, rectangulo);
				
				
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
	
	
	
	public void generarCaras() {
		
		//inicialmente se obtiene la instancia del solido
		
		//System.err.println("BUSCANDO OBJETO + " + getId());
		
		IfcClass objetoIfc = getIfcModel().getIfcObjectByID(getId());
		
		Iterator<IfcRepresentation> it = null;
		
		if(getIfcModel().getIfcObjectByID(getId()) instanceof IfcWallStandardCase){
			
			it = ((IfcWallStandardCase)objetoIfc).getRepresentation().getRepresentations().iterator();
			
		}
		
		if(getIfcModel().getIfcObjectByID(getId()) instanceof IfcOpeningElement){
			
			it = ((IfcOpeningElement)objetoIfc).getRepresentation().getRepresentations().iterator();
			
		}
		
		
		IfcRepresentation item = null;
		
		//System.err.println("Muro = " + this.getId());
		while(it.hasNext()){
			IfcRepresentation representationActual = it.next();
			if(this.representation.getStepLineNumber() == representationActual.getStepLineNumber() ){
				
				item = representationActual;
				//System.err.println("COINCIDEN            = " + this.representation.getStepLineNumber() + " existente = " + representationActual.getStepLineNumber());
				break;
			}else{
				//System.err.println("No coinciden buscado = " + this.representation.getStepLineNumber() + " existente = " + representationActual.getStepLineNumber());
			}
		}
		
		
		
		//la profundidad de la extrusion, que va a afectar las coordenadas en Z
		Double profundidad = 0d;
		
		//System.err.println("Piso = " + getPisoPadre().getNombre());
		//System.err.println("Plancha = " + this.getId());
		
		if(this.representation.getStepLineNumber() > 0){
			
			if(item.getRepresentationType().toString().equals("SweptSolid")){
				IfcExtrudedAreaSolid repItem = (IfcExtrudedAreaSolid) item.getItems().iterator().next();
				profundidad = repItem.getDepth().value;
			}
			
			if(item.getRepresentationType().toString().equals("Clipping")){
				
				
				IfcBooleanOperand primerOperando = ((IfcBooleanClippingResult)item.getItems().iterator().next()).getFirstOperand();
				
				if(primerOperando instanceof IfcExtrudedAreaSolid){
					
					IfcExtrudedAreaSolid repItem = (IfcExtrudedAreaSolid) primerOperando;
					profundidad = repItem.getDepth().value;
					
				}else if(primerOperando instanceof IfcBooleanClippingResult){
					
					IfcExtrudedAreaSolid repItem = (IfcExtrudedAreaSolid) ((IfcBooleanClippingResult)primerOperando).getFirstOperand();
					profundidad = repItem.getDepth().value;
					
				}
				
				
			}
			
			//si por alguna razon el solido aun no tiene sus coordenadas calculadas se cancela el prodecimiento
			//por ejemplo los vacios cuya forma es circular no se estan calculando
			if(coordenadasAbsolutas == null) return;
			
			Poligono caraSuperior = new Poligono();
			//caraSuperior.setTipo("superior");
			
			for (Coordenada coordenadaActual : coordenadasAbsolutas) {
				
				Coordenada c = new Coordenada(coordenadaActual.getX(), coordenadaActual.getY(), coordenadaActual.getZ() );
				caraSuperior.getCoordenadas().add(c);
				
			}
			
			Poligono caraInferior = new Poligono();
			//caraInferior.setTipo("inferior");
			
			//es necesario evaluar en que eje se agrega la profundidad
			Double profundidadX = 0d;
			Double profundidadY = 0d;
			Double profundidadZ = 0d;
			
			//a quien le agrego la profundidad
			boolean aX = false;
			boolean aY = false;
			boolean aZ = false;
			
			if(this.representation.position.axis!=null){
				aX = this.representation.position.axis.getX() != 0;
				aY = this.representation.position.axis.getY() != 0;
				aZ = this.representation.position.axis.getZ() != 0;	
			}else{
				//por defecto se le agrega a Z
				aZ = true;
			}
			
			if(aX){
				profundidadX = profundidad;
			}else if(aY){
				profundidadY = profundidad;
			}else if(aZ){
				profundidadZ = profundidad;
			}
			
			
			for (Coordenada coordenadaActual : coordenadasAbsolutas) {
				
				Coordenada c = new Coordenada(coordenadaActual.getX() + profundidadX, coordenadaActual.getY() + profundidadY, coordenadaActual.getZ() + profundidadZ);
				caraInferior.getCoordenadas().add(c);
				
			}
			
			List<Poligono> carasLaterales = new ArrayList();
			
			Integer puntos = caraSuperior.getCoordenadas().size();
			
			for(int i=0; i<puntos-1; i++){
				Poligono estaCara = new Poligono();
				//estaCara.setTipo("lateral");
				
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
			
			this.setCaras(todasLasCaras);
			
		}
		
		//si hay planos de corte se procede a cortar las caras
		if(this.getPlanosDeCorte() != null){

			this.cortarCaras();
				
		}
		
		//los vacios necesitan una rotacion adicional
		if(this.getTipo()!=null && (this.getTipo().equals("ventana") || this.getTipo().equals("puerta"))){
			((Vacio)this).rotarCoordenadasVacio();
		}
		
	}
	
	public void cortarCaras(){

		//PolyhedronsSet boundingBox = obtenerBoundingBox();
		
		List<Poligono> carasACortar = null;
		List<Poligono> carasCortadas = null;
		
		//se hacen cortes con cada plano de corte existente
		for(PlanoDeCorte planoActual : planosDeCorte){
			
			if(carasACortar == null){
				carasACortar = this.caras;
			}else{
				carasACortar = carasCortadas;
			}
			
			//las originales
			planoActual.setCarasACortar(carasACortar);
			carasCortadas = cortarCarasConPlano(carasACortar, planoActual);
			//se guarda el historico de las caras que se cortaron solo para imprimir en pantalla
			planoActual.setCarasResultado(carasCortadas);
			
			if(planoActual.getCaraDeCorte().getCoordenadas().size() >= 4){
				
				planoActual.getCaraDeCorte().ordenarVerticesRespectoACentroide();
				
				//es necesario agregar la coordenada inicial para cerrar el poligono
				//esto debido a que la cara de corte se calcula SIN vertices repetidos
				Coordenada inicial = planoActual.getCaraDeCorte().getCoordenadas().get(0);
				planoActual.getCaraDeCorte().getCoordenadas().add(new Coordenada(inicial.getX(), inicial.getY(), inicial.getZ()));
				
				carasCortadas.add( planoActual.getCaraDeCorte());
				
			}
			
			this.caras = carasCortadas;
			
		}
		
		
		
		
	}
	
	public List<Poligono> cortarCarasConPlano(List<Poligono> pCaras, PlanoDeCorte pPlano){
		
		List<Poligono> r = new ArrayList();
		
		for (Poligono caraActual : pCaras) {

//			System.err.println("ERROR EN " + this.getId());
			Poligono caraCortada = caraActual.cortar(pPlano);
			//cualquier poligono debe tener al menos 4 puntos (minimo 3 más el primero repetido para cerrar el poligono)
			if(caraCortada.getCoordenadas().size() >= 4){
				r.add(caraCortada);
			}
			
		}
		
		return r;
	}
	
	public PolyhedronsSet obtenerBoundingBox(){
		
		/*
		List<SubHyperplane> subPlanos = new ArrayList(); 
		 
		 for (Poligono caraActual : caras){
			 
			 Iterator<Coordenada> i = caraActual.getCoordenadas().iterator();
			 
			 int c = 0;
			 
			 Coordenada pa = caraActual.getCoordenadas().get(0);
			 Coordenada pb = caraActual.getCoordenadas().get(1);
			 Coordenada pc = caraActual.getCoordenadas().get(2);
			 
			 Plane planoActual = new Plane(pa.toVector3D(), pb.toVector3D(), pc.toVector3D());
			 
			 SubPlane hiperPlano = planoActual.wholeHyperplane();
			 
			 subPlanos.add(hiperPlano);
			 
		 }
		 
		 PolyhedronsSet poliHedros = new PolyhedronsSet((BSPTree<Euclidean3D>) subPlanos);
		 
		 return poliHedros;
		 
		 */
		
		
		
		Double xMin = null;
		Double yMin = null;
		Double zMin = null;
        
		Double xMax = null;
		Double yMax = null;
		Double zMax = null;
        
        
		 for (Poligono caraActual : caras){
			 
			 Iterator<Coordenada> i = caraActual.getCoordenadas().iterator();
			 
			 int c = 0;
			 
			 while(i.hasNext()){
				 
				 Coordenada coordenadaActual = i.next();
				 
				//minimos
				 
				 if(xMin == null || coordenadaActual.getX() < xMin){
					 xMin = coordenadaActual.getX(); 
				 }
				 
				 if(yMin == null || coordenadaActual.getY() < yMin){
					 yMin = coordenadaActual.getY(); 
				 }
				 
				 if(zMin == null || coordenadaActual.getZ() < zMin){
					 zMin = coordenadaActual.getZ(); 
				 }
				 
				 //maximos
				 
				 if(xMax == null || coordenadaActual.getX() > xMax){
					 xMax = coordenadaActual.getX(); 
				 }
				 
				 if(yMax == null || coordenadaActual.getY() > yMax){
					 yMax = coordenadaActual.getY(); 
				 }
				 
				 if(zMax == null || coordenadaActual.getZ() > zMax){
					 zMax = coordenadaActual.getZ(); 
				 }
			 }
			 
			 
		 }
		 
		 PolyhedronsSet r = new PolyhedronsSet(xMin, xMax, yMin, yMax, zMin, zMax);
		 
		 return r;

		 
	}
	
	public static void extraerCoordenadasDeExtrudedAreaSolid(IfcExtrudedAreaSolid itemActual, Solido pSolido){
		
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
        
        pSolido.representation.setRepresentation_position_location(coord);
        
        
        
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
        
        pSolido.representation.setRepresentation_extruded_direction(coord);
        
        
        
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
            
            pSolido.representation.setRepresentation_position_axis(coord);
            
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
            
            pSolido.representation.setRepresentation_position_refDirection(coord);
        }
        
        
        
        
        if(itemActual.getSweptArea() instanceof IfcArbitraryClosedProfileDef){
        	
        	IfcArbitraryClosedProfileDef sweptArea = (IfcArbitraryClosedProfileDef) itemActual.getSweptArea();
            //int at = (Integer) sweptAreaAtributos.get("Count");
            //sobra?
        	pSolido.representation.setRepresentation_representation_SweptAreaType(sweptArea.getClass().getSimpleName());
            Object outerCurve = sweptArea.getOuterCurve();
        	
            if(outerCurve instanceof IfcPolyline){
            	
            	pSolido.setRepresentation_points(new ArrayList());
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
                    
                    pSolido.getRepresentation_points().add(coordenada);
                    
                }                            
            	
            }
        	
            
            if(outerCurve instanceof IfcCompositeCurve){
            	
            	pSolido.setRepresentation_segmentos(new ArrayList());
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
                        
                        pSolido.getRepresentation_segmentos().add(segmento);
                        
                    }
                    
                    if(parentCurve instanceof IfcTrimmedCurve){
                    	
                    	//no se tiene en cuenta IfcTrimmedCurve
                    }
                }
            }
        }
        
        
        if(itemActual.getSweptArea() instanceof IfcRectangleProfileDef){
            
        	
        	IfcRectangleProfileDef sweptArea = (IfcRectangleProfileDef) itemActual.getSweptArea();
        	pSolido.representation.setRepresentation_representation_SweptAreaType(sweptArea.getClass().getSimpleName());
        	
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
            
            pSolido.setRectangulo(rec);
            
            
        }
         
        //se calculan las coordenadas absolutas de la geometria que define el perfil de la plancha
        pSolido.calcularCoordenadasAbsolutas();
		
	}
	
	public static void agregarPlanoDeCorte(Solido pSolido, IfcHalfSpaceSolid halfSpace){
		
		if(pSolido.getPlanosDeCorte() == null){
			pSolido.setPlanosDeCorte(new ArrayList());
		}
		
		IfcPlane plano = (IfcPlane) halfSpace.getBaseSurface();
		
		Coordenada locationIfc = LectorCoordenada.Leer(plano.getPosition().getLocation());
		Coordenada axisIfc = null;
		Coordenada refDirectionIfc = null;
		Coordenada normalIfc = new Coordenada(0,0,0);
		
		Transformador t = new Transformador();

		if(plano.getPosition().getAxis()!= null && plano.getPosition().getRefDirection()!= null){
			
			axisIfc = LectorCoordenada.Leer(plano.getPosition().getAxis());
			refDirectionIfc = LectorCoordenada.Leer(plano.getPosition().getRefDirection());

		}

		locationIfc = t.convertirEnAbsoluta(locationIfc, pSolido);
		normalIfc = t.convertirEnAbsoluta(normalIfc, pSolido);
		if(axisIfc!=null) axisIfc = t.convertirEnAbsoluta(axisIfc, pSolido);
		
		try {
			
			Coordenada axisIfcParaApache = new Coordenada(axisIfc.toVector3D().subtract(normalIfc.toVector3D()));
			
			//se define el plano de apache commons
			//dicho plano contiene al origen (0,0,0) y es normal a axisIfcParaApache 
			Plane planoDeCorteApacheCommons = new Plane(axisIfcParaApache.toVector3D());
			
			//el plano de apache commons se translada a la posicion original definida en locationIfc para que concuerde
			Plane planoTransladado = planoDeCorteApacheCommons.translate(locationIfc.toVector3D());
			PlanoDeCorte p = new PlanoDeCorte(plano, planoTransladado);
			
			p.setAgreementFlagIfc(halfSpace.getAgreementFlag().value);
			
			p.setLocationAbsolutaIfc(locationIfc);
			p.setNormalAbsolutaIfc(normalIfc);
			
			//finalmente el plano se agrega al listado de planos de corte
			pSolido.getPlanosDeCorte().add(p);
			
		} catch (Exception e) {
			System.err.println(" NO SE PUEDE CREAR EL PLANO PORQUE TIENE NORMA = 0" );
		}
		
	}
	
}
