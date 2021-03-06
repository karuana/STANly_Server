package stanly.server.Analysis.Model.Metric;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import stanly.server.Analysis.Model.ProjectElementNode;
import stanly.server.Analysis.Model.Metric.Rate.MetricRate;
import stanly.server.Analysis.Model.Type.NodeType;

/**
 * 클래스의 필드와 관련된 매트릭 정보를 정하는 클래스이다.
 * ElementNodeMetric을 상속받아 구현하고 있다.
 * 하이버네이트의 기본적인 상속 맵핑 방식중 Table per subclass를 이용하였다.
 * @author Karuana
 *
 */
@Entity
@Table(name = "AttributeMetric")
@PrimaryKeyJoinColumn(name="EMID")
public class AttributeMetric extends ElementNodeMetric{
	

	/**
	 * 인스트럭션 수
	 * Count Metrics
	 */
	@Column(name = "Instructions")
	private int Instructions;
	/**
	 * 예상 줄 수 ELOC = Estimated Lines of Code
	 * Count Metrics
	 */
	@Column(name = "ELOC")
	private int ELOC;
	/**
	 * Cyclomatic Complexity 
	 * Complexity Metrics
	 */
	@Column(name = "CC")
	private int CC;
	

	public AttributeMetric() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AttributeMetric(ProjectElementNode node, NodeType type) {
		super(node, type);
		// TODO Auto-generated constructor stub
	}

	public AttributeMetric(NodeType type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	public AttributeMetric(int instructions, int eLOC, int cC, ProjectElementNode node, NodeType type) {
		super(node, type);
		Instructions = instructions;
		ELOC = eLOC;
		CC = cC;
	}
	
	public int getInstructions() {
		return Instructions;
	}
	public void setInstructions(int instructions) {
		Instructions = instructions;
	}
	public int getELOC() {
		return ELOC;
	}
	public void setELOC(int eLOC) {
		ELOC = eLOC;
	}
	public int getCC() {
		return CC;
	}
	public void setCC(int cC) {
		CC = cC;
	}

	@Override
	public void setRate()
	{
		UnitsRate =MetricRate.NO_RATE;
		ELOCRate = MetricRate.NO_RATE;
		NOMRate = MetricRate.NO_RATE;
		NOFRate = MetricRate.NO_RATE;
		CCRate = MetricRate.NO_RATE;
		TangleRate = MetricRate.NO_RATE;
		NoRRate	= MetricRate.NO_RATE;
		DRate = MetricRate.NO_RATE;
		DITRate = MetricRate.NO_RATE;
		
		int ccM = (CC<15) ? MetricRate.A_RATE: ((CC<20) ? MetricRate.B_RATE: MetricRate.C_RATE);
		int locM = (ELOC<60) ? MetricRate.A_RATE: ((ELOC<120) ? MetricRate.B_RATE: MetricRate.C_RATE);
		fatRate = (int) Math.ceil((ccM+locM)/2.0f);
		
		CPRate = MetricRate.NO_RATE;
		CouplingRate = MetricRate.NO_RATE;
		TotalRate = (fatRate + CouplingRate)/2;
	}

}
