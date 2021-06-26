package com.example.springwebdemo.model.persistence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.springwebdemo.model.persistence.entity.Employee;

/**
 * NOTAR que el Dao hace ref a un DataSource en particular
 * 
 * habría que evaluar cómo setear los Spring @Repository para que usen un DataSource particular, en el caso de configurar varios en el proyecto (usan el @Primary).
 * -> Los Repositories tendrían Daos y DataSource varios. Los Repositories son como Daos de domain objects, los Daos representan a una entidad en particular en el modelo de datos.
 * */
@Component
@ComponentScan(basePackages = {"com.example"})
public class EmployeeDao {
	
	
	//ENCONTRARÁ N DataSource EN EL PAQUETE ESCANEADO, ENTONCES:
	//Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans (una lista), or using @Qualifier to identify the bean that should be consumed
	@Qualifier("h2_1")
	@Autowired private DataSource dsH2_1;
	
	@Qualifier("jndi-datasource")
	@Autowired private DataSource jndi_datasource;
	

	//TAMBIÉN PODEMOS setear en el constructor o en un método setter, les llegará lo que tenga registrado el contenedor Spring como beans
	/*@Autowired //sobre un constructor
	public EmployeeDao(ApplicationContext ctx) { // o DataSource object directly
		this.ds = ctx.getBean("h2_1", DataSource.class);
	}*/
	//@Autowired public void setDataSource(DataSource ds) {...}

	
	/**
	 * PARA MOSTRAR USO DE HIBERNATE
	 * */
	@Autowired private SessionFactory hibSessionFactory;
	
	@Autowired	private HibernateTransactionManager hibTxMan;
	
	
	public EmployeeDao() {}
	
	
	public List<Employee> fetchAll_WithDatasource(DataSource datas) {
		String SQL_QUERY = "select * from emp";
		List<Employee> employees = new ArrayList<>();
		try (
				Connection con = datas.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_QUERY);
				ResultSet rs = pst.executeQuery();
				) {
				
				employees = fetchAllFromRs(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}
	
	
	public List<Employee> fetchAll_using_h2_1() {
		String SQL_QUERY = "select * from emp";
		List<Employee> employees = new ArrayList<>();
		try (
				Connection con = dsH2_1.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_QUERY);
				ResultSet rs = pst.executeQuery();
				) {
			
				employees = fetchAllFromRs(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return employees;
	}
	
	
	public List<Employee> fetchAll_using_jndi_datasource() {
		String SQL_QUERY = "select * from emp";
		List<Employee> employees = new ArrayList<>();
		try (
				Connection con = jndi_datasource.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_QUERY);
				ResultSet rs = pst.executeQuery();
				) {
			
				employees = fetchAllFromRs(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return employees;
	}
	

	private List<Employee> fetchAllFromRs(ResultSet rs) throws SQLException {
		List<Employee> employees = new ArrayList<>();
		Employee employee;
		while (rs.next()) {
			employee = new Employee();
			employee.setEmpno(rs.getInt("empno"));
			employee.setEname(rs.getString("ename"));
			employee.setJob(rs.getString("job"));
			employee.setMgr(rs.getInt("mgr"));
			employee.setHiredate(rs.getDate("hiredate"));
			employee.setSal(rs.getInt("sal"));
			employee.setComm(rs.getInt("comm"));
			employee.setDeptno(rs.getInt("deptno"));
			employees.add(employee);
		}
		return employees;
	}
	
	
	//--------------------------------------------
	
	
	/**
	 * MOSTRAMOS USO DE HIBERNATE
	 * */
	{
		//// HIBERNATE MOST INTERESTING METHODS ////
		
		//hibSessionFactory.addNamedQuery(name, Query);
		//hibSessionFactory.close();
		//hibSessionFactory.createEntityManager();
		//hibSessionFactory.createEntityManager(Map);
		//hibSessionFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
		//hibSessionFactory.createEntityManager(SynchronizationType.SYNCHRONIZED, Map);
					//.createStoredProcedureQuery(procedureName)
					//.createStoredProcedureQuery(procedureName)
					//.createQuery(sqlString)
					//.createNativeQuery(sqlString)
					//.createNamedStoredProcedureQuery(name)
					//.createNamedQuery(name)
					//.contains(entity)
					//.detach(entity);
					//.getTransaction()
					//.joinTransaction();
					//.merge(entity)
					//.persist(entity);
					//.refresh(entity);
					//.remove(entity);
		//hibSessionFactory.getCache();
		//hibSessionFactory.getCriteriaBuilder();
				//--> Criteria Builder (JPA), es alternativa a JPQL... HCQL es alternativa a este Criteria Builder de JPA (mirar al fondo cómo se obtienen resultados usando esto)
				//.createQuery(entityClass).from(entityClass).where(restriction).select(selection)
				//.or()
				//.and().alias("")
				//...
		//hibSessionFactory.getCurrentSession();
					//.close();
					//.save(object);
					//.saveOrUpdate(object);
					//.update(object);
					//.delete(object);
					//.getTransaction();
		//hibSessionFactory.getMetamodel();
					//.getEntities()
		//hibSessionFactory.getPersistenceUnitUtil();
		//hibSessionFactory.getStatistics();
		//hibSessionFactory.isClosed();
		//hibSessionFactory.isOpen();
		//hibSessionFactory.openSession();
					//.createNamedQuery(null)
					//...
		//hibSessionFactory.openStatelessSession();
		//hibSessionFactory.openStatelessSession(Connection);
		
		//--> estas son de Hibernate
		//.createQuery("from MyEntity") --> DONDE USAR HQL
		//.getSingleResult()
		//.list()
		//.getResultList()
		//.getResultStream()
		//.setFirstResult(startPosition)
		//.setMaxResults(maxResult)
		//.setParameter(position, value)
		//.executeUpdate()
		//.createSQLQuery(queryString);
		//.createNamedQuery(name)
		//.createNamedStoredProcedureQuery(name)
		//.cancelQuery();
		//.getTransaction()
		//.beginTransaction()
		//.commit()
		//.rollback()
		//.joinTransaction();
		
		//---------------------------------------  //// HQL //// (SELECT Y UPDATE EN MÉTODOS ABAJO)
		
		// DELETE
		
		//Query query = session.createQuery("delete from Emp where id=100");  
		//query.executeUpdate();  
		
		// agreggate functions by HQL :/
		//*termina siendo sql, pero permite usar otra bbdd donde la sintáxis cambie
		
		//Query q=session.createQuery("select sum(salary) from Emp");
		//Query q=session.createQuery("select max(salary) from Emp");
	    //Query q=session.createQuery("select count(id) from Emp");  
	    
		//--------------------------------------- //// HCQL ////
		
		//Criteria interface
				//.add(Criterion c)
				//addOrder(Order o)
				//.setProjection(Projection projection)
				//Common Criterion
						//.lt(String propertyName,Object value) sets the less than constraint to the given property
						//.between(String propertyName, Object low, Object high) sets the between constraint
						//...
		
		// SELECT
		
		//Criteria c = (Criteria) session.getCriteriaBuilder().createQuery(Employee.class);  
		//c.setProjection(Projections.property("name")); //columns to fetch
		//List list2 = c.list();
		
		// UPDATE
		
		//c = (Criteria) session.getCriteriaBuilder().createCriteriaUpdate(Employee.class);
		//c.add(Restrictions.gt("salary",10000));//salary is the propertyname  
		//c.addOrder(Order.asc("salary"));
		
		//--------------------------------------- OBTENER RESULTADOS DE CRITERIA BUILDER DE JPA :/
		
		/* es algo así, pero no provoca ni ver cómo funciona!
		EntityManager em = hibSessionFactory.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
		Root<Employee> from = cq.from(Employee.class);

		cq.select(Employee);
		TypedQuery<Entity> q = em.createQuery(cq);
		List<Entity> allitems = q.getResultList();
		*/
	}

	
	public List<Employee> findAll_using_hibernate_HQL() {
		
		System.out.println("Session Factory mapped entities:");
		for (EntityType t : hibSessionFactory.getMetamodel().getEntities()) {
			System.out.println(t.getName());
		}
		System.out.println();
		
		//--------------------------------------- //// HQL ////
		
		// SELECT WITH PAGINATION
		
		Session session = hibSessionFactory.openSession();
		
		Query query = session.createQuery("from emp"); //com.example.springwebdemo.model.persistence.entity.Employee");//CLASS NAME OR TABLE NAME ? se explica en cabecera de Employee.class
		query.setFirstResult(1);
		query.setMaxResults(5);
		List<Employee> list = query.list();
		System.out.println();
		for (Employee emp : list) {
			//System.out.println(emp);
		}
		
		session.close();//debe usarse un try-catch-finally
		return list;
	}


	public void mod_employee_info() {
		
		//--------------------------------------- //// HQL ////
		
		// UPDATE
		
		//AMBAS FORMAS SIRVEN !!!
		Session sessionTx = hibSessionFactory.openSession();
		//Session sessionTx = hibTxMan.getSessionFactory().openSession();
		
		Transaction tx = null;
		try {
			tx = sessionTx.beginTransaction();
			Query q = sessionTx.createQuery("update emp set ename = :n where empno = :i");  
		    q.setParameter("n", "NEW NAME");
		    q.setParameter("i", 7566);  
		    int status = q.executeUpdate();
		    System.out.println("Updated rows: " + status);
		    sessionTx.flush();
		    tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
		} finally {
			if (sessionTx != null && sessionTx.isOpen())
				sessionTx.close();
		}
	}
	
	
	@Transactional
	public void txMethod() {
		// debo habilitar con @EnableTransactionManagement en @Configuration class.
	}
	
}
