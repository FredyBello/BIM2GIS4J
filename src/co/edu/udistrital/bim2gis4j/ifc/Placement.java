package co.edu.udistrital.bim2gis4j.ifc;

import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;

public class Placement {

	// ObjectPlacement
	
	public Coordenada placementRelTo_placementRelTo;
	
	public Coordenada placementRelTo_relativePlacement;
	
	public Coordenada getPlacementRelTo_placementRelTo() {
		return placementRelTo_placementRelTo;
	}

	public void setPlacementRelTo_placementRelTo(
			Coordenada placementRelTo_placementRelTo) {
		this.placementRelTo_placementRelTo = placementRelTo_placementRelTo;
	}

	public Coordenada getPlacementRelTo_relativePlacement() {
		return placementRelTo_relativePlacement;
	}

	public void setPlacementRelTo_relativePlacement(
			Coordenada placementRelTo_relativePlacement) {
		this.placementRelTo_relativePlacement = placementRelTo_relativePlacement;
	}
	
	
	
	
	public Axis2Placement3D relativePlacement = new Axis2Placement3D();
	
	
	public Coordenada getRelativePlacement_axis() {
		return relativePlacement.axis;
	}

	public void setRelativePlacement_axis(Coordenada axis) {
		relativePlacement.axis = axis;
	}

	public Coordenada getRelativePlacement_refDirection() {
		return relativePlacement.refDirection;
	}

	public void setRelativePlacement_refDirection( Coordenada refDirection) {
		this.relativePlacement.refDirection = refDirection;
	}

	public Coordenada getRelativePlacement_location() {
		return relativePlacement.location;
	}

	public void setRelativePlacement_location( Coordenada location) {
		this.relativePlacement.location = location;
	}
	

}
