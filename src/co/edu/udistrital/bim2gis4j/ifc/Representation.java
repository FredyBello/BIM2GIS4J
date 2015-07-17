package co.edu.udistrital.bim2gis4j.ifc;

import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;

public class Representation {

// Representation
	
	public String representation_representationType;
	
	public String representation_representation_SweptAreaType;

	public Axis2Placement3D position = new Axis2Placement3D();
	
	public Coordenada representation_extruded_direction;
	
	public int stepLineNumber;

	public int getStepLineNumber() {
		return stepLineNumber;
	}

	public void setStepLineNumber(int stepLineNumber) {
		this.stepLineNumber = stepLineNumber;
	}

	public String getRepresentation_representation_SweptAreaType() {
		return representation_representation_SweptAreaType;
	}

	public void setRepresentation_representation_SweptAreaType(
			String representation_representation_SweptAreaType) {
		this.representation_representation_SweptAreaType = representation_representation_SweptAreaType;
	}

	public Coordenada getRepresentation_extruded_direction() {
		return representation_extruded_direction;
	}

	public void setRepresentation_extruded_direction(
			Coordenada representation_extruded_direction) {
		this.representation_extruded_direction = representation_extruded_direction;
	}

	public Coordenada getRepresentation_position_location() {
		return position.location;
	}

	public void setRepresentation_position_location(
			Coordenada representation_position_location) {
		this.position.location = representation_position_location;
	}

	public Coordenada getRepresentation_position_axis() {
		return position.axis;
	}

	public void setRepresentation_position_axis(
			Coordenada representation_position_axis) {
		this.position.axis = representation_position_axis;
	}

	public Coordenada getRepresentation_position_refDirection() {
		return position.refDirection;
	}

	public void setRepresentation_position_refDirection(
			Coordenada representation_position_refDirection) {
		this.position.refDirection = representation_position_refDirection;
	}

	public String getRepresentation_representationType() {
		return representation_representationType;
	}

	public void setRepresentation_representationType(
			String representation_representationType) {
		this.representation_representationType = representation_representationType;
	}

}
