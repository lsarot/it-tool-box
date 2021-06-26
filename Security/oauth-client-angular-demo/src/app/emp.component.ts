import { Component } from '@angular/core';
import {AppService, Foo, Employee} from './app.service'

@Component({
  selector: 'emp-details',
  providers: [AppService],  
  template: 
  `<div class="container">
        <h1 class="col-sm-12">Employees Details</h1>
        
        <div class="col-sm-2">
        <button class="btn btn-primary" (click)="getEmployees()" type="submit">Get Employees</button>        
        </div>
        <div class="col-sm-2">
        <button class="btn btn-primary" (click)="getEmp()" type="submit">Get One Emp</button>        
        </div>
        <div class="col-sm-8">
        <button class="btn btn-primary" (click)="getUserInfo()" type="submit">Get UserInfo (see result debuging endpoint)</button>        
        </div>
        <br/>
        <ng-container *ngFor="let emp of emps">
            <br/>-------    
            <div class="col-sm-12">
                <label class="col-sm-3">EmpNo</label> <span>{{emp.empno}}</span>
            </div>
            <div class="col-sm-12">
                <label class="col-sm-3">Name</label> <span>{{emp.ename}}</span>
            </div>
            <div class="col-sm-12">
                <label class="col-sm-3">Job</label> <span>{{emp.job}}</span>
            </div>            
        </ng-container>
    </div>`
})

export class EmpComponent {

    public emps: Employee[];
    private empsUrl = 'http://localhost:8081/resource-server/api/emps/';  

    constructor(
        private _service:AppService) 
    {}

    getEmp() {
        this._service.getResource(this.empsUrl+7782)
            .subscribe(
                data => this.emps = data,
                error =>  this.emps = new Array<Employee>()
                );
    }

    getEmployees() {
        console.log("Getting Employees...");//se puso como una señal de que se ejecuta el llamado, pero recordar que Angular no recarga si no cambia la vista, igual que React.
        this._service.getResource(this.empsUrl)
            .subscribe(
                data => this.emps = data,
                error => this.emps = new Array<Employee>()
            );
    }

    getUserInfo() {
        this._service.getResource(this.empsUrl+'user/info-from-jwt')
            .subscribe(
                data => alert('SEE RESOURCE SERVER CONSOLE. It will show JWT partial content retrieved.'),
                error => null
            );
    }

}
