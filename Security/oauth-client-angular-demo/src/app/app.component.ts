import { Component } from '@angular/core';

@Component({
  selector: 'app-root', //el nombre de este componente.. con este será embebible en html de Angular usando <app-root></app-root>
  //templateUrl: './app.component.html', //nos abre la página de bienvenida de nuevo proyecto Angular
      //así le decimos que cargue dicho html, con la css de abajo.. es html de Angular, no html simple
  //styleUrls: ['./app.component.css'],
  //... pero preferimos cargarlo directo aquí pq es poco código!
  template: //lo nuestro
  `<nav class="navbar navbar-default">
    <div class="container-fluid">
      <div class="navbar-header">
        <a class="navbar-brand" href="/">Spring Security Oauth - Authorization Code</a>
      </div>
    </div>
  </nav>
  <!--<router-outlet></router-outlet>-->
  <home-header></home-header>`//ESTO ES QUE CARGUE UN COMPONENTE CON ESE NOMBRE
}) //NOS VAMOS A HomeComponent
export class AppComponent {
  title = 'oauth-client-angular-demo';
}
