import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

//// ESTO LO TRAE ANGULAR, PARA MANEJAR RUTAS DE LA APLICACIÓN, NOTAR QUE ES UN MÓDULO

const routes: Routes = [
  //[{ path: '', component: HomeComponent, pathMatch: 'full' }], {onSameUrlNavigation: 'reload'}
];
//routes.push({ path: '', component: HomeComponent, pathMatch: 'full' }], {onSameUrlNavigation: 'reload'});

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
