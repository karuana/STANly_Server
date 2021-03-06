package stanly.server.DevPersonalPage.DAO;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import stanly.server.GitProject.Model.ProjectCommit;
import stanly.server.GitProject.Model.ProjectInfo;
import stanly.server.GitProject.Model.committer.ProjectCommitter;

@Repository
@Transactional
public class DevCommitterDAO {
	protected static final Logger logger = Logger.getLogger("CommitterDAO - Dev");
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	public List<ProjectCommitter> getCommitterProject(String committer)
	{
		List<ProjectCommitter> committerProject=null;
		try{
			logger.info("Get Commit List");
			Session session = sessionFactory.getCurrentSession();
			
			 Criteria crit = session.createCriteria(ProjectCommitter.class);
			 Criterion committerName = Restrictions.eq("committer", committer.toLowerCase() ); 
			 crit.add(committerName);
			 crit.addOrder(Order.asc("id"));
			 committerProject = crit.list();
		
			
		}catch(Exception e)
		{
			logger.error(e);
			return committerProject;
		}
		return committerProject;
	}

	public List<ProjectInfo> getHMode()
	{
		List<ProjectInfo> projectList = null;
		
		try{
			Session session = sessionFactory.getCurrentSession();
			
			//쿼리에 테이블 명이 아닌 클래스명을 써야 한다.
		
			 Criteria crit = session.createCriteria(ProjectInfo.class);

			 crit.addOrder(Order.desc("pid"));
			 projectList =  crit.list();

		}catch(Exception e)
		{
			logger.error(e);
			return null;
		}
		return projectList;
	}

	
}
