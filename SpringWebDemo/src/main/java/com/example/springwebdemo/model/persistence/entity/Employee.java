package com.example.springwebdemo.model.persistence.entity;

import java.sql.Date;

import javax.persistence.Id;


//@javax.persistence.Table(name = "emp")
//@org.hibernate.annotations.Table(appliesTo = "emp")
@javax.persistence.Entity(name = "emp")
		//ESTA ES LA ÚNICA QUE HACE QUE MAPEE AL SESSION FACTORY DE HIBERNATE
		//Si no ponemos name, lo registra con el nombre simple de la clase, y no hay manera de hacerle ref en el HQL, ni con FQCN ni con simple class name
		//Hay que usar name y en el HQL sirven ambas referencias
public class Employee {

	// TUVIMOS QUE USAR PRIMITIVE WRAPPERS TIPO Integer AL USAR HQL
	
	@Id
	Integer empno;
	
	String ename;
	
	String job;
	
	Integer mgr;
	
	Date hiredate;
	
	Integer sal;
	
	Integer comm;
	
	Integer deptno;
	
	public Employee() {}

	public Integer getEmpno() {
		return empno;
	}

	public String getEname() {
		return ename;
	}

	public String getJob() {
		return job;
	}

	public Integer getMgr() {
		return mgr;
	}

	public Date getHiredate() {
		return hiredate;
	}

	public Integer getSal() {
		return sal;
	}

	public Integer getComm() {
		return comm;
	}

	public Integer getDeptno() {
		return deptno;
	}

	public void setEmpno(Integer empno) {
		this.empno = empno;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public void setMgr(Integer mgr) {
		this.mgr = mgr;
	}

	public void setHiredate(Date hiredate) {
		this.hiredate = hiredate;
	}

	public void setSal(Integer sal) {
		this.sal = sal;
	}

	public void setComm(Integer comm) {
		this.comm = comm;
	}

	public void setDeptno(Integer deptno) {
		this.deptno = deptno;
	}

	@Override
	public String toString() {
		return "Employee [empNo=" + empno + ", ename=" + ename + ", job=" + job + ", mgr=" + mgr + ", hiredate="
				+ hiredate + ", sal=" + sal + ", comm=" + comm + ", deptno=" + deptno + "]";
	}
	
}
