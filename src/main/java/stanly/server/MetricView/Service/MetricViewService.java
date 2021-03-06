package stanly.server.MetricView.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import stanly.server.Analysis.DAO.RelationDAO;
import stanly.server.Analysis.Model.ProjectElementNode;
import stanly.server.Analysis.Model.Relation.NodeRelation;
import stanly.server.GitProject.DAO.ProjectDAO;
import stanly.server.GitProject.Model.ProjectCommit;
import stanly.server.GitProject.Model.ProjectInfo;
import stanly.server.MetricView.DAO.ElementSearchDAO;
import stanly.server.MetricView.DAO.MetricSearchDAO;
import stanly.server.MetricView.Json.Relation;
import stanly.server.MetricView.Json.RelationList;
import stanly.server.MetricView.Json.TreeElement;
import stanly.server.MetricView.Json.DoT.CompositionNode;
import stanly.server.MetricView.Json.DoT.CompositionRelation;
import stanly.server.MetricView.Json.DoT.CompositionView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Karuana
 * 매트릭과 관련된 처리를 하는 서비스 객체이다. 
 */
@Service("metricViewService")
public class MetricViewService {
	protected static final Logger logger = Logger.getLogger("MetricViewService");
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private RelationDAO relationDao;
	@Autowired
	private ElementSearchDAO EsearchDAO;
	@Autowired
	private MetricSearchDAO MSearchDAO;
	private String sprite(String path)
	{
		String[]  arr =  path.split("\\.");
		logger.info((arr.length>1) ?  arr[arr.length-1]:path);
		return (arr.length>1) ?  arr[arr.length-1]:path;
	}
	
	/**
	 * Relation , 페이징 처리가 필요할 것 같다. 너무 데이터가 많이 오는 것 아닌가? 
	 * @param projectName
	 * @param SrcName
	 * @return
	 */
	public String getRelationWithSrc(String projectName, int SrcID)
	{
		logger.info("Relation Calc");
		ProjectCommit commit = projectDAO.getLastCommit(projectDAO.getProjectInfo(projectName));

		ProjectElementNode srcnode = EsearchDAO.getElementNode(commit, SrcID);
	
		List<NodeRelation> nodeR = relationDao.getSrcLikeRelation(commit, srcnode.getName());
		Gson gson = new Gson();
		RelationList rList = new RelationList();
		if(nodeR != null)
		{	
			for(int i=0;i<nodeR.size();i++)
			{
				NodeRelation node = nodeR.get(i);
				Relation rel = new Relation(node.getSrcName(), node.getTarName(), node.getType().name());
				rList.addRelation(rel);
			}
		}
		return gson.toJson(rList);
	}
	
	/**
	 *	타켓을 핵심으로 릴레이션을 가져온다. 
	 * @param projectName
	 * @param TarName
	 * @return
	 */
	public String gatRelationWithTar(String projectName,String TarName)
	{
		ProjectCommit commit = projectDAO.getLastCommit(projectDAO.getProjectInfo(projectName));
		List<NodeRelation> nodeR = relationDao.getTarLikeRelation(commit, TarName);
		Gson gson = new Gson();
		RelationList rList = new RelationList();
	
		for(int i=0;i<nodeR.size();i++)
		{
			NodeRelation node = nodeR.get(i);
			Relation rel = new Relation(node.getSrcName(), node.getTarName(), node.getType().name());
			rList.addRelation(rel);
		}
		return gson.toJson(rList);
	}
	
	/**
	 * SRC와 TAR을 중심으로 릴레이션 정보를 가져온다.
	 * @param projectName
	 * @param SrcName
	 * @param TarName
	 * @return
	 */
	public String getRelation(String projectName, String SrcName, String TarName)
	{
		
		ProjectCommit commit = projectDAO.getLastCommit(projectDAO.getProjectInfo(projectName));

		List<NodeRelation> nodeR = relationDao.getLikeRelation(commit,SrcName, TarName);
		Gson gson = new Gson();
		RelationList rList = new RelationList();
	
		for(int i=0;i<nodeR.size();i++)
		{
			NodeRelation node = nodeR.get(i);
			Relation rel = new Relation(node.getSrcName(), node.getTarName(), node.getType().name());
			rList.addRelation(rel);
		}
		return gson.toJson(rList);
	}
	/**
	 * SRC와 TAR을 중심으로 릴레이션 정보를 가져온다.
	 * @param projectName
	 * @param SrcName
	 * @param TarName
	 * @return
	 */
	public String getRelation(String projectName, int SrcID, int TarID)
	{
		
		ProjectCommit commit = projectDAO.getLastCommit(projectDAO.getProjectInfo(projectName));
		ProjectElementNode srcnode = EsearchDAO.getElementNode(commit, SrcID);
		ProjectElementNode tarnode = EsearchDAO.getElementNode(commit, TarID);
		List<NodeRelation> nodeR = relationDao.getLikeRelation(commit,srcnode.getName(), tarnode.getName());
		Gson gson = new Gson();
		RelationList rList = new RelationList();
	
		for(int i=0;i<nodeR.size();i++)
		{
			NodeRelation node = nodeR.get(i);
			Relation rel = new Relation(node.getSrcName(), node.getTarName(), node.getType().name(),SrcID,TarID);
			rList.addRelation(rel);
		}
		return gson.toJson(rList);
	}
	/**
	 * 트리 그래프를 리턴한다. 
	 *  NSLeft  == nodeID 로 사용할 예
	 *  ID의 자식을 반환한다. 
	 * @param projectName
	 * @param nodeID
	 * @return
	 */
	public String getTreeNode(String projectName, int nodeID){ 
		ProjectInfo info = projectDAO.getProjectInfo(projectName);
		ProjectCommit commit = projectDAO.getLastCommit(info);
		ArrayList<TreeElement> Elements = new ArrayList<TreeElement>();
		logger.info("Tree Init");
		if(nodeID == 0)
		{
			ProjectElementNode node = EsearchDAO.getProjectNode(commit);
			if(node==null)
				logger.info("Null Log");
			
			TreeElement projectNode = new TreeElement(info.getName(),node.getNSLeft() ,node.getNSRight());
			projectNode.setAttrID(Integer.toString(1));
	
			projectNode.setRel(node.getType().name());
			Elements.add(projectNode);
		}
		else
		{
			ProjectElementNode node= EsearchDAO.getElementNode(commit, nodeID);
			
			List<ProjectElementNode> nodelist = EsearchDAO.getChildNode(node.getName(), node.getNSLeft(),node.getNSRight(), node.getType(),commit);
			for(int i=0;i<nodelist.size();i++)
			{
				TreeElement projectNode = new TreeElement(sprite(nodelist.get(i).getName()),nodelist.get(i).getNSLeft() ,nodelist.get(i).getNSRight());
				projectNode.setAttrID(Integer.toString(nodelist.get(i).getNSLeft()));
				projectNode.setRel(nodelist.get(i).getType().name());
				Elements.add(projectNode);
			}
		}
		Gson gosn = new Gson();

		return gosn.toJson(Elements);
	}
    
	/**
	 * 오염도 리스트를 리턴한다.
	 * @param projectName
	 * @param nodeID
	 * @return
	 */
	public String getPollutionList(String projectName, int nodeID)
	{
		ProjectInfo info = projectDAO.getProjectInfo(projectName);
		ProjectCommit commit = projectDAO.getLastCommit(info);
		int NSRight = EsearchDAO.getElementNode(commit, nodeID).getNSRight();
		Gson gosn = new Gson();

		return gosn.toJson(MSearchDAO.getPollutionList(commit, nodeID,NSRight));
	}
	
	/**
	 * 마틴 지표 리스트를 반환한다. 
	 * @param projectName
	 * @param nodeID
	 * @return
	 */
	public String getMartinList(String projectName, int nodeID)
	{
		ProjectInfo info = projectDAO.getProjectInfo(projectName);
		ProjectCommit commit = projectDAO.getLastCommit(info);
		
		Gson gosn = new Gson();

		return gosn.toJson(MSearchDAO.getMertinValue(commit, nodeID));
	}
	
	/**
	 * 오염도 차트를 리턴한다. 
	 * @param projectName
	 * @param NSleft
	 * @return
	 */
	public String getPollutionChart(String projectName, int NSleft)
	{
		ProjectInfo info = projectDAO.getProjectInfo(projectName);
		ProjectCommit commit = projectDAO.getLastCommit(info);
		int NSRight = EsearchDAO.getElementNode(commit, NSleft).getNSRight();
		
		Gson gosn = new Gson();
		
		return gosn.toJson(MSearchDAO.getPollutionChart(commit, NSleft,NSRight));
	}
	
	/**
	 * Code Size View 를 리턴한다. 
	 * @param projectName
	 * @param NSleft
	 * @return
	 */
	public String getCodeSize(String projectName, int NSleft)
	{	
		ProjectInfo info = projectDAO.getProjectInfo(projectName);
		ProjectCommit commit = projectDAO.getLastCommit(info);
		int NSRight = EsearchDAO.getElementNode(commit, NSleft).getNSRight();
		
		Gson gosn = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		return gosn.toJson(MSearchDAO.getCodeSize(commit, NSleft,NSRight));
	}

	
	/**
	 * 마틴 디스턴스를 리턴한다.
	 * @param projectName
	 * @param NSleft
	 * @return
	 */
	public String getMartinDistance(String projectName, int NSleft)
	{
		ProjectInfo info = projectDAO.getProjectInfo(projectName);
		ProjectCommit commit = projectDAO.getLastCommit(info);
		int NSRight = EsearchDAO.getElementNode(commit, NSleft).getNSRight();
		
		
		Gson gson = new Gson();
		return gson.toJson(MSearchDAO.getMartinDistance(commit, NSleft,NSRight));
	}
	/**
	 * 메트릭 정보를 리턴한다. 
	 * @param projectName
	 * @param NSleft
	 * @return
	 */
	public String getMetrics(String projectName, int NSleft)
	{
		ProjectInfo info = projectDAO.getProjectInfo(projectName);
		ProjectCommit commit = projectDAO.getLastCommit(info);
		
		Gson gson = new Gson();
		return gson.toJson(MSearchDAO.getMetrics(commit, NSleft));
	}
	/**
	 * @param commit 찾을 프로젝트의 커
	 * @param NSLeft 찾을 Parent 노드의 NSLeft 값 
	 * @return
	 */
	@Cacheable(value="CompositionView")
	public String getCompositionView(String projectName, int NSLeft)
	{
		ProjectInfo info = projectDAO.getProjectInfo(projectName);
		ProjectCommit commit = projectDAO.getLastCommit(info);
		// 대충 순서를 정하자
		ProjectElementNode parent = EsearchDAO.getElementNode(commit, NSLeft);
		CompositionView composition = new CompositionView(Integer.toString(NSLeft));
		String SubGraphPath="/Stanly/component/CompositionView?Name="+commit.getPInfo().getName()+"&nodeID=";
		HashMap<String,Integer> childMap = new HashMap<String,Integer>();
		HashSet<Integer> IsClass = new HashSet<Integer>();
		
		try{
			List<ProjectElementNode> ignore=null;
			List<ProjectElementNode> Child = EsearchDAO.getChildNode(parent.getName(), parent.getNSLeft(), parent.getNSRight(), parent.getType(), commit);
			for(int i=0;i<Child.size();i++)
			{
				ProjectElementNode node = Child.get(i);
				String Type=null;
				boolean subGraph;
				boolean Create=true;
				String imgSrc =null;
				switch(node.getType())
				{
				case PROJECT:
				case LIBRARY:
						
					imgSrc="'/Stanly/assets/PackageGraph/img/subsystem.gif'";
				case PACKAGESET:
					if(imgSrc==null)
					imgSrc="'/Stanly/assets/PackageGraph/img/tree.gif'";
				case PACKAGE:
					//자기 자신을 제외한 다른 요소는 예외 처리용으로 등록 
					if(node.getName().contentEquals(parent.getName()))
					{
						ignore = new ArrayList<ProjectElementNode>();
						ignore.addAll(Child);
						ignore.remove(node);
					}
					
					if(node.getNSRight()-node.getNSLeft()>1)
						subGraph=true;
					Type = "package";
					if(imgSrc==null)
					imgSrc="'/Stanly/assets/PackageGraph/img/package.gif'";
					
					break;
				case CLASS:
					if(node.getNSRight()-node.getNSLeft()>1)
						subGraph=true;
					Type = "JavaClass";
					IsClass.add(node.getNSLeft());
					if(imgSrc==null)
					imgSrc="'/Stanly/assets/PackageGraph/img/class.gif'";
					break;
				case INTERFACE:
					if(node.getNSRight()-node.getNSLeft()>1)
						subGraph=true;
					Type = "Interface";
					IsClass.add(node.getNSLeft());
					if(imgSrc==null)
					imgSrc="'/Stanly/assets/PackageGraph/img/interface.gif'";
					break;
				default:
					Create=false;
					break;
				}
				if(Create)
				{
					childMap.put(node.getName(), node.getNSLeft());
					composition.addNode(new CompositionNode(Integer.toString(node.getNSLeft()),"<div style='padding: 10px;'><img src ="+imgSrc+" width='16' height='16'/>"+sprite(node.getName())+"</div>",SubGraphPath+node.getNSLeft(),Integer.toString(NSLeft),Type));
					
				}
			}
			
			//릴레이션 설정
			Object[] keySet = childMap.keySet().toArray();
			int arr[][]= new int[keySet.length][keySet.length];

			for(int i=0;i<keySet.length;i++)
			{
				for(int j=0;j<keySet.length;j++)
				{
					arr[i][j]=0;
					if(i==j)
						continue;
					long count =0;
					if(ignore!=null && (((String)keySet[i]).contentEquals(parent.getName())))
						count= relationDao.getCountRelation(commit, (String)keySet[i], (String)keySet[j],ignore,true);
					else if(ignore!=null && (((String)keySet[j]).contentEquals(parent.getName())))
						count= relationDao.getCountRelation(commit, (String)keySet[i], (String)keySet[j],ignore,false);
					else
						count= relationDao.getCountRelation(commit, (String)keySet[i], (String)keySet[j]);
					if(count == 0)
						continue;
					
					arr[i][j] = (int)count;
				}
			}
			
			for(int i=0;i<keySet.length;i++)
			{
				for(int j=0;j<keySet.length;j++)
				{
					if(arr[i][j]!=0)
					{
						
						CompositionRelation relation = new CompositionRelation(childMap.get(keySet[i]).toString(),childMap.get(keySet[j]).toString(),arr[i][j]);
						
						if(arr[j][i]!=0 && arr[i][j]<arr[j][i])
						{
							if(!IsClass.contains(childMap.get(keySet[i])))
								relation.setTangled(true);
						}
						composition.addRelation(relation);
					}
					
				}
			}
			
		}catch(Exception e)
		{
			logger.info(e);
			return null;
		}

		logger.info(composition.toString());
		return composition.toString();
	}
}
