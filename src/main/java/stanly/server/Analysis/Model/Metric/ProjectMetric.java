package stanly.server.Analysis.Model.Metric;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import stanly.server.Analysis.Model.ProjectElementNode;
import stanly.server.Analysis.Model.Type.NodeType;

@Entity
@Table(name = "ProjectMetric")
@PrimaryKeyJoinColumn(name="EMID")
public class ProjectMetric extends ElementNodeMetric{
	@Column(name = "Libraries", nullable = false)	
	private int Libraries;
	@Column(name = "Fat", nullable = false)	
	private float Fat;
	@Column(name = "Tangled", nullable = false)	
	private float Tangled;
	@Column(name = "ACDLibrary", nullable = false)	
	private float ACDLibrary;
	
	
	
	public float getACDLibrary() {
		return ACDLibrary;
	}
	public void setACDLibrary(float aCDLibrary) {
		ACDLibrary = aCDLibrary;
	}
	public ProjectMetric() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ProjectMetric(ProjectElementNode node, NodeType type) {
		super(node, type);
		// TODO Auto-generated constructor stub
	}
	public ProjectMetric(NodeType type) {
		super(type);
		// TODO Auto-generated constructor stub
	}
	public int getLibraries() {
		return Libraries;
	}
	public void setLibraries(int libraries) {
		Libraries = libraries;
	}
	public float getFat() {
		return Fat;
	}
	public void setFat(float fat) {
		Fat = fat;
	}
	public float getTangled() {
		return Tangled;
	}
	public void setTangled(float tangled) {
		Tangled = tangled;
	}
	
}