/**
 * AQUÍ EMPIEZA TODO
 *
 * HAY Modules, Components, y otros
 * 
 * Un módulo o componente importa elementos de otro fichero
 *    import {ModuleA} from '@angular/fichero'; si es del SDK de Angular (del core digamos)
 *    import {ClaseA, VariableB, ComponentA} from './home.component'; traerá ClaseA, VariableB, ComponentA del fichero home.component.ts que está en esta misma ruta
 * Un módulo puede cargar otro módulo
 *    en directiva imports: [ModuleA]
 * Un módulo especifica qué componentes vá a usar
 *    en directiva declarations: [ComponentA]
 * 
 * el fichero debe usar la keyword export para indicar que es utilizable con un import (digamos pública)
 * 
 * ver la clase para más detalles...
 * 
 * ejecutar con: npm start (esto es un shortcut a ng serve --port 8089, como está seteado en fichero package.json)
 * debugear: ejecuta la app y luego en pestaña debug lanza un Chrome debug
 * detener: crtl + c en terminal
 */

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';//VIENE CON ANGULAR

import { RouterModule }   from '@angular/router';//lo agrega el developer
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { HomeComponent } from './home.component';
import { FooComponent } from './foo.component';
import { EmpComponent } from './emp.component';

/**
 * AppModule where we wrap all our components, services and routes.
 */

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    FooComponent,
    EmpComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    //AppRoutingModule,
    RouterModule.forRoot(//ESTO NO SÉ QUÉ HACE, lo puso el developer
      [{ path: '', component: HomeComponent, pathMatch: 'full' }], {onSameUrlNavigation: 'reload'}
      )
  ],
  providers: [], //los tipos que indique aquí, serán inyectables dentro del constructor de esta clase (en este caso no tenemos constructor) (dependency injection)
  bootstrap: [AppComponent] //inicializa este componente al arrancar este módulo
}) //NOS VAMOS A AppComponent !!!
export class AppModule { }
