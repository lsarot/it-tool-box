import {Component} from '@angular/core';
import {AppService} from './app.service'
 
/**
 * We're going to use the OAuth2 Authorization Code flow here.
 *
 * Our use case is: the client app requests a code from the Authorization Server and is presented with a login page. 
 * Once a user provides their valid credentials and submits, the Authorization Server gives us the code. 
 * Then the front-end client uses it to acquire an access token.
 * 
 * HERE STARTS ALL THE ACTION!
 * 
 * In the beginning, when the user is not logged in, only the login button appears. 
 * On clicking this button, the user will be navigated to the AS's authorization URL where they key in username and password. 
 * After a successful login, user is redirected back with the authorization code and then we retrieve the access token using this code.
 */

@Component({
    selector: 'home-header',
    providers: [AppService],//lo haremos inyectable en el constructor de la clase (mirar abajo el constructor)
                            //parece que si proviene de un @angular/ no hace falta ponerlo aquí y ya será inyectable!
  template: //este 'html' Angular usa elementos de Angular especiales (if, for, while, etc.) (*ngIf, [ngSwitch], (click), etc.)
  `<div class="container" >
        <button *ngIf="!isLoggedIn" class="btn btn-primary" (click)="login()" type="submit">Login</button>
        <div *ngIf="isLoggedIn" class="content">
            <span><h3>Welcome !!</h3></span>
            <a class="btn btn-default pull-right" (click)="logout()" href="#">Logout</a>
            <br/>
            <foo-details></foo-details><!--carga este componente si se cumple el if-->
            <br/>
            <emp-details></emp-details>
        </div>
    </div>`
})
 
export class HomeComponent {

    public isLoggedIn = false;

    constructor(
        private _service:AppService) //dependency injection
    {}
 
    /**
     * LifeCycle method for all components
     */
    ngOnInit(){
        this.isLoggedIn = this._service.checkCredentials(); //check if access-token is stored on cookies for this site   
        let i = window.location.href.indexOf('code');
        if(!this.isLoggedIn && i != -1) {//si no está loggedin Y SI hay &code=abc en la url, recupera el token de la url misma, y lo guarda en una cookie
            this._service.retrieveToken(window.location.href.substring(i + 5));
        }
    }

    /**
     * hace un llamado al servidor de authorización OAuth
     */
    login() {
        let params = new URLSearchParams();   
        params.append('response_type','code');
        params.append('scope', 'write read');
        params.append('client_id', this._service.clientId);
        params.append('redirect_uri', this._service.redirectUri);

        window.location.href = this._service.authServerBaseUri + '/auth?' + params.toString();
    }
 
    /**
     * elimina la cookie y recarga, cayendo en onInit nuevamente
     * 
     * ALGO NO FUNCIONA BIEN EN EL BOTÓN DE LOGOUT,
     * si vuelvo a hacer Login, carga la url con el code=.. y piensa que es que está accediendo nuevamente. Nos damos cuenta al debugear.
     * y no es que guarde otra cookie en el navegador, ni es la session pq no la guarda.
     */
    logout() {
        this._service.logout();
    }
}