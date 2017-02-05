package com.innvo.web.rest.util;

import java.util.List;

import com.innvo.domain.Pathwaycountermeasurembr;
import com.innvo.domain.Pathwaypathwaymbr;

public class PathwayCountermeasureUtil {

	Pathwaypathwaymbr pathwaypathwaymbr;
	
	List<Pathwaycountermeasurembr> pathwaycountermeasurembrs;
	
	String color;

	public Pathwaypathwaymbr getPathwaypathwaymbr() {
		return pathwaypathwaymbr;
	}

	public void setPathwaypathwaymbr(Pathwaypathwaymbr pathwaypathwaymbr) {
		this.pathwaypathwaymbr = pathwaypathwaymbr;
	}

	public List<Pathwaycountermeasurembr> getPathwaycountermeasurembrs() {
		return pathwaycountermeasurembrs;
	}

	public void setPathwaycountermeasurembrs(List<Pathwaycountermeasurembr> pathwaycountermeasurembrs) {
		this.pathwaycountermeasurembrs = pathwaycountermeasurembrs;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	
}
