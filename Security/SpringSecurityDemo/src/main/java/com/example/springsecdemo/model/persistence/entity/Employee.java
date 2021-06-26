package com.example.springsecdemo.model.persistence.entity;

import java.sql.Date;

import javax.persistence.Id;

import com.example.springsecdemo.util.IEntity;


@javax.persistence.Entity(name = "emp")
public class Employee implements IEntity<Employee> {

	// TUVIMOS QUE USAR PRIMITIVE WRAPPERS TIPO Integer AL USAR HQL
	
	@Id
	Long empno;
	
	String ename;
	
	String job;
	
	Integer mgr;
	
	Date hiredate;
	
	Integer sal;
	
	Integer comm;
	
	Integer deptno;
	
	public Employee() {}

	public Long getEmpno() {
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

	public void setEmpno(Long empno) {
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

	@Override
	public Employee toDto() {
		Employee dto = new Employee();
		dto.setEmpno(empno);
		dto.setEname(ename);
		dto.setJob(job);
		return dto;
	}

}
